package io.jayms.serenno.region;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.db.event.DBConnectEvent;
import io.jayms.serenno.region.event.RegionDeletionEvent;
import io.jayms.serenno.util.MongoTools;
import io.jayms.serenno.util.PlayerTools;
import io.jayms.serenno.util.PlayerTools.Clipboard;
import net.md_5.bungee.api.ChatColor;

public class RegionManager implements Listener {

	private RegionListener listener;
	private Map<String, Region> regions = new HashMap<>();
	
	public RegionManager() {
		this.listener = new RegionListener(this);
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
		Bukkit.getPluginManager().registerEvents(listener, SerennoCrimson.get());
	}
	
	@EventHandler
	public void onDBConnect(DBConnectEvent e) {
		if (!e.isConnected()) {
			return;
		}
		load();
	}
	
	private void load() {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
                MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("region");
                FindIterable<Document> query = collection.find();
                Iterator<Document> iterator = query.iterator();

                while (iterator.hasNext()) {
                    Document document = iterator.next();

                    String regionName = document.getString("regionName");
                    String displayName = document.getString("displayName");
                    String parentWorldStr = document.getString("parentWorld");
                    World parentWorld = Bukkit.getWorld(parentWorldStr);
                    
                    if (parentWorld == null) {
                    	SerennoCrimson.get().getLogger().severe("Parent world of region " + regionName + " no longer exists. Failed to load this region.");
                    	cancel();
                    	return;
                    }
                    
                    Vector point1 = MongoTools.vector((Document) document.get("point1"));
                    Vector point2 = MongoTools.vector((Document) document.get("point2"));
                    
                    List<String> childWorldsList = document.getList("childWorlds", String.class);
                    List<String> flagsList = document.getList("flags", String.class);
                    //List<String> possibleFlagsList = document.getList("possibleFlags", String.class);
                    
                    Location p1 = new Location(parentWorld, point1.getX(), point1.getY(), point1.getZ());
                    Location p2 = new Location(parentWorld, point2.getX(), point2.getY(), point2.getZ());
                    Region region = new SimpleRegion(regionName, p1, p2);
                    region.setDisplayName(displayName);
                    
                    Set<World> childWorlds = new HashSet<>();
                    for (String s : childWorldsList) {
                    	World childWorld = Bukkit.getWorld(s);
                    	if (childWorld == null) {
                    		continue;
                    	}
                    	childWorlds.add(childWorld);
                    }
                    
                    region.getChildWorlds().addAll(childWorlds);
                    region.getFlags().addAll(flagsList);
                    
                    region.getPossibleFlags().clear();
                    region.getPossibleFlags().add(RegionFlags.BLOCK_BREAK);
            		region.getPossibleFlags().add(RegionFlags.BLOCK_PLACE);
            		region.getPossibleFlags().add(RegionFlags.PVP);
            		region.getPossibleFlags().add(RegionFlags.PVE);
            		region.getPossibleFlags().add(RegionFlags.HUNGER_LOSS);
            		region.getPossibleFlags().add(RegionFlags.DAMAGE_LOSS);
                    
                    regions.put(regionName, region);
                    SerennoCrimson.get().getLogger().info("Loaded region: " + region.getName());
                }
			}
			
		}.runTask(SerennoCrimson.get());
	}
	
	public Document document(Region region) {
		Document newDoc = new Document("regionName", region.getName())
				.append("displayName", region.getDisplayName())
				.append("parentWorld", region.getParentWorld().getName())
				.append("point1", MongoTools.toDocument(region.getPoint1()))
				.append("point2", MongoTools.toDocument(region.getPoint2()))
				.append("childWorlds", Lists.newArrayList(region.getChildWorlds()))
				.append("flags", Lists.newArrayList(region.getFlags()));
				//.append("possibleFlags", Lists.newArrayList(region.getPossibleFlags()));
		return newDoc;
	}
	
	private void unsafeSaveRegion(Region region) {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		if (!region.isDirty()) {
			return;
		}
		
		MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("region");
		FindIterable<Document> query = collection.find(Filters.eq("regionName", region.getName()));
		Document document = query.first();
		
		Document newDoc = document(region);
		
		if (document != null) {
			collection.replaceOne(document, newDoc);
		} else {
			collection.insertOne(newDoc);
		}
		region.setDirty(true);
		SerennoCrimson.get().getLogger().info("Saved region: " + region.getName());
		Bukkit.broadcast(ChatColor.YELLOW + "Region: " + ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " has been saved.", "arena.engineer");
	}
	
	public void saveRegion(Region region) {		
		if (!region.isDirty()) {
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				unsafeSaveRegion(region);
			}
			
		}.runTaskAsynchronously(SerennoCrimson.get());
	}
	
	public void saveRegion(Player player, String name) {
		Region region = getRegion(name);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
		}
		
		saveRegion(region);
		player.sendMessage(ChatColor.YELLOW + "You've saved region: " + ChatColor.GOLD + region.getName());
	}
	
	public Region createRegion(Player player, String name) {
		Clipboard cb = PlayerTools.getClipboard(player);
		if (cb == null) {
			return null;
		}
		
		return createRegion(player, name, cb.getP1(), cb.getP2());
	}
	
	public Region createRegion(Player player, String name, Location p1, Location p2) {
		if (regions.containsKey(name)) {
			player.sendMessage(ChatColor.RED + "That region already exists.");
			return null;
		}
		
		Region region = new SimpleRegion(name, p1, p2);
		Region overlap = regions.values().stream().filter(r -> r.overlaps(region)).findFirst().orElse(null);
		if (overlap != null) {
			player.sendMessage(ChatColor.RED + "Overlapping region:" + overlap.getName());
			return null;
		}
		
		region.getPossibleFlags().add(RegionFlags.BLOCK_BREAK);
		region.getPossibleFlags().add(RegionFlags.BLOCK_PLACE);
		region.getPossibleFlags().add(RegionFlags.PVP);
		region.getPossibleFlags().add(RegionFlags.PVE);
		region.getPossibleFlags().add(RegionFlags.DAMAGE_LOSS);
		region.getPossibleFlags().add(RegionFlags.HUNGER_LOSS);
		
		saveRegion(region);
		regions.put(region.getName(), region);
		return region;
	}
	
	public Region getRegion(String name) {
		return regions.get(name);
	}

	public Region getRegion(Location loc) {
		World world = loc.getWorld();
		for (Region r : regions.values()) {
			if (!world.getUID().equals(r.getParentWorld().getUID()) 
					&& !r.hasChildWorld(world)) {
				continue;
			}
			if (r.isInside(loc)) {
				return r;
			}
		}
		return null;
	}
	
	private void deleteRegion(Region region) {
		if (!MongoAPI.isConnected()) {
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("region");
                FindIterable<Document> query = collection.find(Filters.eq("regionName", region.getName()));
                Document document = query.first();

                if (document != null) {
                    collection.findOneAndDelete(document);
                    Bukkit.broadcast(ChatColor.YELLOW + "Region: " + ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " has been deleted.", "arena.engineer");
                }
			}
			
		}.runTaskAsynchronously(SerennoCrimson.get());
		
	}
	
	public void deleteRegion(Player player, String name) {
		Region region = getRegion(name);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
		}
		Bukkit.getPluginManager().callEvent(new RegionDeletionEvent(region));
		
		regions.remove(name);
		deleteRegion(region);
		player.sendMessage(ChatColor.YELLOW + "You've deleted region: " + ChatColor.GOLD + region.getName());
	}
	
	public void saveAll() {
		listRegions().stream().forEach(r -> {
			unsafeSaveRegion(r);
		});
	}
	
	public List<Region> listRegions() {
		return Lists.newArrayList(regions.values());
	}
	
}
