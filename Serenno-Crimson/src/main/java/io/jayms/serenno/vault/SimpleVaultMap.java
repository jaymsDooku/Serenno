package io.jayms.serenno.vault;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.jayms.serenno.arena.Arena;
import net.md_5.bungee.api.ChatColor;

public class SimpleVaultMap implements VaultMap {

	private Arena arena;
	private VaultMapDatabase database;
	private World originalWorld;
	private Set<World> activeWorlds = new HashSet<>();
	
	public SimpleVaultMap(World originalWorld, Arena arena, VaultMapDatabase database) {
		this.originalWorld = originalWorld;
		this.arena = arena;
		this.database = database;
	}
	
	@Override
	public void gotoVaultMap(Player player) {
		player.sendMessage(ChatColor.YELLOW + "You are going to vault map: " + arena.getRegion().getDisplayName());
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.teleport(getGotoLocation());
			}
			
		};
	}
	
	@Override
	public Location getGotoLocation() {
		Vector gotoLoc = database.getGotoLocation();
		return new Location(originalWorld, gotoLoc.getX(), gotoLoc.getY(), gotoLoc.getZ());
	}

	@Override
	public VaultMapDatabase getDatabase() {
		return database;
	}

	@Override
	public Arena getArena() {
		return arena;
	}

	@Override
	public World getOriginalWorld() {
		return originalWorld;
	}

	@Override
	public Set<World> getActiveWorlds() {
		return activeWorlds;
	}

}
