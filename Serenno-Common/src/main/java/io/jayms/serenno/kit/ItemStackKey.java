package io.jayms.serenno.kit;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.util.ItemUtil;

public class ItemStackKey {

	private ItemStack itemStack;
	
	public ItemStackKey(ItemStack it) {
		this.itemStack = it;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemStackKey)) {
			return false;
		}
		
		ItemStackKey key = (ItemStackKey) obj;
		ItemStack keyIt = key.itemStack;
		
		Material type1 = keyIt.getType();
		Material type2 = itemStack.getType();
		
		String name1 = ItemUtil.getName(keyIt);
		String name2 = ItemUtil.getName(itemStack);
		
		return type1 == type2 
				&& name1.equals(name2);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(itemStack.getType(), ItemUtil.getName(itemStack));
	}
	
}
