package io.jayms.serenno.model.citadel.reinforcement;

import java.util.concurrent.ExecutionException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
				.build(new CacheLoader<ChunkCoord, ChunkCache<Reinforcement>>() {
				
					@Override
					public ChunkCache<Reinforcement> load(ChunkCoord key) throws Exception {
						return new ChunkCache<Reinforcement>(key, 
								new CacheLoader<Coords, Reinforcement>() {
									
									@Override
									public Reinforcement load(Coords coords) {
										return dataSource != null ? dataSource.get(new ReinforcementKey(key, coords)) : null;
									}
							
								},
								new RemovalListener<Coords, Reinforcement>() {
									
									@Override
									public void onRemoval(RemovalNotification<Coords, Reinforcement> notification) {
										System.out.println("Removing reinforcement: " + notification.getValue());
										System.out.println("CAUSE: " + notification.getCause());
									}
								});
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
	
	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}
	
	public World getWorld() {
		return world;
	}
	
}
