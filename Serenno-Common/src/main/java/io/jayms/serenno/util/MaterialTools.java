package io.jayms.serenno.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class MaterialTools {

	private static final List<Material> HELMETS = Arrays.asList(Material.DIAMOND_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET);
	private static final List<Material> CHESTS = Arrays.asList(Material.DIAMOND_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE);
	private static final List<Material> LEGS = Arrays.asList(Material.DIAMOND_LEGGINGS, Material.GOLD_LEGGINGS, Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS);
	private static final List<Material> BOOTS = Arrays.asList(Material.DIAMOND_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS);
	private static final List<Material> SWORDS = Arrays.asList(Material.DIAMOND_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD, Material.IRON_SWORD);
	
	public static boolean isHelmet(ItemStack it) {
		if (it == null) return false;
		return isHelmet(it.getType());
	}
	
	public static boolean isHelmet(Material mat) {
		return HELMETS.contains(mat);
	}
	
	public static boolean isChestplate(ItemStack it) {
		if (it == null) return false;
		return isChestplate(it.getType());
	}
	
	public static boolean isChestplate(Material mat) {
		return CHESTS.contains(mat);
	}
	
	public static boolean isLeggings(ItemStack it) {
		if (it == null) return false;
		return isLeggings(it.getType());
	}
	
	public static boolean isLeggings(Material mat) {
		return LEGS.contains(mat);
	}
	
	public static boolean isBoots(ItemStack it) {
		if (it == null) return false;
		return isBoots(it.getType());
	}
	
	public static boolean isBoots(Material mat) {
		return BOOTS.contains(mat);
	}
	
	public static boolean isArmour(ItemStack it) {
		return isArmour(it.getType());
	}
	
	public static boolean isArmour(Material mat) {
		return isHelmet(mat) || isChestplate(mat) || isLeggings(mat) || isBoots(mat);
	}
	
	public static boolean isSword(ItemStack it) {
		return isSword(it.getType());
	}
	
	public static boolean isSword(Material mat) {
		return SWORDS.contains(mat);
	}
	
	public static boolean isPotion(ItemStack it) {
		Material type = it.getType();
		return type == Material.POTION || type == Material.LINGERING_POTION || type == Material.SPLASH_POTION;
	}
	
	public static boolean isChest(Material mat) {
		return mat == Material.CHEST || mat == Material.TRAPPED_CHEST;
	}
	
	public static boolean isChest(Block block) {
		return isChest(block.getType());
	}
}
