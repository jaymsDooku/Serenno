package io.jayms.serenno.model.citadel.reinforcement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.util.ChunkCache;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;

public class ReinforcementWorld {
	
	@FunctionalInterface
	public interface UnloadCallback {
		
		void unload();
		
	}

	private World world;
	private ReinforcementDataSource dataSource;
	private LoadingCache<ChunkCoord, ChunkCache<Reinforcement>> reinCache;
	
	public ReinforcementWorld(World world, ReinforcementDataSource source) {
		this.world = world;
		this.dataSource = source;
		reinCache = Caffeine.newBuilder()
				.removalListener(new RemovalListener<ChunkCoord, ChunkCache<Reinforcement>>() {
					
					@Override
					public void onRemoval(@Nullable ChunkCoord key, @Nullable ChunkCache<Reinforcement> value,
							@NonNull RemovalCause cause) {
						if (dataSource != null) {
							dataSource.persistAll(value.getAll(), () -> {
								value.unload();
							});
						}
					}
				
				})
				.build((key) -> {
					Map<Coords, Reinforcement> reins = dataSource != null ? dataSource.getAll(key) : null;
					return new ChunkCache<Reinforcement>(key, reins, null);
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
