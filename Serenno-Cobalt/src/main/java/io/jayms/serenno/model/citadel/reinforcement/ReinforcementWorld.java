package io.jayms.serenno.model.citadel.reinforcement;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.jayms.serenno.util.ChunkCache;
import io.jayms.serenno.util.ChunkCoord;

public class ReinforcementWorld {

	private World world;
	private ReinforcementDataSource dataSource;
	private LoadingCache<ChunkCoord, ChunkCache<Reinforcement>> reinCache;
	
	public ReinforcementWorld(World world, ReinforcementDataSource source) {
		this.world = world;
		this.dataSource = source;
		reinCache = Caffeine.newBuilder()
				.maximumSize(2000)
				.expireAfterAccess(20, TimeUnit.MINUTES)
				.build(k -> {
					return new ChunkCache<>(k,
					(coord) -> {
						return dataSource != null ? dataSource.get(new ReinforcementKey(k, coord)) : null;
					},
					(coord, rein, cause) -> {
						
					});
				});
	}
	
	public ChunkCache<Reinforcement> getChunkCache(Block b) {
		return getChunkCache(ChunkCoord.fromBlock(b));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(Location l) {
		return getChunkCache(ChunkCoord.fromLocation(l));
	}
	
	public ChunkCache<Reinforcement> getChunkCache(ChunkCoord cc) {
		return reinCache.get(cc);
	}
	
	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}
	
	public World getWorld() {
		return world;
	}
	
}
