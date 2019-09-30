package io.jayms.serenno.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class PerPlayerMenu extends AbstractMenu {

	private Map<UUID, Map<Integer, Button>> buttons = new ConcurrentHashMap<>();
	
	protected PerPlayerMenu(String name) {
		super(name);
	}

	private Map<UUID, Inventory> inventories = new ConcurrentHashMap<>();
	
	public abstract Inventory newInventory(Player player, Map<String, Object> initData);
	
	public void addButton(Player player, int slot, Button button) {
		Map<Integer, Button> buttonMap = getButtonMap(player);
		
		if (buttonMap == null) {
			buttonMap = new ConcurrentHashMap<>();
		}
		
		buttonMap.put(slot, button);
		buttons.put(player.getUniqueId(), buttonMap);
	}
	
	private Map<Integer, Button> getButtonMap(Player player) {
		return buttons.get(player.getUniqueId());
	}
	
	public Button getButton(Player player, int slot) {
		Map<Integer, Button> buttonMap = getButtonMap(player);
		
		if (buttonMap == null) return null;
		
		return buttonMap.get(slot);
	}
	
	@Override
	public Inventory getInventory(Player player, Map<String, Object> initData) {
		Inventory inventory = inventories.get(player.getUniqueId());
		
		if (inventory == null) {
			inventory = newInventory(player, initData);
			inventories.put(player.getUniqueId(), inventory);
		}
		
		return inventory;
	}

	public void refresh(Player player, Inventory inventory) {
		for (int i = 0; i < inventory.getSize(); i++) {
			Button btn = this.getButton(player, i);
			ItemStack it = emptyPane;
			
			if (btn != null) {
				it = btn.getItemStack();
			}
			
			inventory.setItem(i, it);
		}
	}
}
