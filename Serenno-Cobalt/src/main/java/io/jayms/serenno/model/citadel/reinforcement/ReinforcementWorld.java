package io.jayms.serenno.model.citadel.reinforcement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.util.ChunkCache;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;

public class ReinforcementWorld {

	private World world;
	private ReinforcementDataSource dataSource;
	private LoadingCache<ChunkCoord, ChunkCache<Reinforcement>> reinCache;
	
	public ReinforcementWorld(World world, ReinforcementDataSource source) {
		this.world = world;
		this.dataSource = source;
		reinCache = CacheBuilder.newBuilder()
				.removalListener(new RemovalListener<ChunkCoord, ChunkCache<Reinforcement>>() {
					
					@Override
					public void onRemoval(RemovalNotification<ChunkCoord, ChunkCache<Reinforcement>> notification) {
						new BukkitRunnable() {
							@Override
							public void run() {
								if (dataSource != null) {
									dataSource.persistAll(notification.getValue().getAll());
								}
								notification.getValue().unload();
							}
						}.runTaskLater(SerennoCobalt.get(), 1L);
					}
					
				})
				.build(new CacheLoader<ChunkCoord, ChunkCache<Reinforcement>>() {
				
					@Override
					public ChunkCache<Reinforcement> load(ChunkCoord key) throws Exception {
						Map<Coords, Reinforcement> reins = dataSource != null ? dataSource.getAll(key) : null;
						return new ChunkCache<Reinforcement>(key, reins, null);
					}
				
				});
	}
	
	public ChunkCache<Reinforcement> getChunkCache(Block b) {
		return getChunkCache(ChunkCoord.fromBlock(b));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(Location l) {
		return getChunkCache(ChunkCoord.fromLocation(l));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(ChunkCoord cc) {
		try {
			return reinCache.get(cc);
		} catch (ExecutionException e) {
			SerennoCobalt.get().getLogger().warning("Failed to load from cache: " + e.getMessage());
			return null;
		}
	}
	
	public void loadChunkData(Chunk chunk) {
		reinCache.refresh(ChunkCoord.fromChunk(chunk));
	}
	
	public void unloadChunkData(Chunk chunk) {
		reinCache.invalidate(ChunkCoord.fromChunk(chunk));
	}
	
	public void unloadAll(boolean save) {
		reinCache.invalidateAll();
	}
	
	public Set<Reinforcement> getAllReinforcements() {
		Set<Reinforcement> reinforcements = new HashSet<>();
		
		for (ChunkCache<Reinforcement> chunks : reinCache.asMap().values()) {
			reinforcements.addAll(chunks.getAll());
		}
		
		return reinforcements;
	}
	
	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}
	
	public World getWorld() {
		return world;
	}
	
}
