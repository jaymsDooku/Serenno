package io.jayms.serenno.vault;

import java.util.Set;
import java.util.function.Consumer;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
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
	
	VaultMapDatabase getDatabase();
	
	Arena getArena();
	
	boolean inOriginalWorld(Player player);
	
	boolean isOriginalWorldLoaded();
	
	String getOriginalWorldName();

	void getOriginalWorldAsync(Consumer<VaultMap> callback);

	void getReinforcementWorldAsync(Consumer<VaultMap> callback);

	World getOriginalWorld();

	void setReady(boolean set);

	boolean isReady();

	ReinforcementWorld getReinforcementWorld();
	
	World activateWorld();
	
	void deactivateWorld(World world);
	
	boolean isActiveWorld(World world);
	
	Set<World> getActiveWorlds();

	void dispose();
	
}
