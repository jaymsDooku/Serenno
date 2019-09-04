package io.jayms.serenno.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import io.jayms.serenno.util.MaterialTools;

public class ItemMetaBuilder {

	private String name;
	private List<String> lore = new ArrayList<>();
	private boolean unbreakable = false;
	private Map<Enchantment, Level> enchantments;
	private List<ItemFlag> flags;
	
	//potion meta
	private PotionData potionData;
	private Color colour;
	private List<PotionEffect> effects;
	
	//skull meta
	private String owner;
	
	public ItemMetaBuilder owner(String owner) {
		this.owner = owner;
		return this;
	}
	
	public ItemMetaBuilder name(String set) {
		name = set;
		return this;
	}
	
	public ItemMetaBuilder lore(List<String> set) {
		lore = set;
		return this;
	}
	
	public List<String> lore() {
		return lore;
	}
	
	public ItemMetaBuilder flag(ItemFlag flag) {
		if (flags == null) {
			flags = new ArrayList<>();
		}
		flags.add(flag);
		return this;
	}
	
	public ItemMetaBuilder enchant(Enchantment ench, int level, boolean ignoreLvlRestrict) {
		if (enchantments == null) {
			enchantments = new HashMap<>();
		}
		enchantments.put(ench, new Level(level, ignoreLvlRestrict));
		return this;
	}
	
	public ItemMetaBuilder potionData(PotionData set) {
		potionData = set;
		return this;
	}
	
	public ItemMetaBuilder colour(Color set) {
		colour = set;
		return this;
	}
	
	public ItemMetaBuilder effects(List<PotionEffect> set) {
		effects = set;
		return this;
	}
	
	public ItemMeta build(ItemStack it) {
		ItemMeta meta = it.getItemMeta();
		if (meta == null) {
			return meta;
		}
		if (name != null) {
			meta.setDisplayName(name);
		}
		if (lore != null) {
			meta.setLore(lore);
		}
		if (flags != null) {
			meta.addItemFlags(flags.toArray(new ItemFlag[0]));
		}
		if (enchantments != null) {
			for (Map.Entry<Enchantment, Level> enchEn : enchantments.entrySet()) {
				Level lvl = enchEn.getValue();
				meta.addEnchant(enchEn.getKey(), lvl.level, lvl.ignoreLevelRestrict);
			}
		}
		meta.setUnbreakable(unbreakable);
		
		if (MaterialTools.isPotion(it) && meta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) meta;
			if (potionData != null) {
				potionMeta.setBasePotionData(potionData);
			}
			if (colour != null) {
				potionMeta.setColor(colour);
			}
			if (effects != null && !effects.isEmpty()) {
				effects.stream().forEach(e -> {
					potionMeta.addCustomEffect(e, false);
				});
			}
		}
		
		if (meta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) meta;
			if (owner != null) {
				skullMeta.setOwner(owner);
			}
		}
		return meta;
	}
	
	private class Level {
		
		int level;
		boolean ignoreLevelRestrict;
		
		Level(int level, boolean ignoreLevelRestrict) {
			this.level = level;
			this.ignoreLevelRestrict = ignoreLevelRestrict;
		}
	}
}

