package io.jayms.serenno.vault;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.event.ArenaLoadEvent;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.region.Region;
import net.md_5.bungee.api.ChatColor;

public class VaultMapManager implements Listener {
	
	private Map<String, VaultMap> vaultMaps = Maps.newConcurrentMap();
	
	private File vaultMapsFolder;
	private File vaultMapsFolderTemp;
	
	public VaultMapManager() {
		vaultMapsFolder = new File(SerennoCrimson.get().getDataFolder(), "vaultMapsFolder");
		vaultMapsFolderTemp = new File(vaultMapsFolder, "temp");
		if (!vaultMapsFolder.exists()) {
			vaultMapsFolder.mkdirs();
		}
		if (!vaultMapsFolderTemp.exists()) {
			vaultMapsFolderTemp.mkdirs();
		}
		
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public File getVaultMapsFolderTemp() {
		return vaultMapsFolderTemp;
	}
	
	public File getVaultMapsFolder() {
		return vaultMapsFolder;
	}
	
	@EventHandler
	public void onArenaLoad(ArenaLoadEvent e) {
		Arena arena = e.getArena();
		String name = arena.getRegion().getName();
		File file = new File(vaultMapsFolder, name + ".db");
		if (!file.exists()) {
			return;
		}
		
		VaultMap vaultMap = new SimpleVaultMap(name, arena, 
				new SQLite(SerennoCrimson.get(), SerennoCrimson.get().getLogger(), "[VaultMap - " + name + "]", name + ".db", vaultMapsFolder.getAbsolutePath()));
		vaultMaps.put(name, vaultMap);
		e.setArena(vaultMap);
		SerennoCrimson.get().getLogger().info("Loaded vault map: " + name);
	}
	
	public VaultMap createVault(SerennoPlayer sp, String name, int radius) {
		if (isVaultMap(name)) {
			return null;
		}
		
		World world = Bukkit.getWorld(name);
		if (world != null) {
			sp.sendMessage(ChatColor.RED + "A world with that name already exists.");
			return null;
		}
		
		WorldCreator creator = new WorldCreator(name);
		creator.generator(new ChunkGenerator() {
		    @Override
		    public byte[] generate(World world, Random random, int x, int z) {
		        return new byte[32768]; //Empty byte array
		    }
		});
		creator.environment(Environment.NORMAL);
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		World createdWorld = creator.createWorld();
		createdWorld.getBlockAt(0, 69, 0).setType(Material.BEDROCK);
		
		Location p1 = new Location(createdWorld, -radius, 0, -radius);
		Location p2 = new Location(createdWorld, radius, 256, radius);
		Region worldRegion = SerennoCrimson.get().getRegionManager().createRegion(sp.getBukkitPlayer(), name, p1, p2);
		
		if (worldRegion == null) {
			sp.sendMessage(ChatColor.RED + "Failed to create region.");
			return null;
		}
		
		Arena worldArena = SerennoCrimson.get().getArenaManager().createArena(sp.getBukkitPlayer(), worldRegion.getName());
		
		if (worldArena == null) {
			sp.sendMessage(ChatColor.RED + "Failed to create arena.");
			return null;
		}
		
		VaultMap vaultMap = new SimpleVaultMap(createdWorld.getName(), worldArena,
				new SQLite(SerennoCrimson.get(), SerennoCrimson.get().getLogger(), "[VaultMap - " + name + "]", name + ".db", vaultMapsFolder.getAbsolutePath()));
		
		vaultMaps.put(worldRegion.getName(), vaultMap);
		return vaultMap;
	}
	
	public void deleteVaultMap(VaultMap vaultMap) {
		vaultMaps.remove(vaultMap.getArena().getName());
		vaultMap.getDatabase().delete();
		vaultMap.getOriginalWorld().getWorldFolder().delete();
		SerennoCrimson.get().getArenaManager().deleteArena(vaultMap.getArena());
		SerennoCrimson.get().getRegionManager().deleteRegion(vaultMap.getArena().getRegion());
		SerennoCrimson.get().getLogger().info("Deleted vault map: " + vaultMap.getArena().getName());
	}
	
	public boolean isVaultMap(String name) {
		return vaultMaps.containsKey(name);
	}
	
	public VaultMap getVaultMap(String name) {
		VaultMap vm = vaultMaps.get(name);
		return vm;
	}
	
	public Collection<VaultMap> listVaults() {
		return vaultMaps.values();
	}

}
