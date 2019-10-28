package io.jayms.serenno.vault;

import java.io.File;
import java.io.IOException;
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
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class SimpleVaultMap implements VaultMap {

	private Arena arena;
	
	private String originalWorldName;
	private World originalWorld;
	
	private Set<Player> inOriginalWorld;
	private Map<String, VaultMapDatabase> vaultMapDatabases;
	private Set<World> activeWorlds = new HashSet<>();
	private int activeWorldID;
	
	//private SQLite database;
	
	public SimpleVaultMap(String originalWorldName, Arena arena, SQLite database) {
		this.originalWorldName = originalWorldName;
		this.arena = arena;
		this.inOriginalWorld = new HashSet<>();
		this.vaultMapDatabases = new HashMap<>();
		//this.database = database;
		
		VaultMapDatabase db = new VaultMapDatabase(originalWorldName, SimpleVaultMap.this, database);
		db.load();
		vaultMapDatabases.put(originalWorldName, db);
	}
	
	@Override
	public void load() {
	}
	
	@Override
	public void save() {
		if (!isOriginalWorldLoaded()) {
			return;
		}
		
		VaultMapDatabase database = getDatabase();
		ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
		
		VaultMapReinforcementDataSource reinSource = database.getReinforcementSource();
		
		ReinforcementWorld reinWorld = rm.getReinforcementWorld(originalWorld);
		
		Set<Reinforcement> reinforcements = reinWorld.getAllReinforcements();
		
		for (Reinforcement rein : reinforcements) {
			if (rein.isInMemory()) {
				reinSource.create(rein);
			} else {
				reinSource.update(rein);
			}
		}
		
		SerennoCrimson.get().getArenaManager().saveArena(getArena());
		SerennoCrimson.get().getRegionManager().saveRegion(getRegion());
		SerennoCrimson.get().getLogger().info("Saved vault map: " + arena.getName());
	}
	
	@Override
	public void delete() {
		if (isOriginalWorldLoaded()) {
			World originalWorld = getOriginalWorld();
			SerennoCrimson.get().getLobby().sendToLobby(originalWorld);
			
			SerennoCobalt.get().getCitadelManager().getReinforcementManager().deleteReinforcementWorld(originalWorld, false);
			SerennoCobalt.get().getCitadelManager().getBastionManager().deleteBastionWorld(originalWorld);
			SerennoCobalt.get().getCitadelManager().getSnitchManager().deleteSnitchWorld(originalWorld);
			SerennoCobalt.get().getCitadelManager().getArtilleryManager().deleteArtilleryWorld(originalWorld);
			
			Bukkit.unloadWorld(originalWorldName, false);
			SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().remove(originalWorld.getUID());
			LocationTools.deleteWorld(originalWorld.getWorldFolder());
		} else {
			LocationTools.deleteWorld(new File(SerennoCrimson.get().getDataFolder().getParentFile().getParentFile(), originalWorldName));
		}
		if (!activeWorlds.isEmpty()) {
			for (World activeWorld : new HashSet<>(activeWorlds)) {
				deactivateWorld(activeWorld);
			}
		}
		SerennoCrimson.get().getArenaManager().deleteArena(getArena());
		SerennoCrimson.get().getRegionManager().deleteRegion(getArena().getRegion());
		SerennoCrimson.get().getLogger().info("Deleted vault map: " + getArena().getName());
		getDatabase().delete();
	}
	
	@Override
	public void gotoVaultMap(Player player) {
		if (!getDatabase().isAllowed(player)) {
			player.sendMessage(ChatColor.RED + "You are not allowed to go this vault map.");
			return;
		}
		
		for (Group group : getDatabase().getGroupSource().values()) {
			group.addMember(player);
		}
		player.sendMessage(ChatColor.YELLOW + "You are going to vault map: " + arena.getRegion().getDisplayName());
		inOriginalWorld.add(player);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.teleport(getGotoLocation());
			}
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
	}
	
	@Override
	public void leaveVaultMap(Player player) {
		for (Group group : getDatabase().getGroupSource().values()) {
			group.removeMember(player);
		}
		inOriginalWorld.remove(player);
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
		try {
			newDBFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		VaultMapDatabase db = vaultMapDatabases.get(originalWorldName);
		return db;
	}

	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public boolean inOriginalWorld(Player player) {
		return inOriginalWorld(player);
	}
	
	@Override
	public boolean isOriginalWorldLoaded() {
		if (arena.getRegion().isWorldLoaded()) {
			return true;
		}
		
		return originalWorld != null;
	}
	
	@Override
	public String getOriginalWorldName() {
		return originalWorldName;
	}

	@Override
	public World getOriginalWorld() {
		if (originalWorld == null) {
			originalWorld = LocationTools.loadWorld(originalWorldName);
			SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(originalWorld, getDatabase().getReinforcementSource());
			SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(originalWorld, getDatabase().getBastionSource());
			SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(originalWorld, getDatabase().getSnitchSource());
			SerennoCobalt.get().getCitadelManager().getArtilleryManager().newArtilleryWorld(originalWorld);
			SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().put(originalWorld.getUID(), this);
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
		String worldName = getOriginalWorld().getName() + activeWorldID++;
		File originalWorldFolder = getOriginalWorld().getWorldFolder();
		File newWorldFolder = new File(originalWorldFolder.getParentFile(), worldName);
		newWorldFolder.setReadable(true, false);
		newWorldFolder.setWritable(true, false);
		newWorldFolder.setExecutable(true, false);
		LocationTools.copyWorld(originalWorldFolder, newWorldFolder);
		World activatedWorld = LocationTools.loadWorld(worldName);
		
		VaultMapDatabase database = newDatabase(activatedWorld);
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(activatedWorld, database.getReinforcementSource());
		SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(activatedWorld, database.getBastionSource());
		SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(activatedWorld, database.getSnitchSource());
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().newArtilleryWorld(activatedWorld);
		
		SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().put(activatedWorld.getUID(), this);
		
		activeWorlds.add(activatedWorld);
		
		Arena arena = getArena();
		Region region = arena.getRegion();
		region.newChildWorld(activatedWorld);
		return activatedWorld;
	}

	@Override
	public void deactivateWorld(World world) {
		if (!isActiveWorld(world)) {
			return;
		}
		activeWorlds.remove(world);
		SerennoCrimson.get().getVaultMapManager().getWorldToVaultMaps().remove(world.getUID());
		SerennoCrimson.get().getLobby().sendToLobby(world);
		
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().deleteReinforcementWorld(world, false);
		SerennoCobalt.get().getCitadelManager().getBastionManager().deleteBastionWorld(world);
		SerennoCobalt.get().getCitadelManager().getSnitchManager().deleteSnitchWorld(world);
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().deleteArtilleryWorld(world);
		
		VaultMapDatabase database = getDatabase(world);
		database.delete();
		
		Bukkit.unloadWorld(world, false);
		LocationTools.deleteWorld(world.getWorldFolder());
		
		Arena arena = getArena();
		Region region = arena.getRegion();
		region.removeChildWorld(world);
	}

	@Override
	public boolean isActiveWorld(World world) {
		return activeWorlds.contains(world);
	}

}
