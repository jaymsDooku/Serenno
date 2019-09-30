package io.jayms.serenno.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hotbar {

	private int heldSlot;
	private ItemStack[] items = new ItemStack[9];
	
	public Hotbar() {
	}
	
	public Hotbar(Player player) {
		heldSlot = player.getInventory().getHeldItemSlot();
		for (int i = 0; i < items.length; i++) {
			items[i] = player.getInventory().getItem(i);
		} 
	}
	
	public void set(int i, ItemStack it) {
		items[i] = it;
	}
	
	public void load(Player player) {
		player.getInventory().setHeldItemSlot(heldSlot);
		for (int i = 0; i < items.length; i++) {
			player.getInventory().setItem(i, items[i]);
		}
	}
	
}
