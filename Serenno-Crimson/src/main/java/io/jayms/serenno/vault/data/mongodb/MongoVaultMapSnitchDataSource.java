package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.BulkWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchDataSource;
import io.jayms.serenno.vault.Core;
import io.jayms.serenno.vault.VaultMapDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MongoVaultMapSnitchDataSource implements SnitchDataSource {

    public static final String COLLECTION = "snitch";

    public static final String REINFORCEMENT_ID = "reinforcement_id";
    public static final String NAME = "name";
    public static final String RADIUS = "radius";

    private VaultMapDatabase db;
    private Map<UUID, Snitch> snitches = new ConcurrentHashMap<>();

    public MongoVaultMapSnitchDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION));
    }

    @Override
    public Snitch fromDocumentKey(Reinforcement key, Document doc) {
        Snitch snitch = new Snitch(key,
                doc.getString(NAME),
                doc.getInteger(RADIUS));
        return snitch;
    }

    @Override
    public Snitch fromDocument(ReinforcementWorld world, Document doc) {
        UUID reinID = UUID.fromString(doc.getString(REINFORCEMENT_ID));
        ReinforcementWorld reinforcementWorld = world != null ? world : db.getVaultMap().getReinforcementWorld();
        Reinforcement reinforcement = reinforcementWorld.getReinforcement(reinID);
        if (reinforcement == null) {
            return null;
        }
        return  fromDocumentKey(reinforcement, doc);
    }

    @Override
    public Document toDocument(Snitch value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getReinforcementID().toString());
        doc.append(NAME, value.getName());
        doc.append(RADIUS, value.getRadius());
        return doc;
    }

    @Override
    public Bson getFilter(Reinforcement key) {
        return Filters.eq(REINFORCEMENT_ID, key.getID().toString());
    }

    public Bson getFilter(Snitch value) {
        return Filters.eq(REINFORCEMENT_ID, value.getReinforcementID().toString());
    }

    @Override
    public void create(Snitch value) {
        if (exists(value)) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Snitch value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value), doc);
    }

    @Override
    public Snitch get(Reinforcement key) {
        UUID uuid = key.getID();
        if (snitches.containsKey(uuid)) {
            return snitches.get(uuid);
        }

        FindIterable<Document> query = getCollection().find(getFilter(key));

        Document doc = query.first();
        if (doc == null) {
            return null;
        }

        Snitch snitch = fromDocumentKey(key, doc);
        snitches.put(uuid, snitch);
        return snitch;
    }

    @Override
    public boolean exists(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    public boolean exists(Snitch key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Snitch> getAll(ReinforcementWorld world) {
        MongoCollection<Document> collection = getCollection();
        Collection<Snitch> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        List<String> toRemove = new ArrayList<>();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            Snitch snitch = fromDocument(world, doc);
            if (snitch == null) {
                toRemove.add(doc.getString(REINFORCEMENT_ID));
                continue;
            }
            result.add(snitch);
        }

        deleteMany(toRemove);

        return result;
    }

    @Override
    public void delete(Snitch value) {
        getCollection().deleteOne(getFilter(value));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

    private void deleteMany(List<String> toRemove) {
        MongoCollection<Document> collection = getCollection();
        if (!toRemove.isEmpty()) {
            Bson deleteFilter = Filters.all(REINFORCEMENT_ID, toRemove);
            collection.deleteMany(deleteFilter);
        }
    }

    @Override
    public void persistAll(ReinforcementWorld world, Collection<Snitch> snitches, ReinforcementWorld.UnloadCallback callback) {
        if (snitches.isEmpty()) {
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                List<Document> toInsert = new ArrayList<>();
                List<Snitch> toRemove = new ArrayList<>();
                for (Snitch snitch : snitches) {
                    Reinforcement reinforcement = snitch.getReinforcement(world);
                    if (reinforcement == null) {
                        break;
                    }
                    if (!reinforcement.isDirty()) {
                        continue;
                    }
                    if (reinforcement.isBroken()) {
                        toRemove.add(snitch);
                    } else {
                        toInsert.add(toDocument(snitch));
                    }
                }
                deleteMany(toRemove.stream()
                        .map(s -> s.getReinforcementID().toString())
                        .collect(Collectors.toList()));

                MongoCollection<Document> collection = getCollection();
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
