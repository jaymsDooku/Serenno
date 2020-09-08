package io.jayms.serenno.game.kiteditor;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.PlayerTools;
import net.md_5.bungee.api.ChatColor;

public class KitEditor implements Listener {
	
	private KitEditorChest archerChest;
	private KitEditorChest diamondChest;
	
	private Location saveSignLocation;
	
	private Location kitEditorLocation;
	private Map<SerennoPlayer, KitEditorData> inKitEditor = Maps.newConcurrentMap();
	
	public KitEditor() {
		archerChest = new ArcherKitChest(this, SerennoCrimson.get().getConfigManager().getKitEditorArcherChest());
		diamondChest = new DiamondKitChest(this, SerennoCrimson.get().getConfigManager().getKitEditorDiamondChest());
		saveSignLocation = SerennoCrimson.get().getConfigManager().getKitEditorSaveSignLocation();
		kitEditorLocation = SerennoCrimson.get().getConfigManager().getKitEditorLocation();
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public void sendToKitEditor(SerennoPlayer player, DuelType duelType, int kitSlot) {
		if (SerennoCrimson.get().getLobby().inLobby(player)) {
			SerennoCrimson.get().getLobby().depart(player);
		}
		
		new BukkitRunnable() {
			
			public void run() {
				player.getBukkitPlayer().sendMessage(ChatColor.YELLOW + "Welcome to the " + ChatColor.GOLD + "Kit Editor");
				player.getBukkitPlayer().teleport(kitEditorLocation);
				player.getDuelingKit(duelType, kitSlot).load(player.getBukkitPlayer());
			};
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
		inKitEditor.put(player, new KitEditorData(duelType, kitSlot));
	}
	
	public boolean inKitEditor(SerennoPlayer player) {
		return inKitEditor.containsKey(player);
	}
	
	public KitEditorData getKitEditorData(SerennoPlayer player) {
		return inKitEditor.get(player);
	}
	
	public void saveKitAndDepart(SerennoPlayer player) {
		KitEditorData kitEditorData = inKitEditor.get(player);
		player.setDuelingKit(kitEditorData.duelType, kitEditorData.kitSlot, new Kit(player.getBukkitPlayer()));
		inKitEditor.remove(player);
		SerennoCrimson.get().getLobby().sendToLobby(player);
		player.sendMessage(ChatColor.YELLOW + "Saving kit and sending you back to the lobby!");
	}
	
	public Location getKitEditorLocation() {
		return kitEditorLocation;
	}
	
	public KitEditorChest getDiamondChest() {
		return diamondChest;
	}
	
	public KitEditorChest getArcherChest() {
		return archerChest;
	}
	
	public static class KitEditorData {
		
		public DuelType duelType;
		public int kitSlot;
		
		public KitEditorData(DuelType duelType, int kitSlot) {
			this.duelType = duelType;
			this.kitSlot = kitSlot;
		}
		
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		if (!inKitEditor(sp)) {
			return;
		}
		
		e.getItemDrop().remove();
	}
	
	@EventHandler
	public void saveSign(PlayerInteractEvent e) {
		if (!PlayerTools.isRightClick(e.getAction())) {
			return;
		}

		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(e.getPlayer());
		if (!inKitEditor(sp)) {
			return;
		}
		
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}

		if (clickedBlock.getType() != Material.CHEST && clickedBlock.getType() != Material.TRAPPED_CHEST && clickedBlock.getType() != Material.WALL_SIGN) {
			e.setCancelled(true);
		}
		
		if (!clickedBlock.getLocation().equals(saveSignLocation)) {
			return;
		}

		saveKitAndDepart(sp);
	}

}
