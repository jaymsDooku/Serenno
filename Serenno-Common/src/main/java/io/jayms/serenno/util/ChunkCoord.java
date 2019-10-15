package io.jayms.serenno.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.base.Objects;

public class ChunkCoord {
	
	public static ChunkCoord fromBlock(Block b) {
		return fromLocation(b.getLocation());
	}
	
	public static ChunkCoord fromLocation(Location l) {
		return fromChunk(l.getChunk());
	}
	
	public static ChunkCoord fromChunk(Chunk chunk) {
		return new ChunkCoord(chunk.getX(), chunk.getZ()); 
	}

	private int x;
	private int z;
	
	public ChunkCoord(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChunkCoord)) {
			return false;
		}
		
		ChunkCoord cc = (ChunkCoord) obj;
		return x == cc.x && z == cc.z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(x, z);
	}
	
}