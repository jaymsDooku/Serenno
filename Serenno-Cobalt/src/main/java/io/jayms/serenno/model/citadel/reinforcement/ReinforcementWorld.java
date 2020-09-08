package io.jayms.serenno.model.citadel.reinforcement;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.google.common.cache.*;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.util.ChunkCache;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;

public class ReinforcementWorld {
	
	@FunctionalInterface
	public interface UnloadCallback {
		
		void unload();
		
	}

	private String world;
	private ReinforcementDataSource dataSource;
	private Map<String, Group> groupMap;
	private Map<ChunkCoord, ChunkCache<Reinforcement>> reinCache;
	private Map<UUID, Reinforcement> uuidReinforcementMap;
	private double scaling;
	
	public ReinforcementWorld(String world, Map<String, Group> groupMap, ReinforcementDataSource dataSource, double scaling) {
		this.world = world;
		this.dataSource = dataSource;
		this.groupMap = groupMap;
		this.reinCache = new ConcurrentHashMap<>();
		this.uuidReinforcementMap = new ConcurrentHashMap<>();
		this.scaling = scaling;
	}

	private ReinforcementWorld parent;

	public static ReinforcementWorld clone(World world, ReinforcementWorld parent, Map<String, Group> groupMap, double scaling) {
		ReinforcementWorld result = new ReinforcementWorld(world.getName(), parent, groupMap, scaling);
		Collection<Reinforcement> all = parent.getAllReinforcements();
		for (Reinforcement rein : all) {
			Reinforcement reinforcement = rein.clone(result);
			reinforcement.setHealth(reinforcement.getBlueprint().getMaxHealth());

			Location loc = rein.getLocation();
			loc.setWorld(world);

			ChunkCoord chunkCoord = new ChunkCoord(rein.getChunkX(), rein.getChunkZ());
			ChunkCache<Reinforcement> cache = result.reinCache.get(chunkCoord);
			if (cache == null) {
				cache = new ChunkCache<>(chunkCoord);
				result.reinCache.put(chunkCoord, cache);
			}
			cache.put(Coords.fromLocation(loc), rein);
			result.uuidReinforcementMap.put(rein.getID(), rein);
			rein.setReinforcementWorld(result);
		}
		SerennoCobalt.get().getLogger().info("Loaded reinforcements for world: " + world.getName());
		return result;
	}

	private ReinforcementWorld(String world, ReinforcementWorld parent, Map<String, Group> groupMap, double scaling) {
		this.world = world;
		this.parent = parent;
		this.groupMap = groupMap;
		this.reinCache = new ConcurrentHashMap<>();
		this.uuidReinforcementMap = new ConcurrentHashMap<>();
		this.scaling = scaling;
	}

	public Map<String, Group> getGroupMap() {
		return groupMap;
	}

	public double getScaling() {
		return scaling;
	}

	public void putReinforcement(Reinforcement reinforcement) {
		Location location = reinforcement.getLocation();
		ChunkCache<Reinforcement> chunkCache = getChunkCache(location);
		Coords coords = Coords.fromLocation(location);
		/*Reinforcement current = chunkCache.get(coords);
		if (current != null)  {
			current.destroy();
		}*/
		chunkCache.put(coords, reinforcement);
		uuidReinforcementMap.put(reinforcement.getID(), reinforcement);
		reinforcement.setReinforcementWorld(this);
	}

	public void destroyReinforcement(Player player, Reinforcement reinforcement) {
		ReinforcementDestroyEvent reinDestroyEvent = player != null ? new PlayerReinforcementDestroyEvent(player, reinforcement, dataSource)
				: new ReinforcementDestroyEvent(reinforcement, dataSource);
		Bukkit.getPluginManager().callEvent(reinDestroyEvent);

		Location loc = reinforcement.getLocation();
		ChunkCache<Reinforcement> reinChunkCache = getChunkCache(loc);
		reinChunkCache.delete(Coords.fromLocation(loc));
		uuidReinforcementMap.remove(reinforcement.getID());
	}

	public ChunkCache<Reinforcement> getChunkCache(Block b) {
		return getChunkCache(ChunkCoord.fromBlock(b));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(Location l) {
		return getChunkCache(ChunkCoord.fromLocation(l));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(ChunkCoord cc) {
		ChunkCache<Reinforcement> chunkCache = reinCache.get(cc);
		if (chunkCache == null) {
			chunkCache = new ChunkCache<>(cc);
			reinCache.put(cc, chunkCache);
		}
		return chunkCache;
	}

	public Reinforcement getReinforcement(Block b) {
		return getReinforcement(b.getLocation());
	}

	public Reinforcement getReinforcement(Location l) {
		ChunkCache<Reinforcement> chunkCache = getChunkCache(l);
		return chunkCache.get(Coords.fromLocation(l));
	}

	public Reinforcement getReinforcement(UUID id) {
		//Reinforcement reinforcement = uuidReinforcementMap.get(id);
		/*if (reinforcement == null) {
			Set<Reinforcement> all = getAllReinforcements();
			List<Reinforcement> list = new ArrayList<>(all);
			Comparator<Reinforcement> comparator = new Comparator<Reinforcement>() {
				@Override
				public int compare(Reinforcement o1, Reinforcement o2) {
					return o1.getID().compareTo(o2.getID());
				}
			};
			Collections.sort(list, comparator);
			int index = Collections.binarySearch(list, Reinforcement.builder().id(id).build(), comparator);
			System.out.println("binary search index: " + index);
			reinforcement = list.get(index);
		}*/
		return uuidReinforcementMap.get(id);
	}

	public void unloadAll() {
		reinCache.clear();
		uuidReinforcementMap.clear();
	}

	public void load() {
		load(dataSource);
	}

	public void load(ReinforcementDataSource dataSource) {
		if (dataSource == null) {
			return;
		}
		Collection<Reinforcement> all = dataSource.getAll();
		for (Reinforcement rein : all) {
			Location loc = rein.getLocation();
			ChunkCoord chunkCoord = new ChunkCoord(rein.getChunkX(), rein.getChunkZ());
			ChunkCache<Reinforcement> cache = reinCache.get(chunkCoord);
			if (cache == null) {
				cache = new ChunkCache<>(chunkCoord);
				reinCache.put(chunkCoord, cache);
			}
			cache.put(Coords.fromLocation(loc), rein);
			uuidReinforcementMap.put(rein.getID(), rein);
			rein.setReinforcementWorld(this);
		}
		SerennoCobalt.get().getLogger().info("Loaded reinforcements for world: " + world);
	}

	public void save(UnloadCallback callback) {
		save(dataSource, callback);
	}

	public void save(ReinforcementDataSource dataSource, UnloadCallback callback) {
		dataSource.persistAll(getAllReinforcements(), callback);
	}
	
	public Set<Reinforcement> getAllReinforcements() {
		return new HashSet<>(uuidReinforcementMap.values());
	}

	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}
	
}
