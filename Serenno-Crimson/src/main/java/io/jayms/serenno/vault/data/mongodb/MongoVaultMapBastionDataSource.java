package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.vault.VaultMapDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MongoVaultMapBastionDataSource implements BastionDataSource {

    public static final String COLLECTION = "bastion";

    public static final String REINFORCEMENT_ID = "reinforcement_id";
    public static final String BLUEPRINT = "blueprint";

    private VaultMapDatabase db;

    public MongoVaultMapBastionDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION));
    }

    @Override
    public Bastion fromDocumentKey(Reinforcement key, Document doc) {
        Bastion b = new Bastion(key, db.getBastionBlueprintSource().get(doc.getString(BLUEPRINT)));
        return b;
    }

    @Override
    public Bastion fromDocument(ReinforcementWorld world, Document doc) {
        UUID reinID = UUID.fromString(doc.getString(REINFORCEMENT_ID));
        ReinforcementWorld reinforcementWorld = world != null ? world : db.getVaultMap().getReinforcementWorld();
        Reinforcement reinforcement = reinforcementWorld.getReinforcement(reinID);
        return fromDocumentKey(reinforcement, doc);
    }

    @Override
    public Document toDocument(Bastion value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getReinforcementID().toString());
        doc.append(BLUEPRINT, value.getBlueprint().getName());
        return doc;
    }

    @Override
    public Bson getFilter(Reinforcement key) {
        return Filters.eq(REINFORCEMENT_ID, key.getID().toString());
    }

    public Bson getFilter(Bastion key) {
        return Filters.eq(REINFORCEMENT_ID, key.getReinforcementID().toString());
    }

    @Override
    public void create(Bastion value) {
        if (exists(value)) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Bastion value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value), doc);
    }

    @Override
    public Bastion get(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        Document doc = query.first();
        return fromDocumentKey(key, doc);
    }

    @Override
    public boolean exists(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    public boolean exists(Bastion key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Bastion> getAll(ReinforcementWorld world) {
        MongoCollection<Document> collection = getCollection();
        Collection<Bastion> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(world, doc));
        }

        return result;
    }

    @Override
    public void delete(Bastion value) {
        getCollection().deleteOne(getFilter(value));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

    @Override
    public void persistAll(ReinforcementWorld world, Collection<Bastion> bastions, ReinforcementWorld.UnloadCallback callback) {
        if (bastions.isEmpty()) {
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                List<Document> toInsert = new ArrayList<>();
                List<Bastion> toRemove = new ArrayList<>();
                for (Bastion bastion : bastions) {
                    Reinforcement reinforcement = bastion.getReinforcement(world);
                    if (reinforcement == null) {
                        break;
                    }
                    if (!reinforcement.isDirty()) {
                        continue;
                    }
                    if (reinforcement.isBroken()) {
                        toRemove.add(bastion);
                    } else {
                        toInsert.add(toDocument(bastion));
                    }
                }
                MongoCollection<Document> collection = getCollection();
                if (!toRemove.isEmpty()) {
                    Bson deleteFilter = Filters.all(REINFORCEMENT_ID, toRemove.stream()
                            .map(b -> b.getReinforcementID().toString())
                            .collect(Collectors.toList()));
                    collection.deleteMany(deleteFilter);
                }

                if (!toInsert.isEmpty()) {
                    collection.insertMany(toInsert);
                }
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        callback.unload();
                    }
                }.runTask(SerennoCobalt.get());
            }

        }.runTaskAsynchronously(SerennoCobalt.get());
    }
}
