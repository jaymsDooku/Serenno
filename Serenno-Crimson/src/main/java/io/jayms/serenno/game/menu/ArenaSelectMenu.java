package io.jayms.serenno.game.menu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.MathTools;

public class ArenaSelectMenu extends SingleMenu {

	public ArenaSelectMenu() {
		super(ChatColor.YELLOW + "Select Arena");
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
		DuelType duelType = (DuelType) initData.get("duelType");
		List<Arena> arenas = SerennoCrimson.get().getArenaManager().listArenas(duelType);
		
		int i = 0;
		arenas.stream().forEach(a -> {
			if (!a.getDuelTypes().contains(duelType)) {
				return;
			}
			addButton(i, getArenaButton(a, initData));
		});
		
		int size = MathTools.ceil(arenas.size(), 9);
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getArenaButton(Arena arena, Map<String, Object> initData) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.GRASS, 1)
						.meta(new ItemMetaBuilder()
								.name(arena.getRegion().getDisplayName())
								.lore(Arrays.asList(ChatColor.DARK_GREEN + "Creators: [" + ChatColor.GREEN + arena.getCreators() + ChatColor.DARK_GREEN + "]"))).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
						
						DuelType duelType = (DuelType) initData.get("duelType");
						SerennoPlayer toDuel = (SerennoPlayer) initData.get("toDuel");
						
						DuelRequest request = new DuelRequest(sp, duelType, arena);
						
						if (toDuel instanceof SerennoBot) {
							SerennoCrimson.get().getGameManager().duel(toDuel, request);
							return;
						}
						toDuel.sendRequest(request);
					}
					
				}).build();
	}

}
