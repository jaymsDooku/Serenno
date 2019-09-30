package io.jayms.serenno.game;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.Kit;
import net.md_5.bungee.api.ChatColor;

public enum DuelType {

	KARATE(ChatColor.DARK_GRAY + "Karate",
			new ItemStackBuilder(Material.WOOD_SWORD, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_GRAY + "Karate")
					.enchant(Enchantment.DURABILITY, 1, false)
					.flag(ItemFlag.HIDE_ENCHANTS)).build(),
			DefaultKits.noDebuff(), true),
	NODEBUFF(ChatColor.YELLOW + "No Debuff",
			new ItemStackBuilder(Material.SPLASH_POTION, 1).meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "No Debuff")
					.colour(Color.YELLOW)
					.flag(ItemFlag.HIDE_POTION_EFFECTS)
					.enchant(Enchantment.DURABILITY, 1, false)
					.flag(ItemFlag.HIDE_ENCHANTS)).build(),
			DefaultKits.noDebuff(), true),
	DEBUFF(ChatColor.DARK_GREEN + "Debuff",
			new ItemStackBuilder(Material.SPLASH_POTION, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_GREEN + "Debuff")
					.colour(Color.GREEN)
					.flag(ItemFlag.HIDE_POTION_EFFECTS)
					.enchant(Enchantment.DURABILITY, 1, false)
					.flag(ItemFlag.HIDE_ENCHANTS)).build(),
			DefaultKits.debuff(), true),
	COMBO(ChatColor.YELLOW + "Combo",
			new ItemStackBuilder(Material.RAILS, 1).meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Combo")).build(),
			DefaultKits.combo(), true),
	ARCHER(ChatColor.LIGHT_PURPLE + "Archer",
			new ItemStackBuilder(Material.BOW, 1).meta(new ItemMetaBuilder().name(ChatColor.LIGHT_PURPLE + "Archer")
					.enchant(Enchantment.DURABILITY, 1, false)
					.flag(ItemFlag.HIDE_ENCHANTS)).build(),
			DefaultKits.archer(), true),
	ENGINEER(ChatColor.GRAY + "Engineer",
			new ItemStackBuilder(Material.STICK, 1).meta(new ItemMetaBuilder().name(ChatColor.GRAY + "Engineer")
					.enchant(Enchantment.DURABILITY, 1, false)
					.flag(ItemFlag.HIDE_ENCHANTS)).build(),
			DefaultKits.archer(), true),
	CLASSES(ChatColor.RED + "Classes",
			new ItemStackBuilder(Material.NETHER_STAR, 1).meta(new ItemMetaBuilder().name(ChatColor.GOLD + "Classes")).build(),
			DefaultKits.noDebuff(), true),
	VAULTBATTLE(ChatColor.DARK_RED + "Vault Battles",
			new ItemStackBuilder(Material.OBSIDIAN, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_RED + "Vault Battles")).build(),
			DefaultKits.vaultBattle(), true),
	KOTH(ChatColor.GOLD + "KOTH",
			new ItemStackBuilder(Material.DARK_OAK_FENCE, 1).meta(new ItemMetaBuilder().name(ChatColor.GOLD + "KOTH")).build(),
			DefaultKits.koth(), false),
	SPELLBOUND(ChatColor.DARK_PURPLE + "Spellbound", 
			new ItemStackBuilder(Material.STICK, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_PURPLE + "Spellbound")).build(),
			DefaultKits.noDebuff(), false);
	
	private String displayName;
	private ItemStack displayItem;
	private Kit defaultKit;
	private boolean visible;
	
	private DuelType(String displayName, ItemStack displayItem, Kit defaultKit, boolean visible) {
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.defaultKit = defaultKit;
		this.visible = visible;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public Kit getDefaultKit() {
		return defaultKit;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
