package io.jayms.serenno.game.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duelable;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.player.SerennoPlayerManager;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.team.TeamManager;
import io.jayms.serenno.util.MathTools;
import io.jayms.serenno.util.PlayerTools;

public class DuelMenu extends SingleMenu {
	
	public DuelMenu() {
		super(ChatColor.YELLOW + "Duel Menu");
	}
	
	public boolean renew(Player player) {
		clear();
		
		List<Duelable> toDuel = new ArrayList<>();
		SerennoPlayerManager spm = SerennoCrimson.get().getPlayerManager();
		TeamManager teamManager = SerennoCrimson.get().getTeamManager();
		SerennoPlayer senderPlayer = spm.get(player);
		Team senderTeam = teamManager.getTeam(senderPlayer);
		
		Collection<? extends Player> online = Bukkit.getOnlinePlayers();
		if (online.size() <= 1) {
			return false;
		}
		for (Player p : online) {
			SerennoPlayer serennoPlayer = spm.get(p);
			
			if (senderPlayer.getID().equals(serennoPlayer.getID()) || (senderTeam != null && senderTeam.inTeam(serennoPlayer))) {
				continue;
			}
			
			Team team = teamManager.getTeam(serennoPlayer);
			
			if (serennoPlayer.isSpectating()) {
				continue;
			}
			
			Duelable duelable = null;
			
			if (team != null) {
				duelable = team;
			} else {
				duelable = serennoPlayer;
			}
			
			if (serennoPlayer.inDuel()) {
				continue;
			}
			
			toDuel.add(duelable);
		}
		
		for (int i = 0; i < toDuel.size(); i++) {
			Duelable duelable = toDuel.get(i);
			addButton(i, getDuelPlayerButton(duelable));
		}
		
		int size = MathTools.ceil(toDuel.size(), 9);
		boolean updateMenu = getSize() != size;
		setSize(size);
		return updateMenu;
	}
	
	@Override
	public boolean onOpen(Player player) {
		renew(player);
		boolean opening = getSize() > 0;
		if (!opening) {
			player.sendMessage(ChatColor.RED + "There is no else online to duel.");
		}
		return opening;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Map<String, Object> initData) {
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getDuelPlayerButton(Duelable toDuel) {
		UUID headID = (toDuel instanceof Team) ? ((Team) toDuel).getLeader().getID() : ((SerennoPlayer) toDuel).getID();
		String name = ChatColor.RED + ((toDuel instanceof Team) ? ((Team) toDuel).getLeader().getBukkitPlayer().getName() + "'s Team" : ((SerennoPlayer) toDuel).getName());
		
		ItemStack head = PlayerTools.getHead(headID, name);
		
		ItemMeta meta = head.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) lore = new ArrayList<>();
		
		final List<String> finalLore = lore;
		if (toDuel instanceof Team) {
			((Team) toDuel).getMembers().stream().forEach(p -> {
				finalLore.add(ChatColor.YELLOW + p.getBukkitPlayer().getName());
			});
		}
		
		meta.setLore(finalLore);
		head.setItemMeta(meta);
		
		return new SimpleButton.Builder(this)
				.setItemStack(head)
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						HashMap<String, Object> initData = new HashMap<>();
						initData.put("toDuel", toDuel);
						SerennoCrimson.get().getGameManager().getDuelTypeMenu().open((Player) e.getWhoClicked(), initData);
					}
					
				}).build();
	}

}
