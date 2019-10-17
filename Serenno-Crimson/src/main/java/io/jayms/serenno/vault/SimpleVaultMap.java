package io.jayms.serenno.vault;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchWorld;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class SimpleVaultMap implements VaultMap {

	private Arena arena;
	private String originalWorldName;
	private Map<String, VaultMapDatabase> vaultMapDatabases;
	private World originalWorld;
	private Set<World> activeWorlds = new HashSet<>();
	private int activeWorldID;
	
	public SimpleVaultMap(String originalWorldName, Arena arena, SQLite database) {
		this.originalWorldName = originalWorldName;
		this.arena = arena;
		this.vaultMapDatabases = new HashMap<>();
		
		VaultMapDatabase db = new VaultMapDatabase(originalWorldName, this, database);
		vaultMapDatabases.put(originalWorldName, db);
	}
	
	@Override
	public void load() {
	}
	
	@Override
	public void save() {
		VaultMapDatabase database = getDatabase();
		ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
		BastionManager bm = SerennoCobalt.get().getCitadelManager().getBastionManager();
		SnitchManager sm = SerennoCobalt.get().getCitadelManager().getSnitchManager();
		
		VaultMapReinforcementDataSource reinSource = database.getReinforcementSource();
		VaultMapBastionDataSource bastionSource = database.getBastionSource();
		VaultMapSnitchDataSource snitchSource = database.getSnitchSource();
		
		ReinforcementWorld reinWorld = rm.getReinforcementWorld(originalWorld);
		BastionWorld bastionWorld = bm.getBastionWorld(originalWorld);
		SnitchWorld snitchWorld = sm.getSnitchWorld(originalWorld);
		
		Set<Reinforcement> reinforcements = reinWorld.getAllReinforcements();
		Set<Bastion> bastions = bastionWorld.getAllBastions();
		Set<Snitch> snitches = snitchWorld.getAllSnitches();
		
		for (Reinforcement rein : reinforcements) {
			reinSource.create(rein);
		}
		
		for (Bastion bastion : bastions) {
			bastionSource.create(bastion);
		}
		
		for (Snitch snitch : snitches) {
			snitchSource.create(snitch);
		}
		SerennoCrimson.get().getLogger().info("Saved vault map: " + arena.getName());
	}
	
	@Override
	public void delete() {
		getDatabase().delete();
		if (isOriginalWorldLoaded()) {
			World originalWorld = getOriginalWorld();
			SerennoCrimson.get().getLobby().sendToLobby(originalWorld);
			Bukkit.unloadWorld(originalWorldName, false);
			LocationTools.deleteWorld(originalWorld.getWorldFolder());
			SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().remove(originalWorld);
		}
		if (!activeWorlds.isEmpty()) {
			for (World activeWorld : new HashSet<>(activeWorlds)) {
				deactivateWorld(activeWorld);
			}
		}
		SerennoCrimson.get().getArenaManager().deleteArena(getArena());
		SerennoCrimson.get().getRegionManager().deleteRegion(getArena().getRegion());
		SerennoCrimson.get().getLogger().info("Deleted vault map: " + getArena().getName());
	}
	
	@Override
	public void gotoVaultMap(Player player) {
		player.sendMessage(ChatColor.YELLOW + "You are going to vault map: " + arena.getRegion().getDisplayName());
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.teleport(getGotoLocation());
			}
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
	}
	
	@Override
	public void setGotoLocation(Location location) {
		getDatabase().setGotoLocation(location);
	}
	
	@Override
	public Location getGotoLocation() {
		return getDatabase().getGotoLocation();
	}
	
	@Override
	public VaultMapDatabase newDatabase(World world) {
		String name = world.getName();
		File newDBFile = new File(SerennoCrimson.get().getVaultMapManager().getVaultMapsFolderTemp(), name + ".db");
		VaultMapDatabase vmDB = getDatabase().copy(name, newDBFile);
		vaultMapDatabases.put(name, vmDB);
		return vmDB;
	}
	
	@Override
	public VaultMapDatabase getDatabase(World world) {
		return vaultMapDatabases.get(world.getName());
	}

	@Override
	public VaultMapDatabase getDatabase() {
		return vaultMapDatabases.get(originalWorldName);
	}

	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public boolean isOriginalWorldLoaded() {
		if (arena.getRegion().isWorldLoaded()) {
			return true;
		}
		
		return originalWorld != null;
	}

	@Override
	public World getOriginalWorld() {
		if (originalWorld == null) {
			originalWorld = LocationTools.loadWorld(originalWorldName);
			SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(originalWorld, getDatabase().getReinforcementSource());
			SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(originalWorld, getDatabase().getBastionSource());
			SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(originalWorld, getDatabase().getSnitchSource());
			SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().put(originalWorld, this);
		}
		return originalWorld;
	}

	@Override
	public Set<World> getActiveWorlds() {
		return activeWorlds;
	}

	@Override
	public String getName() {
		return arena.getName();
	}

	@Override
	public void setDescription(String set) {
		arena.setDescription(set);
	}

	@Override
	public String getDescription() {
		return arena.getDescription();
	}

	@Override
	public void setCreators(String set) {
		arena.setCreators(set);
	}

	@Override
	public String getCreators() {
		return arena.getCreators();
	}

	@Override
	public void setDisplayItem(ItemStack set) {
		arena.setDisplayItem(set);
	}

	@Override
	public ItemStack getDisplayItem() {
		return arena.getDisplayItem();
	}

	@Override
	public Region getRegion() {
		return arena.getRegion();
	}

	@Override
	public void addSpawnPoint(ChatColor teamColor, Location spawn) {
		arena.addSpawnPoint(teamColor, spawn);
	}

	@Override
	public void removeSpawnPoint(ChatColor teamColor) {
		arena.removeSpawnPoint(teamColor);
	}

	@Override
	public Map<ChatColor, Location> getSpawnPoints() {
		return arena.getSpawnPoints();
	}

	@Override
	public boolean requiresWorldCloning() {
		return arena.requiresWorldCloning();
	}

	@Override
	public void addDuelType(DuelType type) {
		arena.addDuelType(type);
	}

	@Override
	public void removeDuelType(DuelType type) {
		arena.removeDuelType(type);
	}

	@Override
	public Set<DuelType> getDuelTypes() {
		return arena.getDuelTypes();
	}

	@Override
	public void setDirty(boolean set) {
		arena.setDirty(set);
	}

	@Override
	public boolean isDirty() {
		return arena.isDirty();
	}

	@Override
	public World activateWorld() {
		String worldName = originalWorld.getName() + activeWorldID++;
		File originalWorldFolder = originalWorld.getWorldFolder();
		File newWorldFolder = new File(originalWorldFolder.getParentFile(), worldName);
		newWorldFolder.setReadable(true, false);
		newWorldFolder.setWritable(true, false);
		newWorldFolder.setExecutable(true, false);
		LocationTools.copyWorld(originalWorldFolder, newWorldFolder);
		World activatedWorld = LocationTools.loadWorld(worldName);
		
		VaultMapDatabase database = getDatabase();
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(activatedWorld, database.getReinforcementSource());
		SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(activatedWorld, database.getBastionSource());
		SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(activatedWorld, database.getSnitchSource());
		
		SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().put(activatedWorld, this);
		
		activeWorlds.add(activatedWorld);
		return activatedWorld;
	}

	@Override
	public void deactivateWorld(World world) {
		if (!isActiveWorld(world)) {
			return;
		}
		activeWorlds.remove(world);
		SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().remove(world);
		SerennoCrimson.get().getLobby().sendToLobby(world);
		
		VaultMapDatabase database = getDatabase(world);
		database.delete();
		
		Bukkit.unloadWorld(world, false);
		LocationTools.deleteWorld(world.getWorldFolder());
	}

	@Override
	public boolean isActiveWorld(World world) {
		return activeWorlds.contains(world);
	}

}
