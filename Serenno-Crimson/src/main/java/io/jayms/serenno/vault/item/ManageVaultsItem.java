package io.jayms.serenno.vault.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.vault.menu.ManageVaultsMenu;
import net.md_5.bungee.api.ChatColor;

public class ManageVaultsItem extends CustomItem {
	
	public static final int ID = 405;
	
	private ManageVaultsMenu manageVaultsMenu;
	private MenuController menuController;
	
	public ManageVaultsItem(int id) {
		super(SerennoCrimson.get(), id);
		this.manageVaultsMenu = new ManageVaultsMenu();
		this.menuController = new MenuController(this.manageVaultsMenu);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.OBSIDIAN, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Manage Vaults"));
	}
	
	@Override
	public boolean preventOnLeftClick() {
		return false;
	}
	
	@Override
	public boolean preventOnRightClick() {
		return true;
	}

	@Override
	public Runnable getLeftClick(PlayerInteractEvent e) {
		return null;
	}

	@Override
	public Runnable getRightClick(PlayerInteractEvent e) {
		return () -> {
			Player who = e.getPlayer();
			manageVaultsMenu.open(who, null);
		};
	}
}
