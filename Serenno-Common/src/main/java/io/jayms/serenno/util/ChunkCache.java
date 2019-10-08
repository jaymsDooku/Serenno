package io.jayms.serenno.util;

import java.util.Collection;

import org.bukkit.block.Block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

public class ChunkCache<T> {

	private ChunkCoord chunkPair;
	private LoadingCache<Coords, T> cache;
	private boolean dirty = false;
	
	public ChunkCache(ChunkCoord chunkPair, CacheLoader<Coords, T> loader, RemovalListener<Coords, T> remover) {
		this.chunkPair = chunkPair;
		cache = CacheBuilder.newBuilder()
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
		return cache.getIfPresent(coords);
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
