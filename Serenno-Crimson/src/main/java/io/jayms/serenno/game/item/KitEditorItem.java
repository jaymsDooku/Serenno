package io.jayms.serenno.game.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.menu.DuelTypeMenu.MenuType;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;

public class KitEditorItem extends CustomItem {
	
	public static final int ID = 402;
	
	public KitEditorItem(int id) {
		super(SerennoCrimson.get(), id);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.BOOK, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Open Kit Editor"));
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
			Map<String, Object> initData = new HashMap<>();
			initData.put("menuType", MenuType.KIT_EDITOR);
			SerennoCrimson.get().getGameManager().getDuelTypeMenu().open(e.getPlayer(), initData);
		};
	}

}
