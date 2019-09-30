package io.jayms.serenno.game.menu;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.util.MathTools;

public class DuelTypeMenu extends SingleMenu {
	
	public enum MenuType {
		ARENA, KIT_EDITOR;
	}
	
	public DuelTypeMenu() {
		super(ChatColor.YELLOW + "Choose Duel Type");
	}
	
	@Override
	public boolean onOpen(Player player) {
		return true;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Map<String, Object> initData) {
		DuelType[] duelTypes = DuelType.values();
		
		for (int i = 0; i < duelTypes.length; i++) {
			if (duelTypes[i] != null) {
				DuelType duelType = duelTypes[i];
				if (!duelType.isVisible()) continue;
				addButton(i, getDuelTypeButton(duelTypes[i], initData));
			}
		}
		
		int size = MathTools.ceil(duelTypes.length, 9);
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getDuelTypeButton(DuelType duelType, Map<String, Object> initData) {
		return new SimpleButton.Builder(this)
				.setItemStack(duelType.getDisplayItem())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						MenuType menuType = (MenuType) initData.getOrDefault("menuType", MenuType.ARENA);
						initData.put("duelType", duelType);
						Player player = (Player) e.getWhoClicked();
						
						if (menuType == MenuType.ARENA) {
							SerennoCrimson.get().getGameManager().getArenaSelectMenu().open(player, initData);
						} else if (menuType == MenuType.KIT_EDITOR) {
							SerennoCrimson.get().getGameManager().getKitSlotMenu().open(player, initData);
						}
					}
					
				}).build();
	}

}
