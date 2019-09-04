package io.jayms.serenno.lobby;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Sets;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;

public class Lobby implements Listener {
	
	private Location lobbySpawn;
	private Set<SerennoPlayer> inLobby = Sets.newConcurrentHashSet();
	
	public Lobby() {
		lobbySpawn = SerennoCrimson.get().getConfigManager().getLobbySpawn();
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
	public void sendToLobby(SerennoPlayer player) {
		new BukkitRunnable() {
			
			public void run() {
				player.getBukkitPlayer().teleport(lobbySpawn);
			};
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
		inLobby.add(player);
	}
	
	public boolean inLobby(SerennoPlayer player) {
		return inLobby.contains(player);
	}
	
	public void depart(SerennoPlayer player) {
		inLobby.remove(player);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().getPlayer(player);
		sendToLobby(sPlayer);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().getPlayer(player);
		if (inLobby(sPlayer)) {
			depart(sPlayer);
		}
	}
}
