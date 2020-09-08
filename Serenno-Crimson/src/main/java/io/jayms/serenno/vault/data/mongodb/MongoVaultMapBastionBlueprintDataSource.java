package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.SerennoDataSource;
import io.jayms.serenno.vault.VaultMapDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoVaultMapBastionBlueprintDataSource implements MongoSerennoDataSource<BastionBlueprint, ItemStack> {

    public static final String COLLECTION = "bastion-blueprint";

    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "display_name";
    public static final String ITEM_STACK_MATERIAL = "item_stack_material";
    public static final String ITEM_STACK_AMOUNT = "item_stack_amount";
    public static final String ITEM_STACK_NAME = "item_stack_name";
    public static final String BASTION_SHAPE = "bastion_shape";
    public static final String RADIUS = "radius";
    public static final String REQUIRES_MATURITY = "requires_maturity";
    public static final String PEARL_BLOCK = "pearl_block";
    public static final String PEARL_BLOCK_MID_AIR = "pearl_block_mid_air";
    public static final String PEARL_CONSUME_ON_BLOCK = "pearl_consume_on_block";
    public static final String PEARL_DAMAGE = "pearl_damage";

    private VaultMapDatabase db;
    private Map<String, BastionBlueprint> bastionBlueprintMap = new ConcurrentHashMap<>();
    private Map<ItemStackKey, BastionBlueprint> itemStackKeyBastionBlueprintMap = new ConcurrentHashMap<>();

    public MongoVaultMapBastionBlueprintDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION));
    }

    @Override
    public BastionBlueprint fromDocument(Document doc) {
        String displayName = doc.getString(DISPLAY_NAME);

        BastionBlueprint bb = BastionBlueprint.builder()
                .name(doc.getString(NAME))
                .displayName(displayName)
                .itemStack(new ItemStackBuilder(Material.valueOf(doc.getString(ITEM_STACK_MATERIAL)), doc.getInteger(ITEM_STACK_AMOUNT))
                        .meta(new ItemMetaBuilder()
                                .name(doc.getString(ITEM_STACK_NAME)))
                        .build())
                .shape(BastionShape.valueOf(doc.getString(BASTION_SHAPE)))
                .radius(doc.getInteger(RADIUS))
                .requiresMaturity(doc.getBoolean(REQUIRES_MATURITY))
                .pearlConfig(BastionBlueprint.PearlConfig.builder()
                        .block(doc.getBoolean(PEARL_BLOCK))
                        .blockMidAir(doc.getBoolean(PEARL_BLOCK_MID_AIR))
                        .consumeOnBlock(doc.getBoolean(PEARL_CONSUME_ON_BLOCK))
                        .damage(doc.getDouble(PEARL_DAMAGE))
                        .build())
                .build();
        return bb;
    }

    @Override
    public Document toDocument(BastionBlueprint value) {
        Document doc = new Document();
        doc.append(NAME, value.getName());
        doc.append(DISPLAY_NAME, value.getDisplayName());
        doc.append(ITEM_STACK_MATERIAL, value.getItemStack().getType().toString());
        doc.append(ITEM_STACK_AMOUNT, value.getItemStack().getAmount());
        doc.append(ITEM_STACK_NAME, ItemUtil.getName(value.getItemStack()));
        doc.append(BASTION_SHAPE, value.getShape().toString());
        doc.append(RADIUS, value.getRadius());
        doc.append(REQUIRES_MATURITY, value.requiresMaturity());
        doc.append(PEARL_BLOCK, value.getPearlConfig().block());
        doc.append(PEARL_BLOCK_MID_AIR, value.getPearlConfig().blockMidAir());
        doc.append(PEARL_CONSUME_ON_BLOCK, value.getPearlConfig().consumeOnBlock());
        doc.append(PEARL_DAMAGE, value.getPearlConfig().getDamage());
        return doc;
    }

    @Override
    public Bson getFilter(ItemStack key) {
        return Filters.and(
                Filters.eq(ITEM_STACK_MATERIAL, key.getType().toString()),
                Filters.eq(ITEM_STACK_AMOUNT, key.getAmount())
        );
    }

    @Override
    public void create(BastionBlueprint value) {
        if (exists(value.getItemStack())) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(BastionBlueprint value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value.getItemStack()), doc);
    }

    public BastionBlueprint get(String key) {
        if (bastionBlueprintMap.containsKey(key)) {
            return bastionBlueprintMap.get(key);
        }

        FindIterable<Document> query = getCollection().find(Filters.eq(NAME, key));
        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        BastionBlueprint blueprint = fromDocument(doc);
        bastionBlueprintMap.put(key, blueprint);
        return blueprint;
    }

    @Override
    public BastionBlueprint get(ItemStack key) {
        ItemStackKey itemStackKey = new ItemStackKey(key);
        if (itemStackKeyBastionBlueprintMap.containsKey(itemStackKey)) {
            return itemStackKeyBastionBlueprintMap.get(itemStackKey);
        }

        FindIterable<Document> query = getCollection().find(getFilter(key));
        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        BastionBlueprint blueprint = fromDocument(doc);
        itemStackKeyBastionBlueprintMap.put(itemStackKey, blueprint);
        return blueprint;
    }

    @Override
    public boolean exists(ItemStack key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<BastionBlueprint> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<BastionBlueprint> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(BastionBlueprint value) {
        getCollection().deleteOne(getFilter(value.getItemStack()));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

}
