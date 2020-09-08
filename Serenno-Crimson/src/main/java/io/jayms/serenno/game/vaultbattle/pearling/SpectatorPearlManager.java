package io.jayms.serenno.game.vaultbattle.pearling;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import io.jayms.serenno.game.DeathCause;
import io.jayms.serenno.util.LocationTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorPearlManager {

	private Duel duel;
	private Map<SerennoPlayer, SpectatorPearlItem> pearled = Maps.newConcurrentMap();
	
	public SpectatorPearlManager(Duel duel) {
		this.duel = duel;
	}
	
	public DeathCause pearl(SerennoPlayer player) {
		DuelStatistics stats = duel.getStatistics();
		SortedSet<Entry<UUID, Double>> sortedDamageDealers = stats.getSortedDamageDealersTo(player, 1000 * 60 * 2);

		if (sortedDamageDealers.isEmpty()) {
			DuelTeam team =  duel.getTeam(player);
			Location spawn = duel.getSpawnPoint(team.getTeamColor());
			player.teleport(spawn);
 			return DeathCause.ENVIRONMENT;
		}

		SpectatorPearlItem pearlItem = (SpectatorPearlItem) CustomItemManager.getCustomItemManager().getCustomItem(SpectatorPearlItem.ID, SpectatorPearlItem.class);
		
		Map<String, Object> data = new HashMap<>();
		data.put("pearled", player);
		ItemStack pearlStack = pearlItem.getItemStack(data);

		SerennoPlayer killer = null;
		double damage = 0;
		List<Entry<UUID, Double>> damageDealersList = new ArrayList<>(sortedDamageDealers);
		for (Entry<UUID, Double> damageDealersEn : damageDealersList) {
			UUID damageDealer = damageDealersEn.getKey();
			Player damagePlayer = Bukkit.getPlayer(damageDealer);
			if (damagePlayer == null || !damagePlayer.isOnline()) {
				continue;
			}
			SerennoPlayer sDamagePlayer = SerennoCrimson.get().getPlayerManager().get(damagePlayer);
			DuelTeam team = duel.getTeam(sDamagePlayer);
			if (sDamagePlayer != null && team.isAlive(sDamagePlayer)) {
				killer = sDamagePlayer;
				damage = damageDealersEn.getValue();
				break;
			}
		}

		if (killer == null || damage <= 0) {
			DuelTeam team =  duel.getTeam(player);
			Location spawn = duel.getSpawnPoint(team.getTeamColor());
			player.teleport(spawn);
			return DeathCause.ENVIRONMENT;
		}

		pearled.put(player, pearlItem);
		if (killer != null) {
			HashMap<Integer, ItemStack> left = killer.getBukkitPlayer().getInventory().addItem(pearlStack);
			for (ItemStack item : left.values()) {
				killer.getLocation().getWorld().dropItemNaturally(killer.getLocation(), item);
			}
			duel.broadcast(ChatColor.GOLD + player.getName() + ChatColor.YELLOW
					+ " has been pearled by " + ChatColor.GOLD + killer.getName() + ChatColor.BLACK + "(" + ChatColor.YELLOW + df.format(damage) + ChatColor.BLACK + ")");
			return DeathCause.PVP;
		}

		Location pLoc = player.getLocation();
		pLoc.getWorld().dropItemNaturally(pLoc, pearlStack);
		duel.broadcast(ChatColor.GOLD + player.getName() + ChatColor.YELLOW
				+ " has been pearled at "
				+ ChatColor.BLACK + "[" + ChatColor.YELLOW + pLoc.getBlockX() + ChatColor.BLACK  + ", " + ChatColor.YELLOW + pLoc.getBlockY() + ChatColor.BLACK + ", " + ChatColor.YELLOW + pLoc.getBlockZ() + ChatColor.BLACK + "]");
		return DeathCause.PVP;
	}

	DecimalFormat df = new DecimalFormat("######");
	
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
