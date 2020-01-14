package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.vault.Core;
import io.jayms.serenno.vault.VaultMapDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MongoVaultMapSnitchDataSource implements MongoSnitchDataSource {

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
    public Snitch fromDocument(Document doc) {
        Snitch snitch = new Snitch(db.getReinforcementSource().get(UUID.fromString(doc.getString(REINFORCEMENT_ID))),
                doc.getString(NAME),
                doc.getInteger(RADIUS));
        return snitch;
    }

    @Override
    public Document toDocument(Snitch value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getReinforcement().getID().toString());
        doc.append(NAME, value.getName());
        doc.append(RADIUS, value.getRadius());
        return doc;
    }

    @Override
    public Bson getFilter(Reinforcement key) {
        return Filters.eq(REINFORCEMENT_ID, key.getID().toString());
    }

    @Override
    public void create(Snitch value) {
        if (exists(value.getReinforcement())) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Snitch value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value.getReinforcement()), doc);
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

        Snitch snitch = fromDocument(doc);
        snitches.put(uuid, snitch);
        return snitch;
    }

    @Override
    public boolean exists(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Snitch> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<Snitch> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(Snitch value) {
        getCollection().deleteOne(getFilter(value.getReinforcement()));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

}
