package io.jayms.serenno.game.menu;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.vaultbattle.VaultBattleRequest;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.vault.VaultMap;
import net.md_5.bungee.api.ChatColor;

public class VaultSideMenu extends SingleMenu {

	public VaultSideMenu() {
		super(ChatColor.YELLOW + "Select Side");
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
		Arena arena = (Arena) initData.get("arena");
		if (!(arena instanceof VaultMap)) {
			return null;
		}
		VaultMap vaultMap = (VaultMap) arena;
		
		Set<ChatColor> teamColors = arena.getSpawnPoints().keySet();
		if (teamColors.isEmpty()) {
			return null;
		}
		
		Iterator<ChatColor> teamColorsIt = teamColors.iterator();
		
		ChatColor team1 = teamColorsIt.next();
		ChatColor team2 = teamColorsIt.next();
		String groupName1 = vaultMap.getDatabase().getGroupNameFromColour(team1);
		String groupName2 = vaultMap.getDatabase().getGroupNameFromColour(team2);
		System.out.println("groups: " + vaultMap.getDatabase().getGroupSource());
		Group group1 = vaultMap.getDatabase().getGroupSource().get(groupName1.toLowerCase());
		Group group2 = vaultMap.getDatabase().getGroupSource().get(groupName2.toLowerCase());
		System.out.println("group1: " + group1);
		System.out.println("group2: " + group2);
		if (group1 == null || group2 == null) {
			return null;
		}
		
		addButton(3, getSideButton(team1, group1.getName(), initData));
		addButton(5, getSideButton(team2, group2.getName(), initData));
		
		int size = 9;
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getSideButton(ChatColor teamColor, String sideName, Map<String, Object> initData) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.GRASS, 1)
						.meta(new ItemMetaBuilder()
								.name(teamColor + sideName)).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
						
						Arena arena = (Arena) initData.get("arena");
						DuelType duelType = (DuelType) initData.get("duelType");
						SerennoPlayer toDuel = (SerennoPlayer) initData.get("toDuel");
						double scaling = (double) initData.get("scaling");
						
						VaultBattleRequest request = new VaultBattleRequest(sp, duelType, (VaultMap) arena, teamColor, scaling);
						
						if (toDuel instanceof SerennoBot) {
							SerennoCrimson.get().getGameManager().vaultBattle(toDuel, request);
							return;
						}
						toDuel.sendRequest(request);
					}
					
				}).build();
	}

}
