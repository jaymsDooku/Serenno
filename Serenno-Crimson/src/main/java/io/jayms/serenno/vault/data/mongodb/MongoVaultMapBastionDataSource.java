package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.vault.VaultMapDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MongoVaultMapBastionDataSource implements MongoBastionDataSource {

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
    public Bastion fromDocument(Document doc) {
        Bastion b = new Bastion(db.getReinforcementSource().get(UUID.fromString(doc.getString(REINFORCEMENT_ID))),
                db.getBastionBlueprintSource().get(doc.getString(BLUEPRINT)));
        return b;
    }

    @Override
    public Document toDocument(Bastion value) {
        Document doc = new Document();
        doc.append(REINFORCEMENT_ID, value.getReinforcement().getID().toString());
        doc.append(BLUEPRINT, value.getBlueprint().getName());
        return doc;
    }

    @Override
    public Bson getFilter(Reinforcement key) {
        return Filters.eq(REINFORCEMENT_ID, key.getID().toString());
    }

    @Override
    public void create(Bastion value) {
        if (exists(value.getReinforcement())) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(Bastion value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value.getReinforcement()), doc);
    }

    @Override
    public Bastion get(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));

        Document doc = query.first();

        return fromDocument(doc);
    }

    @Override
    public boolean exists(Reinforcement key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<Bastion> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<Bastion> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(Bastion value) {
        getCollection().deleteOne(getFilter(value.getReinforcement()));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

}
