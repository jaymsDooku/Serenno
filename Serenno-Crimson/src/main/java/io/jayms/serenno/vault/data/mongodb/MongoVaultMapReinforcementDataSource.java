package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.BulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;
import io.jayms.serenno.vault.VaultMapDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MongoVaultMapReinforcementDataSource implements ReinforcementDataSource {

    public static final String COLLECTION = "reinforcement";

    public static final String REINFORCEMENT_ID = "reinforcement_id";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String CX = "cx";
    public static final String CZ = "cz";
    public static final String BLUEPRINT = "blueprint";
    public static final String CITADEL_GROUP = "citadel_group";
    public static final String CREATION_TIME = "creation_time";
    public static final String HEALTH = "health";

    private VaultMapDatabase db;

    public MongoVaultMapReinforcementDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    public VaultMapDatabase getDb() {
        return db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION),
                (c) -> {
                    c.createIndex(Indexes.ascending(CX, CZ));
                });
        return collection;
    }

    @Override
    public Reinforcement fromDocument(Document doc) {
        Reinforcement r = Reinforcement.builder()
                .id(UUID.fromString(doc.getString(REINFORCEMENT_ID)))
                .loc(new Location(db.getWorld(), doc.getDouble(X), doc.getDouble(Y), doc.getDouble(Z)))
                .chunkX(doc.getInteger(CX))
                .chunkZ(doc.getInteger(CZ))
                .creationTime(doc.getLong(CREATION_TIME))
                .group(doc.getString(CITADEL_GROUP))
                .blueprint(db.getReinforcementBlueprintSource().get(doc.getString(BLUEPRINT)))
                .health(doc.getDouble(HEALTH))
                .inMemory(false)
                .build();
        return r;
    }

    @Override
    public Document toDocument(Reinforcement value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getID().toString());
        doc.append(BLUEPRINT, value.getBlueprint().getName());
        doc.append(X, value.getLocation().getX());
        doc.append(Y, value.getLocation().getY());
        doc.append(Z, value.getLocation().getZ());
        doc.append(CX, value.getChunkX());
        doc.append(CZ, value.getChunkZ());
        doc.append(BLUEPRINT, value.getBlueprint().getName());
        Group group = value.getGroup();
        if (group == null) {
            System.out.println("GROUP IS NULL | " + value.getGroupName());
        }
        doc.append(CITADEL_GROUP, group.getName().toLowerCase());
        doc.append(CREATION_TIME, value.getCreationTime());
        doc.append(HEALTH, value.getHealth());
        return doc;
    }

    @Override
    public Bson getFilter(Coords key) {
        return Filters.and(Filters.eq(X, key.getX()),
                Filters.eq(Y, key.getY()),
                Filters.eq(Z, key.getZ()));
    }

    @Override
    public void create(Reinforcement value) {
        if (exists(Coords.fromLocation(value.getLocation()))) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Reinforcement value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(Coords.fromLocation(value.getLocation())), doc);
    }

    public Reinforcement get(UUID key) {
        FindIterable<Document> query = getCollection().find(Filters.eq(REINFORCEMENT_ID, key.toString()));
        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        return fromDocument(doc);
    }

    @Override
    public Reinforcement get(Coords key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));

        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        return fromDocument(doc);
    }

    @Override
    public boolean exists(Coords key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Reinforcement> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<Reinforcement> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(Reinforcement value) {
        getCollection().deleteOne(getFilter(Coords.fromLocation(value.getLocation())));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    public void persistAll(Collection<Reinforcement> reinforcements, ReinforcementWorld.UnloadCallback callback) {
        if (reinforcements.isEmpty()) {
            return;
        }

        executorService.execute(new MongoReinforcementPersistTask(this, reinforcements, 30000, callback));
    }

    public void dispose() {
        executorService.shutdown();
    }

    @Override
    public boolean isAcidBlock(Material type) {
        return type == Material.GOLD_BLOCK;
    }

    @Override
    public Map<Coords, Reinforcement> getAll(ChunkCoord coord) {
        Map<Coords, Reinforcement> all = new HashMap<>();
        FindIterable<Document> query = getCollection().find(Filters.and(Filters.eq(CX, coord.getX()),
                Filters.eq(CZ, coord.getZ())));

        for (Document doc : query) {
            Reinforcement reinforcement = fromDocument(doc);
            all.put(Coords.fromLocation(reinforcement.getLocation()), reinforcement);
        }
        return all;
    }
}
