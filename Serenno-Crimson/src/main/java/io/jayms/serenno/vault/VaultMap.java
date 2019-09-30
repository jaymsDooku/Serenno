package io.jayms.serenno.vault;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.jayms.serenno.arena.Arena;

public interface VaultMap {
	
	void gotoVaultMap(Player player);

	Location getGotoLocation();
	
	VaultMapDatabase getDatabase();
	
	Arena getArena();
	
	World getOriginalWorld();
	
	Set<World> getActiveWorlds();
	
}
