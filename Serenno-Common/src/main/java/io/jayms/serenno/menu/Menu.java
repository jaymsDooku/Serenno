package io.jayms.serenno.menu;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface Menu {
	
	void setName(String name);
	
	String getName();
	
	void setSize(int set);
	
	int getSize();
	
	boolean allowPlayerInventory();
	
	Inventory getInventory(Player player, Map<String, Object> initData);
	
	boolean hasOpen(Player player);
	
	void open(Player player, Map<String, Object> initData);
	
	void close(Player player);
	
	boolean onOpen(Player player);
	
	void onClose(Player player);
	
	void onClose(Player player, Map<String, Object> data);
	
}
