package io.jayms.serenno.game.menu;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.player.SerennoPlayer;

public class KitSlotMenu extends SingleMenu {

	public KitSlotMenu() {
		super(ChatColor.YELLOW + "Select Kit Slot");
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
		int size = 9;
		for (int i = 0; i < size; i++) {
			addButton(i, getKitSlotButton(i, initData));
		}
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getKitSlotButton(int i, Map<String, Object> initData) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.BOOK, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.WHITE + "Kit " + ChatColor.RED + "#" + (i+1))
								.enchant(Enchantment.DURABILITY, 1, false)
								.flag(ItemFlag.HIDE_ENCHANTS)).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
						DuelType duelType = (DuelType) initData.get("duelType");
						
						SerennoCrimson.get().getKitEditor().sendToKitEditor(sp, duelType, i);
					}
					
				}).build();
	}

}
