package io.jayms.serenno.team.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.team.menu.ManageTeamMenu;
import net.md_5.bungee.api.ChatColor;

public class ManageTeamItem extends CustomItem {
	
	public static final int ID = 408;
	
	private ManageTeamMenu manageTeamMenu;
	private MenuController menuController;
	
	public ManageTeamItem(int id) {
		super(SerennoCrimson.get(), id);
		this.manageTeamMenu = new ManageTeamMenu();
		this.menuController = new MenuController(this.manageTeamMenu);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.PAPER, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Manage Team"));
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
			manageTeamMenu.open(who, null);
		};
	}
}

