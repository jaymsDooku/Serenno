package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.DBKey;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.vault.VaultMapDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MongoVaultMapReinforcementBlueprintDataSource implements MongoSerennoDataSource<ReinforcementBlueprint, ItemStack> {

    public static final String COLLECTION = "reinforcement-blueprint";

    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "display_name";
    public static final String ITEM_STACK_MATERIAL = "item_stack_material";
    public static final String ITEM_STACK_AMOUNT = "item_stack_amount";
    public static final String REGEN_RATE_AMOUNT = "regen_rate_amount";
    public static final String REGEN_RATE_INTERVAL = "regen_rate_interval";
    public static final String HEALTH = "health";
    public static final String MATURATION_TIME = "maturation_time";
    public static final String MATURATION_SCALE = "maturation_scale";
    public static final String ACID_TIME = "acid_time";
    public static final String DAMAGE_COOLDOWN = "damage_cooldown";
    public static final String DEFAULT_DAMAGE = "default_damage";
    public static final String REINFORCEABLE_MATERIAL = "reinforceable-materials";
    public static final String UNREINFORCEABLE_MATERIAL = "unreinforceable-materials";

    private VaultMapDatabase db;
    private Map<String, ReinforcementBlueprint> reinforcementBlueprintMap = new ConcurrentHashMap<>();
    private Map<ItemStackKey, ReinforcementBlueprint> itemStackKeyReinforcementBlueprintMap = new ConcurrentHashMap<>();

    public MongoVaultMapReinforcementBlueprintDataSource(VaultMapDatabase db) {
        this.db = db;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return SerennoCommon.get().getDBManager().getCollection(new DBKey(db.getWorldName(), COLLECTION));
    }

    @Override
    public ReinforcementBlueprint fromDocument(Document doc) {
        String displayName = doc.getString(DISPLAY_NAME);

        List<Material> reinforceableMaterials = doc.getList(REINFORCEABLE_MATERIAL, String.class).stream()
                .map(m -> Material.valueOf(m))
                .collect(Collectors.toList());
        List<Material> unreinforceableMaterials = doc.getList(UNREINFORCEABLE_MATERIAL, String.class).stream()
                .map(m -> Material.valueOf(m))
                .collect(Collectors.toList());

        ReinforcementBlueprint bb = ReinforcementBlueprint.builder()
                .name(doc.getString(NAME))
                .displayName(displayName)
                .itemStack(new ItemStackBuilder(Material.valueOf(doc.getString(ITEM_STACK_MATERIAL)), doc.getInteger(ITEM_STACK_AMOUNT))
                        .meta(new ItemMetaBuilder()
                                .name(displayName))
                        .build())
                .regenRate(new RegenRate(doc.getDouble(REGEN_RATE_AMOUNT), doc.getLong(REGEN_RATE_INTERVAL)))
                .maxHealth(doc.getDouble(HEALTH))
                .maturationTime(doc.getLong(MATURATION_TIME))
                .maturationScale(doc.getDouble(MATURATION_SCALE))
                .acidTime(doc.getLong(ACID_TIME))
                .damageCooldown(doc.getLong(DAMAGE_COOLDOWN))
                .defaultDamage(doc.getDouble(DEFAULT_DAMAGE))
                .reinforceableMaterials(reinforceableMaterials)
                .unreinforceableMaterials(unreinforceableMaterials)
                .build();
        return bb;
    }

    @Override
    public Document toDocument(ReinforcementBlueprint value) {
        Document doc = new Document();
        doc.append(NAME, value.getName());
        doc.append(DISPLAY_NAME, value.getDisplayName());
        doc.append(ITEM_STACK_MATERIAL, value.getItemStack().getType().toString());
        doc.append(ITEM_STACK_AMOUNT, value.getItemStack().getAmount());
        doc.append(REGEN_RATE_AMOUNT, value.getRegenRate().getAmount());
        doc.append(REGEN_RATE_INTERVAL, value.getRegenRate().getInterval());
        doc.append(HEALTH, value.getMaxHealth());
        doc.append(MATURATION_TIME, value.getMaturationTime());
        doc.append(MATURATION_SCALE, value.getMaturationScale());
        doc.append(ACID_TIME, value.getAcidTime());
        doc.append(DAMAGE_COOLDOWN, value.getDamageCooldown());
        doc.append(DEFAULT_DAMAGE, value.getDefaultDamage());
        List<String> reinforceableMaterials = value.getReinforceableMaterials().stream()
                .map(m -> m.name())
                .collect(Collectors.toList());
        doc.append(REINFORCEABLE_MATERIAL, reinforceableMaterials);
        List<String> unreinforceableMaterials = value.getUnreinforceableMaterials().stream()
                .map(m -> m.name())
                .collect(Collectors.toList());
        doc.append(UNREINFORCEABLE_MATERIAL, unreinforceableMaterials);
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
    public void create(ReinforcementBlueprint value) {
        if (exists(value.getItemStack())) {
            return;
        }

        Document doc = toDocument(value);
        getCollection().insertOne(doc);
    }

    @Override
    public void update(ReinforcementBlueprint value) {
        Document doc = toDocument(value);
        getCollection().replaceOne(getFilter(value.getItemStack()), doc);
    }

    public ReinforcementBlueprint get(String key) {
        if (reinforcementBlueprintMap.containsKey(key)) {
            return reinforcementBlueprintMap.get(key);
        }

        FindIterable<Document> query = getCollection().find(Filters.eq(NAME, key));
        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        ReinforcementBlueprint blueprint = fromDocument(doc);
        reinforcementBlueprintMap.put(key, blueprint);
        return blueprint;
    }

    @Override
    public ReinforcementBlueprint get(ItemStack key) {
        ItemStackKey itemStackKey = new ItemStackKey(key);
        if (itemStackKeyReinforcementBlueprintMap.containsKey(itemStackKey)) {
            return itemStackKeyReinforcementBlueprintMap.get(itemStackKey);
        }

        FindIterable<Document> query = getCollection().find(getFilter(key));
        Document doc = query.first();
        if (doc == null) {
            return null;
        }
        ReinforcementBlueprint blueprint = fromDocument(doc);
        itemStackKeyReinforcementBlueprintMap.put(itemStackKey, blueprint);
        return blueprint;
    }

    @Override
    public boolean exists(ItemStack key) {
        FindIterable<Document> query = getCollection().find(getFilter(key));
        return query.first() != null;
    }

    @Override
    public Collection<ReinforcementBlueprint> getAll() {
        MongoCollection<Document> collection = getCollection();
        Collection<ReinforcementBlueprint> result = new ArrayList<>();

        FindIterable<Document> allDocs = collection.find();

        MongoCursor<Document> cursor = allDocs.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            result.add(fromDocument(doc));
        }

        return result;
    }

    @Override
    public void delete(ReinforcementBlueprint value) {
        getCollection().deleteOne(getFilter(value.getItemStack()));
    }

    @Override
    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }
}
