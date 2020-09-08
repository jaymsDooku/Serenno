package io.jayms.serenno.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.jayms.serenno.SerennoCommon;

public abstract class PlayerManager<T> implements Listener {
	
	protected PlayerManager() {
		Bukkit.getPluginManager().registerEvents(this, SerennoCommon.get());
	}
	
	protected abstract Function<Player, T> getPlayerInstantiator();

	public abstract void onJoin(PlayerJoinEvent e);

	public abstract void onQuit(PlayerQuitEvent e);
	
	private Map<UUID, T> players = new ConcurrentHashMap<>();
	
	public Map<UUID, T> getPlayers() {
		return players;
	}
	
	public boolean isPlayer(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	public T get(Player player) {
		T result = players.get(player.getUniqueId());
		
		if (result == null) {
			result = getPlayerInstantiator().apply(player);
			players.put(player.getUniqueId(), result);
		}

		return result;
	}

	public T remove(Player player) {
		return players.remove(player.getUniqueId());
	}
	
}
