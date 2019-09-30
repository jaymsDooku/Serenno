package io.jayms.serenno.vault;

import java.io.File;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.region.Region;
import net.md_5.bungee.api.ChatColor;

public class VaultMapManager {
	
	private Map<String, VaultMap> vaultMaps = Maps.newConcurrentMap();
	
	private File vaultMapsFolder;
	
	public VaultMapManager() {
		vaultMapsFolder = new File(SerennoCrimson.get().getDataFolder(), "vaultMapsFolder");
		if (!vaultMapsFolder.exists()) {
			vaultMapsFolder.mkdirs();
		}
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
		
		Location p1 = new Location(createdWorld, -radius, 0, -radius);
		Location p2 = new Location(createdWorld, radius, 255, radius);
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
		
		VaultMapDatabase vaultMapDatabase = new VaultMapDatabase(new SQLite(SerennoCrimson.get(), SerennoCrimson.get().getLogger(), "[VaultMap - " + name + "]", name + ".db", vaultMapsFolder.getAbsolutePath()));
		VaultMap vaultMap = new SimpleVaultMap(createdWorld, worldArena, vaultMapDatabase);
		
		vaultMaps.put(worldRegion.getName(), vaultMap);
		return vaultMap;
	}
	
	public boolean isVaultMap(String name) {
		return vaultMaps.containsKey(name);
	}

}
