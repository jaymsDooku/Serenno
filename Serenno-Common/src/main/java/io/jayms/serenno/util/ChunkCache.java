package io.jayms.serenno.util;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bukkit.block.Block;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;

public class ChunkCache<T> {

	private ChunkCoord chunkPair;
	private LoadingCache<Coords, T> cache;
	private boolean dirty = false;
	
	public ChunkCache(ChunkCoord chunkPair, CacheLoader<Coords, T> loader, RemovalListener<Coords, T> remover) {
		this.chunkPair = chunkPair;
		cache = Caffeine.newBuilder()
		.recordStats()
		.expireAfterAccess(5, TimeUnit.MINUTES)
		.maximumSize(70_000)
		.removalListener(remover)
		.build(loader);
	}
	
	public ChunkCoord getChunkPair() {
		return chunkPair;
	}
	
	public void put(int x, int y, int z, T v) {
		put(new Coords(x, y, z), v);
	}
	
	public void put(Coords coords, T v) {
		cache.put(coords, v);
	}
	
	public T get(Block block) {
		return get(new Coords(block.getX(), block.getY(), block.getZ()));
	}
	
	public T get(int x, int y, int z) {
		return get(new Coords(x, y, z));
	}
	
	public T get(Coords coords) {
		return cache.get(coords);
	}
	
	public Collection<T> getAll() {
		return cache.asMap().values();
	}
	
	public void delete(Coords coords) {
		cache.invalidate(coords);
	}
	
	public void unload() {
		cache.invalidateAll();
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public String toString() {
		return "ChunkCache [chunkPair=" + chunkPair + ", cache=" + cache.asMap().toString() + ", dirty=" + dirty + "]";
	}
	
}
