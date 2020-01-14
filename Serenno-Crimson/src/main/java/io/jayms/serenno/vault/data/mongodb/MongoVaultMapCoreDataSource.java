package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
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

public class MongoVaultMapCoreDataSource implements MongoSerennoDataSource<Core, Reinforcement> {

    public static final String COLLECTION = "core";

    public static final String REINFORCEMENT_ID = "reinforcement_id";
    public static final String TEAM_COLOR = "team_color";

    private VaultMapDatabase db;
    private Map<UUID, Core> cores = new ConcurrentHashMap<>();

    public MongoVaultMapCoreDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION));
    }

    @Override
    public Core fromDocument(Document doc) {
        Core core = new Core(db,
                ChatColor.valueOf(doc.getString(TEAM_COLOR)),
                db.getReinforcementSource().get(UUID.fromString(doc.getString(REINFORCEMENT_ID))));
        return core;
    }

    @Override
    public Document toDocument(Core value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getReinforcement().getID().toString());
        doc.append(TEAM_COLOR, value.getTeamColor().toString());
        return doc;
    }

    @Override
    public Bson getFilter(Reinforcement key) {
        return Filters.eq(REINFORCEMENT_ID, key.getID().toString());
    }

    @Override
    public void create(Core value) {
        if (exists(value.getReinforcement())) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Core value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value.getReinforcement()), doc);
    }

    @Override
    public Core get(Reinforcement key) {
        UUID uuid = key.getID();
        if (cores.containsKey(uuid)) {
            return cores.get(uuid);
        }

        FindIterable<Document> query = getCollection().find(getFilter(key));

        Document doc = query.first();
        if (doc == null) {
            return null;
        }

        Core core = fromDocument(doc);
        cores.put(uuid, core);
        return core;
    }

    @Override
    public boolean exists(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Core> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<Core> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(Core value) {
        getCollection().deleteOne(getFilter(value.getReinforcement()));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

}
