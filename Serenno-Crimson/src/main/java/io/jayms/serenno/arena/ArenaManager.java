package io.jayms.serenno.arena;


import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.event.ArenaLoadEvent;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.db.event.DBConnectEvent;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.event.RegionDeletionEvent;
import io.jayms.serenno.util.MongoTools;
import net.md_5.bungee.api.ChatColor;

public class ArenaManager implements Listener {

	private Map<String, Arena> arenas = Maps.newConcurrentMap();
	
	public ArenaManager() {
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	@EventHandler
	public void onDBConnect(DBConnectEvent e) {
		if (!e.isConnected()) {
			return;
		}
		load();
	}
	
	@EventHandler
	public void onRegionDelete(RegionDeletionEvent e) {
		Region deleted = e.getDeleted();
		Arena arena = getArena(deleted);
		if (arena == null) {
			return;
		}
		deleteArena(arena);
	}
	
	public Arena createArena(Player player, String regionName) {
		Region region = SerennoCrimson.get().getRegionManager().getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return null;
		}
		
		if (arenas.containsKey(regionName)) {
			player.sendMessage(ChatColor.RED + "That arena already exists.");
			return null;
		}
		
		Arena arena = new SimpleArena(region);
		saveArena(arena);
		arenas.put(regionName, arena);
		return arena;
	}
	
	public void replaceArena(Arena arena) {
		arenas.put(arena.getRegion().getName(), arena);
	}

	public Arena getArena(String name) {
		return arenas.get(name);
	}
	
	public Arena getArena(Region region) {
		return getArena(region.getName());
	}
	
	public List<Arena> listArenas(DuelType duelType) {
		return Lists.newArrayList(arenas.values().stream()
				.filter(a -> a.getDuelTypes().contains(duelType)).collect(Collectors.toList()));
	}
	
	public List<Arena> listArenas() {
		return Lists.newArrayList(arenas.values());
	}
	
	private void load() {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("arena");
                FindIterable<Document> query = collection.find();
                Iterator<Document> iterator = query.iterator();

                while (iterator.hasNext()) {
                    Document document = iterator.next();

                    String regionName = document.getString("regionName");
                    Region region = SerennoCrimson.get().getRegionManager().getRegion(regionName);
                    if (region == null) {
                    	SerennoCrimson.get().getLogger().severe("Failed to load arena. " + regionName + " region doesn't exist anymore.");
                    	continue;
                    }
                    
                    String description = document.getString("description");
                    String creators = document.getString("creators");
                    ItemStack displayItem = MongoTools.itemstack((Document) document.get("displayItem"));
                    
                    Map<ChatColor, Location> spawns = new HashMap<>();
                    Document spawnsDoc = (Document) document.get("spawns");
                    for (Entry<String, Object> spawnsDocEn : spawnsDoc.entrySet()) { 
                    	String teamColorStr = spawnsDocEn.getKey();
                    	ChatColor teamColor = Arrays.stream(ChatColor.values()).filter(c -> c.getName().equals(teamColorStr)).findFirst().orElse(null);
                    	if (teamColor == null) continue;
                    	
                    	Document spawnLocDoc = (Document) spawnsDocEn.getValue();
                    	Location spawnLoc = MongoTools.location(spawnLocDoc);
                    	spawns.put(teamColor, spawnLoc);
                    }
                    
                    List<String> duelTypesStr = document.getList("duelTypes", String.class);
                    Set<DuelType> duelTypes = duelTypesStr.stream().map(s -> DuelType.valueOf(s)).collect(Collectors.toSet());
                    Arena arena = new SimpleArena(region, description, creators, displayItem, spawns, duelTypes);
                    
                    ArenaLoadEvent event = new ArenaLoadEvent(arena);
                    Bukkit.getPluginManager().callEvent(event);
                    arena = event.getArena();
                    
                    arenas.put(regionName, arena);
                    SerennoCrimson.get().getLogger().info("Loaded arena: " + arena.getRegion().getName());
                }
			}
			
		}.runTask(SerennoCrimson.get());
	}
	
	public Document document(Arena arena) {
		Document doc = new Document();
		doc.append("regionName", arena.getRegion().getName());
		doc.append("creators", arena.getCreators());
		doc.append("description", arena.getDescription());
		doc.append("displayItem", MongoTools.toDocument(arena.getDisplayItem()));
		
		Map<ChatColor, Location> spawns = arena.getSpawnPoints();
		Document spawnsDoc = new Document();
		for (Entry<ChatColor, Location> spawnsEn : spawns.entrySet()) {
			ChatColor teamColor = spawnsEn.getKey();
			Location spawnLoc = spawnsEn.getValue();
			if (spawnLoc != null) {
				spawnsDoc.append(teamColor.getName(), MongoTools.toDocument(spawnLoc));
			}
		}
		doc.append("spawns", spawnsDoc);
		
		Set<DuelType> duelTypes = arena.getDuelTypes();
		List<String> duelTypesStr = duelTypes.stream().map(d -> d.toString()).collect(Collectors.toList());
		doc.append("duelTypes", duelTypesStr);
		return doc;
	}
	
	private void unsafeSaveArena(Arena arena) {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		if (!arena.isDirty()) {
			return;
		}
		
		MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("arena");
		FindIterable<Document> query = collection.find(Filters.eq("regionName", arena.getRegion().getName()));
		Document document = query.first();
		
		Document newDoc = document(arena);
		
		if (document != null) {
			collection.replaceOne(document, newDoc);
		} else {
			collection.insertOne(newDoc);
		}
		arena.setDirty(false);
		SerennoCrimson.get().getLogger().info("Saved arena: " + arena.getName());
		Bukkit.broadcast(ChatColor.YELLOW + "Arena: " + ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + " has been saved.", "arena.engineer");
	}
	
	public void saveArena(Arena arena) {
		if (!arena.isDirty()) {
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				unsafeSaveArena(arena);
			}
			
		}.runTaskAsynchronously(SerennoCrimson.get());
	}
	
	public void saveAll() {
		listArenas().forEach(a -> {
			unsafeSaveArena(a);
		});
	}
	
	public void deleteArena(Arena arena) {
		deleteArena(arena.getRegion());
	}
	
	public void deleteArena(Region region) {
		deleteArena(region.getName());
	}
	
	public void deleteArena(String regionName) {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		arenas.remove(regionName);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("arena");
                FindIterable<Document> query = collection.find(Filters.eq("regionName", regionName));
                Document document = query.first();

                if (document != null) {
                    collection.findOneAndDelete(document);
                    Bukkit.broadcast(ChatColor.YELLOW + "Arena: " + ChatColor.GOLD + regionName + ChatColor.YELLOW + " has been deleted.", "arena.engineer");
                }
			}
			
		}.runTaskAsynchronously(SerennoCrimson.get());
	}
	
}
