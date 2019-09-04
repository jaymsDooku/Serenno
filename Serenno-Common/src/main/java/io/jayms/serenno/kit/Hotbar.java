package io.jayms.serenno.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hotbar {

	private ItemStack[] items = new ItemStack[9];
	
	public Hotbar() {
	}
	
	public Hotbar(Player player) {
		for (int i = 0; i < items.length; i++) {
			items[i] = player.getInventory().getItem(i);
			//System.out.println(i + "item type: " + (items[i] != null ? items[i].getType() : ""));
		} 
	}
	
	public void set(int i, ItemStack it) {
		items[i] = it;
	}
	
	public void load(Player player) {
		for (int i = 0; i < items.length; i++) {
			player.getInventory().setItem(i, items[i]);
		}
	}
	
}
