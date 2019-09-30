package io.jayms.serenno.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.google.common.collect.ImmutableMap;

import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.Item;

public class ItemUtil {
	
	private static final List<Material> HELMET = Arrays.asList(Material.DIAMOND_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET);
	private static final List<Material> CHEST = Arrays.asList(Material.DIAMOND_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.ELYTRA);
	private static final List<Material> LEGS = Arrays.asList(Material.DIAMOND_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLD_LEGGINGS, Material.LEATHER_LEGGINGS);
	private static final List<Material> BOOTS = Arrays.asList(Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS);

	private static final Map<Material, Armour> DEFAULT_ARMOUR = ImmutableMap.<Material, Armour>builder()
			.put(Material.LEATHER_BOOTS, new Armour(0, 1))
			.put(Material.LEATHER_LEGGINGS, new Armour(0, 2))
			.put(Material.LEATHER_CHESTPLATE, new Armour(0, 3))
			.put(Material.LEATHER_HELMET, new Armour(0, 1))
			.put(Material.GOLD_BOOTS, new Armour(0, 1))
			.put(Material.GOLD_LEGGINGS, new Armour(0, 3))
			.put(Material.GOLD_CHESTPLATE, new Armour(0, 5))
			.put(Material.GOLD_HELMET, new Armour(0, 2))
			.put(Material.CHAINMAIL_BOOTS, new Armour(0, 1))
			.put(Material.CHAINMAIL_LEGGINGS, new Armour(0, 4))
			.put(Material.CHAINMAIL_CHESTPLATE, new Armour(0, 5))
			.put(Material.CHAINMAIL_HELMET, new Armour(0, 2))
			.put(Material.IRON_BOOTS, new Armour(0, 2))
			.put(Material.IRON_LEGGINGS, new Armour(0, 5))
			.put(Material.IRON_CHESTPLATE, new Armour(0, 6))
			.put(Material.IRON_HELMET, new Armour(0, 2))
			.put(Material.DIAMOND_BOOTS, new Armour(2, 3))
			.put(Material.DIAMOND_LEGGINGS, new Armour(2, 6))
			.put(Material.DIAMOND_CHESTPLATE, new Armour(2, 8))
			.put(Material.DIAMOND_HELMET, new Armour(2, 3))
			.build();
	
	public static int getDefaultArmourToughness(ItemStack is) {
		if (!(DEFAULT_ARMOUR.containsKey(is.getType()))) {
			return 0;
		}
		
		return DEFAULT_ARMOUR.get(is.getType()).getToughness();
	}
	
	public static int getDefaultArmour(ItemStack is) {
		if (!(DEFAULT_ARMOUR.containsKey(is.getType()))) {
			return 0;
		}
		
		return DEFAULT_ARMOUR.get(is.getType()).getArmour();
	}
	
	public static boolean isArmour(ItemStack is) {
		Material type = is.getType();
		return HELMET.contains(type) || CHEST.contains(type) || LEGS.contains(type) || BOOTS.contains(type);
	}
	
	public static ItemStack setArmour(ItemStack is, int toughness, int armor) {
		String slot = null;
		int uidLeast = 1;
		int uidMost = 1;
		if (HELMET.contains(is.getType())) {
			slot = "head";
		} else if (CHEST.contains(is.getType())) {
			slot = "chest";
			uidMost = 2;
		} else if (LEGS.contains(is.getType())) {
			slot = "legs";
			uidLeast = 2;
		} else if (BOOTS.contains(is.getType())) {
			slot = "feet";
			uidMost = 2;
			uidLeast = 2;
		}
		
		NBTItem nbtItem = new NBTItem(is);
		NBTCompoundList attribModifiers = nbtItem.getCompoundList("AttributeModifiers");
		
		if (toughness != -1) {
			NBTListCompound toughnessNbt = attribModifiers.addCompound();
			toughnessNbt.setString("AttributeName", "generic.armorToughness");
			toughnessNbt.setString("Name", "generic.armorToughness");
			toughnessNbt.setInteger("Operation", 0);
			toughnessNbt.setString("Slot", slot);
			toughnessNbt.setInteger("Amount", toughness);
			toughnessNbt.setInteger("UUIDLeast", uidLeast);
			toughnessNbt.setInteger("UUIDMost", uidMost);
		}
		
		if (armor != -1) {
			NBTListCompound armorNbt = attribModifiers.addCompound();
			armorNbt.setString("AttributeName", "generic.armor");
			armorNbt.setString("Name", "generic.armor");
			armorNbt.setInteger("Operation", 0);
			armorNbt.setString("Slot", slot);
			armorNbt.setInteger("Amount", armor);
			armorNbt.setInteger("UUIDLeast", uidLeast);
			armorNbt.setInteger("UUIDMost", uidMost);
		}
		
		return nbtItem.getItem();
	}
	
	public static int getToughness(ItemStack it) {
		NBTItem nbtItem = new NBTItem(it);
		NBTCompoundList attribModifiers = nbtItem.getCompoundList("AttributeModifiers");
		for (NBTListCompound nbt : attribModifiers) {
			if (!nbt.hasKey("Name")) continue;
			if (nbt.getString("Name").equalsIgnoreCase("generic.armorToughness")) {
				return nbt.getInteger("Amount");
			}
		}
		return 0;
	}
	
	public static int getArmour(ItemStack it) {
		NBTItem nbtItem = new NBTItem(it);
		NBTCompoundList attribModifiers = nbtItem.getCompoundList("AttributeModifiers");
		for (NBTListCompound nbt : attribModifiers) {
			if (!nbt.hasKey("Name")) continue;
			if (nbt.getString("Name").equalsIgnoreCase("generic.armor")) {
				return nbt.getInteger("Amount");
			}
		}
		return 0;
	}
	
	public static ItemStack setWeapon(ItemStack is, double damage, double attackSpeed) {
		NBTItem nbtItem = new NBTItem(is);
		NBTCompoundList attribModifiers = nbtItem.getCompoundList("AttributeModifiers");
		
		if (damage != -1) {
			NBTListCompound attackDamageNbt = attribModifiers.addCompound();
			attackDamageNbt.setString("AttributeName", "generic.attackDamage");
			attackDamageNbt.setString("Name", "generic.attackDamage");
			attackDamageNbt.setInteger("Operation", 0);
			attackDamageNbt.setString("Slot", "mainhand");
			attackDamageNbt.setDouble("Amount", damage);
			attackDamageNbt.setInteger("UUIDLeast", 894654);
			attackDamageNbt.setInteger("UUIDMost", 2872);
		}
		
		if (attackSpeed != -1) {
			NBTListCompound attackSpeedNbt = attribModifiers.addCompound();
			attackSpeedNbt.setString("AttributeName", "generic.attackSpeed");
			attackSpeedNbt.setString("Name", "generic.attackSpeed");
			attackSpeedNbt.setInteger("Operation", 0);
			attackSpeedNbt.setString("Slot", "mainhand");
			attackSpeedNbt.setDouble("Amount", attackSpeed);
			attackSpeedNbt.setInteger("UUIDLeast", 894654);
			attackSpeedNbt.setInteger("UUIDMost", 2872);
		}
		
		return nbtItem.getItem();
	}
	
	public static ItemStack setMaxDurability(ItemStack is, int dura) {
		net.minecraft.server.v1_12_R1.ItemStack isNms = CraftItemStack.asNMSCopy(is);
		Item item = isNms.getItem();
		try {
			Method setMaxDura = Item.class.getMethod("setMaxDurability", int.class);
			setMaxDura.setAccessible(true);
			setMaxDura.invoke(item, dura);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return CraftItemStack.asBukkitCopy(isNms);
	}
	
	public static void setName(ItemStack it, String name) {
		ItemMeta meta = it.getItemMeta();
		meta.setDisplayName(name);
		it.setItemMeta(meta);
	}
	
	public static String getName(ItemStack it) {
		ItemMeta meta = it.getItemMeta();
		String name = it.getType().name();
		if (meta != null && meta.getDisplayName() != null) {
			name = meta.getDisplayName();
		}
		return name;
	}
	
	public static List<String> getLore(ItemStack it) {
		ItemMeta meta = it.getItemMeta();
		if (meta == null) return new ArrayList<>();
		return meta.getLore();
	}
	
	public static boolean isPotion(ItemStack it, PotionType type) {
		if (it == null) return false;
		
		ItemMeta meta = it.getItemMeta();
		if (meta instanceof PotionMeta) {
			PotionMeta potMeta = (PotionMeta) meta;
			if (potMeta.getBasePotionData().getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsEffect(List<PotionEffect> effects, PotionEffectType effect) {
		return effects.stream().filter(e -> e.getType() == effect).findFirst().isPresent();
	}
	
	public static String getPotionDurations(long duration) {
		long minutes = (duration / 1000) / 60;
		long seconds = (duration / 1000) % 60;
		return minutes + ":" + seconds;
	}
	
	public static String getEffectName(PotionEffectType type) {
		if (type.equals(PotionEffectType.SPEED)) {
			return ChatColor.AQUA + "Speed";
		} else if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
			return ChatColor.DARK_PURPLE + "Strength";
		} else if (type.equals(PotionEffectType.REGENERATION)) {
			return ChatColor.LIGHT_PURPLE + "Regeneration";
		} else if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
			return ChatColor.GOLD + "Fire Resistance";
		} else if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
			return ChatColor.DARK_GRAY + "Resistance";
		} else if (type.equals(PotionEffectType.INVISIBILITY)) {
			return ChatColor.GRAY + "Invisibility";
		} else if (type.equals(PotionEffectType.JUMP)) {
			return ChatColor.GREEN + "Jump Boost";
		} else if (type.equals(PotionEffectType.FAST_DIGGING)) {
			return ChatColor.YELLOW + "Haste";
		} else if (type.equals(PotionEffectType.BLINDNESS)) {
			return ChatColor.BLACK + "Blindness";
		} else if (type.equals(PotionEffectType.WEAKNESS)) {
			return ChatColor.DARK_GRAY + "Weakness";
		} else if (type.equals(PotionEffectType.SLOW)) {
			return ChatColor.GRAY + "Slownesss";
		} else if (type.equals(PotionEffectType.POISON)) {
			return ChatColor.DARK_GREEN + "Poison";
		} else if (type.equals(PotionEffectType.GLOWING)) {
			return ChatColor.YELLOW + "Spectral";
		} else if (type.equals(PotionEffectType.LEVITATION)) {
			return ChatColor.WHITE + "Levitation";
		} else if (type.equals(PotionEffectType.CONFUSION)) {
			return ChatColor.DARK_PURPLE + "Confusion";
		} else if (type.equals(PotionEffectType.ABSORPTION)) {
			return ChatColor.YELLOW + "Absorption";
		} else if (type.equals(PotionEffectType.HARM)) {
			return ChatColor.DARK_RED + "Harming";
		} else if (type.equals(PotionEffectType.HEAL)) {
			return ChatColor.RED + "Health";
		} else if (type.equals(PotionEffectType.HEALTH_BOOST)) {
			return ChatColor.WHITE + "Health Boost";
		} else if (type.equals(PotionEffectType.NIGHT_VISION)) {
			return ChatColor.DARK_BLUE + "Night Vision";
		} else if (type.equals(PotionEffectType.SLOW_DIGGING)) {
			return ChatColor.GRAY + "Fatigue";
		} else if (type.equals(PotionEffectType.HUNGER)) {
			return ChatColor.DARK_GREEN + "Hunger";
		} else if (type.equals(PotionEffectType.WITHER)) {
			return ChatColor.BLACK + "Wither";
		} else if (type.equals(PotionEffectType.LUCK)) {
			return ChatColor.GREEN + "Luck";
		} else if (type.equals(PotionEffectType.SATURATION)) {
			return ChatColor.GOLD + "Saturation";
		} else if (type.equals(PotionEffectType.UNLUCK)) {
			return ChatColor.DARK_GRAY + "Unluck";
		} else if (type.equals(PotionEffectType.WATER_BREATHING)) {
			return ChatColor.BLUE + "Water Breathing";
		}
		return "";
	}
	
}
