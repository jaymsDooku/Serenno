package io.jayms.serenno.game;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.Kit;
import net.md_5.bungee.api.ChatColor;

public enum DuelType {

	NODEBUFF(ChatColor.YELLOW + "No Debuff",
			new ItemStackBuilder(Material.SPLASH_POTION, 1).meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "No Debuff")
					.colour(Color.YELLOW)).build(), DefaultKits.noDebuff()),
	DEBUFF(ChatColor.DARK_GREEN + "Debuff",
			new ItemStackBuilder(Material.SPLASH_POTION, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_GREEN + "Debuff")
					.colour(Color.GREEN)).build(), DefaultKits.debuff()),
	CLASSES(ChatColor.GOLD + "Classes",
			new ItemStackBuilder(Material.NETHER_STAR, 1).meta(new ItemMetaBuilder().name(ChatColor.GOLD + "Classes")).build(),
			DefaultKits.noDebuff()),
	VAULTBATTLE(ChatColor.DARK_RED + "Vault Battles",
			new ItemStackBuilder(Material.OBSIDIAN, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_RED + "Vault Battles")).build(), DefaultKits.vaultBattle()),
	KOTH(ChatColor.GOLD + "KOTH", new ItemStackBuilder(Material.DARK_OAK_FENCE, 1).meta(new ItemMetaBuilder().name(ChatColor.GOLD + "KOTH")).build(), DefaultKits.koth()),
	SPELLBOUND(ChatColor.DARK_PURPLE + "Spellbound", 
			new ItemStackBuilder(Material.STICK, 1).meta(new ItemMetaBuilder().name(ChatColor.DARK_PURPLE + "Spellbound")).build(), DefaultKits.noDebuff());
	
	private String displayName;
	private ItemStack displayItem;
	private Kit defaultKit;
	
	private DuelType(String displayName, ItemStack displayItem, Kit defaultKit) {
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.defaultKit = defaultKit;
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
	
}
