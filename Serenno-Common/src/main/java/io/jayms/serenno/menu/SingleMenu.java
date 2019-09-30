package io.jayms.serenno.menu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SingleMenu extends AbstractMenu {

	private Map<Integer, Button> buttons = new ConcurrentHashMap<>();
	
	protected SingleMenu(String name) {
		super(name);
	}
	
	public abstract Inventory newInventory(Map<String, Object> initData);
	
	public void addButton(int slot, Button button) {
		buttons.put(slot, button);
	}
	
	public void clear() {
		buttons.clear();
	}
	
	public Button getButton(int slot) {
		return buttons.get(slot);
	}
	
	@Override
	public Inventory getInventory(Player player, Map<String, Object> initData) {
		return newInventory(initData);
	}

	public void refresh(Inventory inventory) {
		for (int i = 0; i < inventory.getSize(); i++) {
			Button btn = this.getButton(i);
			ItemStack it = emptyPane;
			
			if (btn != null) {
				it = btn.getItemStack();
			}
			
			inventory.setItem(i, it);
		}
	}
}
