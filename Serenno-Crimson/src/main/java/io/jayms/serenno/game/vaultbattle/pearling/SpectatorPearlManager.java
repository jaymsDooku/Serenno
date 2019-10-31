package io.jayms.serenno.game.vaultbattle.pearling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class SpectatorPearlManager {

	private Duel duel;
	private Map<SerennoPlayer, SpectatorPearlItem> pearled = Maps.newConcurrentMap();
	
	public SpectatorPearlManager(Duel duel) {
		this.duel = duel;
	}
	
	public void pearl(SerennoPlayer player) {
		SpectatorPearlItem pearlItem = (SpectatorPearlItem) CustomItemManager.getCustomItemManager().getCustomItem(SpectatorPearlItem.ID, SpectatorPearlItem.class);
		
		Map<String, Object> data = new HashMap<>();
		data.put("pearled", player);
		ItemStack pearlStack = pearlItem.getItemStack(data);
		
		DuelStatistics stats = duel.getStatistics();
		SortedSet<Entry<UUID, Double>> sortedDamageDealers = stats.getSortedDamageDealersTo(player, 1000 * 60 * 2);
		
		SerennoPlayer killer = null;
		Iterator<Entry<UUID, Double>> damageDealersIt = sortedDamageDealers.iterator();
		while (damageDealersIt.hasNext()) {
			Entry<UUID, Double> damageDealersEn = damageDealersIt.next();
			UUID damageDealer = damageDealersEn.getKey();
			Player damagePlayer = Bukkit.getPlayer(damageDealer);
			if (damagePlayer == null || !damagePlayer.isOnline()) {
				continue;
			}
			SerennoPlayer sDamagePlayer = SerennoCrimson.get().getPlayerManager().get(damagePlayer);
			DuelTeam team = duel.getTeam(sDamagePlayer);
			if (sDamagePlayer != null && team.isAlive(sDamagePlayer)) {
				killer = sDamagePlayer;
				break;
			}
		}
		
		
		if (killer != null) {
			HashMap<Integer, ItemStack> left = killer.getBukkitPlayer().getInventory().addItem(pearlStack);
			for (ItemStack item : left.values()) {
				killer.getLocation().getWorld().dropItemNaturally(killer.getLocation(), item);
			}
			return;
		}
		
		player.getLocation().getWorld().dropItemNaturally(player.getLocation(), pearlStack);
	}
	
	public boolean isPearled(SerennoPlayer player) {
		return pearled.containsKey(player);
	}
	
	public void free(SerennoPlayer master, SpectatorPearlItem pearl, ItemStack pearlStack) {
		SerennoPlayer pearledPlayer = pearl.getPearled(pearlStack);
		if (pearledPlayer == null) {
			master.sendMessage(ChatColor.RED + "This pearl holds no one.");
			return;
		}
		if (!isPearled(pearledPlayer)) {
			master.sendMessage(ChatColor.RED + "That player is not pearled.");
			master.getBukkitPlayer().getInventory().remove(pearlStack);
			return;
		}
		
		duel.broadcast(msgPrefix(ChatColor.GOLD + pearledPlayer.getName() + ChatColor.YELLOW + " has been freed!"));
		pearledPlayer.sendMessage(msgPrefix(ChatColor.GREEN + "You have been freed!"));
		duel.spawn(pearledPlayer);
	}
	
	private String msgPrefix(String msg) {
		return ChatColor.BLACK + "[" + ChatColor.YELLOW + "Spectator" + ChatColor.DARK_PURPLE + "Pearl" + ChatColor.BLACK + "]: " 
				+ ChatColor.RESET + msg;
	}
	
}
