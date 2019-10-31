package io.jayms.serenno.game.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.menu.DuelMenu;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.MenuController;
import net.md_5.bungee.api.ChatColor;

public class DuelMenuItem extends CustomItem {
	
	public static final int ID = 400;

	private DuelMenu duelMenu;
	private MenuController menuController;
	
	public DuelMenuItem(int id) {
		super(SerennoCrimson.get(), id);
		this.duelMenu = new DuelMenu();
		this.menuController = new MenuController(this.duelMenu);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.GOLD_SWORD, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Open Duel Menu"));
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
			duelMenu.open(e.getPlayer(), null);
		};
	}

}
