package io.jayms.serenno.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCommon;

public class CustomItemManager implements Listener {

	private static final CustomItemManager customItemManager = new CustomItemManager();
	
	public static CustomItemManager getCustomItemManager() {
		return customItemManager;
	}
	
	private CustomItemManager() {
	}
	
	private Map<Integer, CustomItem> customItems = new ConcurrentHashMap<>();
	
	private boolean registered = false;
	
	public CustomItem createCustomItem(int id, Class<? extends CustomItem> clazz) {
		CustomItem customItem = null;
		try {
			Constructor<? extends CustomItem> constructor = clazz.getConstructor(int.class);
			customItem = constructor.newInstance(id);
			customItems.put(customItem.getID(), customItem);
				
			if (!registered) {
				Bukkit.getPluginManager().registerEvents(this, SerennoCommon.get());
				registered = true;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return customItem;
	}
	
	public CustomItem getCustomItem(int id, Class<? extends CustomItem> clazz) {
		CustomItem customItem = null; 
		for (Entry<Integer, CustomItem> ciEn : customItems.entrySet()) {
			if (ciEn.getValue().getClass().equals(clazz)) {
				customItem = ciEn.getValue();
			}
		}
		
		if (customItem == null) {
			customItem = createCustomItem(id, clazz);
		}
		
		return customItem;
	}
	
	public CustomItem getCustomItem(ItemStack itemStack) {
		int id = CustomItem.getCustomItemID(itemStack);
		if (id == -1) {
			return null;
		}
		return customItems.get(id);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;

		Runnable runnable = null;
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			runnable = customItem.getRightClick(e);
			e.setCancelled(customItem.preventOnRightClick());
		}
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			runnable = customItem.getLeftClick(e);
			e.setCancelled(customItem.preventOnLeftClick());
		}
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(customItem.getPlugin(), runnable);
	}
	
	@EventHandler
	public void onSwitchSlot(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItem(e.getNewSlot());
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;
		Runnable runnable = customItem.onSwitchSlot(e);
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(customItem.getPlugin(), runnable);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null || item.getType() == Material.AIR) return;
		
		int customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == -1) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		if (customItem == null) return;
		
		if (customItem.preventOnBlockPlace(e)) {
			e.setCancelled(true);
			return;
		}
		
		Runnable runnable = customItem.onBlockPlace(e);
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(customItem.getPlugin(), runnable);
	}

}
