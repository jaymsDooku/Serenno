package io.jayms.serenno.vault.menu;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.PerPlayerMenu;
import io.jayms.serenno.menu.SimpleButton;

public class ManageVaultsMenu extends PerPlayerMenu {
	
	public ManageVaultsMenu() {
		super(ChatColor.YELLOW + "Vault Management Menu");
	}
	
	@Override
	public boolean onOpen(Player player) {
		return true;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Player player, Map<String, Object> initData) {
		addButton(player, 2, getCreateVaultButton(player));
		addButton(player, 6, getViewVaultsButton(player));
		
		int size = 9;
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(player, inventory);
		return inventory;
	}
	
	private SimpleButton getCreateVaultButton(Player player) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.EMERALD_BLOCK, 1)
						.meta(new ItemMetaBuilder().name(ChatColor.GREEN + "Create New Vault"))
						.build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						
					}
					
				}).build();
	}
	
	private SimpleButton getViewVaultsButton(Player player) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.EMERALD_BLOCK, 1)
						.meta(new ItemMetaBuilder().name(ChatColor.GREEN + "View Vaults"))
						.build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						
					}
					
				}).build();
	}
}
