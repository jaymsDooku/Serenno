package io.jayms.serenno.item;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
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
	
	private Map<UUID, CustomItem> customItems = new ConcurrentHashMap<>();
	
	private boolean registered = false;
	
	public CustomItem createCustomItem(Class<? extends CustomItem> clazz) {
		CustomItem customItem = null;
		try {
			customItem = clazz.getConstructor().newInstance();
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
	
	public CustomItem getCustomItem(Class<? extends CustomItem> clazz) {
		CustomItem customItem = null; 
		for (Entry<UUID, CustomItem> ciEn : customItems.entrySet()) {
			if (ciEn.getValue().getClass().equals(clazz)) {
				customItem = ciEn.getValue();
			}
		}
		
		if (customItem == null) {
			customItem = createCustomItem(clazz);
		}
		
		return customItem;
	}
	
	public CustomItem getCustomItem(ItemStack itemStack) {
		UUID id = CustomItem.getCustomItemID(itemStack);
		if (id == null) {
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
		
		UUID customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == null) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);

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
		
		UUID customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == null) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
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
		
		UUID customItemID = CustomItem.getCustomItemID(item);
		if (customItemID == null) return;
		if (!(customItems.containsKey(customItemID))) return;
		
		CustomItem customItem = customItems.get(customItemID);
		
		if (customItem.preventOnBlockPlace(e)) {
			e.setCancelled(true);
			return;
		}
		
		Runnable runnable = customItem.onBlockPlace(e);
		
		if (runnable == null) return;
		
		Bukkit.getScheduler().runTask(customItem.getPlugin(), runnable);
	}

}
