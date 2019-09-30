package io.jayms.serenno.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;

import io.jayms.serenno.SerennoCommon;

public class MenuController implements Listener {

	private Menu menu;
	
	public Menu getMenu() {
		return menu;
	}
	
	public MenuController(Menu menu) {
		this.menu = menu;
		Bukkit.getPluginManager().registerEvents(this, SerennoCommon.get());
	}
	
	private boolean inMenuInventory(HumanEntity he, InventoryEvent e) {
		if (!(he instanceof Player)) return false;
		
		Player player = (Player) he;
		
		if (!e.getInventory().getName().equals(menu.getName())) return false;
		
		return menu.hasOpen(player);
	}
	
	private Button getButton(Player player, int slot) {
		return (menu instanceof PerPlayerMenu) ? ((PerPlayerMenu) menu).getButton(player, slot) : ((SingleMenu) menu).getButton(slot);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!inMenuInventory(e.getWhoClicked(), e)) return;
		System.out.println("Clicking inside " + menu);
		
		Player player = (Player) e.getWhoClicked();
		
		if (e.getClickedInventory().equals(player.getInventory()) && menu.allowPlayerInventory()) {
			return;
		}
		
		e.setCancelled(true);
		Button btn = getButton(player, e.getSlot());
		
		if (btn == null) return;
		
		if (btn.isPickUpAble()) {
			e.setCursor(btn.getItemStack().clone());
		}
		
		ClickHandler clickHandle = btn.getClickHandler();
		
		if (clickHandle == null) return;
		
		clickHandle.handleClick(e);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!inMenuInventory(e.getPlayer(), e)) return;
		
		Player player = (Player) e.getPlayer();
		
		System.out.println("Closing " + menu);
		menu.close(player);
	}
	
}
