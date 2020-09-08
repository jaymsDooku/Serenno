package io.jayms.serenno.util;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.cache.CacheLoader;
import org.bukkit.block.Block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;

public class ChunkCache<T> {

	private ChunkCoord chunkPair;
	private Map<Coords, T> cache;
	private boolean dirty = false;
	
	public ChunkCache(ChunkCoord chunkPair) {
		this.chunkPair = chunkPair;
		this.cache = new ConcurrentHashMap<>();
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
		return cache.values();
	}
	
	public void delete(Coords coords) {
		cache.remove(coords);
	}
	
	public void unload() {
		cache.clear();
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public String toString() {
		return "ChunkCache [chunkPair=" + chunkPair + ", cache=" + cache.toString() + ", dirty=" + dirty + "]";
	}
	
}
