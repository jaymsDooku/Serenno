package io.jayms.serenno.vault;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchWorld;
import io.jayms.serenno.vault.data.mongodb.MongoVaultMapBastionDataSource;
import io.jayms.serenno.vault.data.mongodb.MongoVaultMapReinforcementDataSource;
import io.jayms.serenno.vault.data.mongodb.MongoVaultMapSnitchDataSource;
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
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.util.Vector;

public class SimpleVaultMap implements VaultMap {

	private Arena arena;
	
	private String originalWorldName;
	private World originalWorld;
	private VaultMapDatabase database;
	private Set<World> activeWorlds = new HashSet<>();
	private int activeWorldID;

	private boolean ready;
	
	public SimpleVaultMap(String originalWorldName, Arena arena) {
		this.originalWorldName = originalWorldName;
		this.arena = arena;
		this.database = new VaultMapDatabase(originalWorldName, this);
		database.load();
	}

	@Override
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	@Override
	public boolean isReady() {
		return ready;
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
		BastionManager bm = SerennoCobalt.get().getCitadelManager().getBastionManager();
		SnitchManager sm = SerennoCobalt.get().getCitadelManager().getSnitchManager();
		
		MongoVaultMapReinforcementDataSource reinSource = database.getReinforcementSource();
		MongoVaultMapBastionDataSource bastionDataSource = database.getBastionSource();
		MongoVaultMapSnitchDataSource snitchDataSource = database.getSnitchSource();
		
		ReinforcementWorld reinWorld = rm.getReinforcementWorld(originalWorld);
		BastionWorld bastionWorld = bm.getBastionWorld(originalWorld);
		SnitchWorld snitchWorld = sm.getSnitchWorld(originalWorld);
		
		Set<Reinforcement> reinforcements = reinWorld.getAllReinforcements();
		reinSource.persistAll(reinforcements, () -> {
			SerennoCrimson.get().getLogger().info("Saved reinforcements for: " + arena.getName());
		});
		Set<Bastion> bastions = bastionWorld.getAllBastions();
		bastionDataSource.persistAll(reinWorld, bastions, () -> {
			SerennoCrimson.get().getLogger().info("Saved bastions for: " + arena.getName());
		});
		Set<Snitch> snitches = snitchWorld.getAllSnitches();
		snitchDataSource.persistAll(reinWorld, snitches, () -> {
			SerennoCrimson.get().getLogger().info("Saved snitches for: " + arena.getName());
		});
		
		SerennoCrimson.get().getArenaManager().saveArena(getArena());
		SerennoCrimson.get().getRegionManager().saveRegion(getRegion());
		SerennoCrimson.get().getLogger().info("Saved vault map: " + arena.getName());
	}
	
	@Override
	public void delete() {
		database.delete();
		
		if (isOriginalWorldLoaded()) {
			World originalWorld = getOriginalWorld();
			SerennoCrimson.get().getLobby().sendToLobby(originalWorld);
			
			SerennoCobalt.get().getCitadelManager().getReinforcementManager().deleteReinforcementWorld(originalWorld, false);
			SerennoCobalt.get().getCitadelManager().getBastionManager().deleteBastionWorld(originalWorld);
			SerennoCobalt.get().getCitadelManager().getSnitchManager().deleteSnitchWorld(originalWorld);
			SerennoCobalt.get().getCitadelManager().getArtilleryManager().deleteArtilleryWorld(originalWorld);
			
			Bukkit.unloadWorld(originalWorldName, false);
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
		getOriginalWorldAsync((vm) -> {
			player.teleport(getGotoLocation());
		});
	}
	
	@Override
	public void leaveVaultMap(Player player) {
		for (Group group : getDatabase().getGroupSource().values()) {
			group.removeMember(player);
		}
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
	public VaultMapDatabase getDatabase() {
		return database;
	}

	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public boolean inOriginalWorld(Player player) {
		return player.getWorld().getName().equals(originalWorldName);
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

	private ReinforcementWorld reinforcementWorld;

	@Override
	public ReinforcementWorld getReinforcementWorld() {
		return reinforcementWorld;
	}

	@Override
	public void getReinforcementWorldAsync(Consumer<VaultMap> callback) {
		new BukkitRunnable() {

			@Override
			public void run() {
				reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(originalWorldName, getDatabase().getGroupSource(), getDatabase().getReinforcementSource());
				new BukkitRunnable() {

					@Override
					public void run() {
						callback.accept(SimpleVaultMap.this);
					}
				}.runTaskLater(SerennoCrimson.get(), 1L);
			}

		}.runTaskAsynchronously(SerennoCrimson.get());
	}

	@Override
	public void getOriginalWorldAsync(Consumer<VaultMap> callback) {
		if (originalWorld != null) {
			callback.accept(this);
			return;
		}
		originalWorld = LocationTools.loadWorld(originalWorldName);
		new BukkitRunnable() {

			@Override
			public void run() {
				SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(originalWorld, getDatabase().getBastionSource());
				SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(originalWorld, getDatabase().getSnitchSource());
				SerennoCobalt.get().getCitadelManager().getArtilleryManager().newArtilleryWorld(originalWorld);
				new BukkitRunnable() {

					@Override
					public void run() {
						callback.accept(SimpleVaultMap.this);
					}
				}.runTaskLater(SerennoCrimson.get(), 1L);
 				}

		}.runTaskAsynchronously(SerennoCrimson.get());
	}

	@Override
	public World getOriginalWorld() {
		if (originalWorld == null) {
			originalWorld = LocationTools.loadWorld(originalWorldName);
			reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().newReinforcementWorld(originalWorldName, getDatabase().getGroupSource(), getDatabase().getReinforcementSource());
			SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(originalWorld, getDatabase().getBastionSource());
			SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(originalWorld, getDatabase().getSnitchSource());
			SerennoCobalt.get().getCitadelManager().getArtilleryManager().newArtilleryWorld(originalWorld);
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
		String worldName = getOriginalWorldName() + activeWorldID++;
		File serverFolder = SerennoCrimson.get().getDataFolder().getParentFile().getParentFile();
		File originalWorldFolder = new File(serverFolder, getOriginalWorldName());
		File newWorldFolder = new File(serverFolder, worldName);
		newWorldFolder.setReadable(true, false);
		newWorldFolder.setWritable(true, false);
		newWorldFolder.setExecutable(true, false);
		LocationTools.copyWorld(originalWorldFolder, newWorldFolder);
		World activatedWorld = LocationTools.loadWorld(worldName);
		activatedWorld.setAutoSave(false);
		
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
		SerennoCrimson.get().getLobby().sendToLobby(world);
		
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().deleteReinforcementWorld(world, false);
		SerennoCobalt.get().getCitadelManager().getBastionManager().deleteBastionWorld(world);
		SerennoCobalt.get().getCitadelManager().getSnitchManager().deleteSnitchWorld(world);
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().deleteArtilleryWorld(world);
		
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

	@Override
	public void dispose() {
		database.dispose();
	}
}
