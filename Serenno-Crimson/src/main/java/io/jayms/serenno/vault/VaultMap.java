package io.jayms.serenno.vault;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.jayms.serenno.arena.Arena;

public interface VaultMap extends Arena {
	
	void load();
	
	void save();
	
	void delete();
	
	void gotoVaultMap(Player player);
	
	void leaveVaultMap(Player player);

	void setGotoLocation(Location location);
	
	Location getGotoLocation();
	
	VaultMapDatabase newDatabase(World world);
	
	VaultMapDatabase getDatabase(World world);
	
	VaultMapDatabase getDatabase();
	
	Arena getArena();
	
	boolean inOriginalWorld(Player player);
	
	boolean isOriginalWorldLoaded();
	
	World getOriginalWorld();
	
	World activateWorld();
	
	void deactivateWorld(World world);
	
	boolean isActiveWorld(World world);
	
	Set<World> getActiveWorlds();
	
}
