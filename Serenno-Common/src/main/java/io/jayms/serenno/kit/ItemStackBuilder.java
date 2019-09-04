package io.jayms.serenno.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackBuilder {

	private Material type;
	private int amount;
	private short durability = 0;
	private ItemMetaBuilder meta;
	
	public ItemStackBuilder(Material type, int amount) {
		this.type = type;
		this.amount = amount;
	}
	
	public ItemStackBuilder durability(short set) {
		durability = set;
		return this;
	}
	
	public ItemStackBuilder meta(ItemMetaBuilder set) {
		meta = set;
		return this;
	}
	
	public ItemStack build() {
		ItemStack result = new ItemStack(type, amount, durability);
		if (meta != null) {
			result.setItemMeta(meta.build(result));
		}
		return result;
	}
}
