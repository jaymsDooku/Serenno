package io.jayms.serenno.util;

import java.util.Objects;

import org.bukkit.Location;

public class Coords {

	public static Coords fromLocation(Location l) {
		return new Coords(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	private int x;
	private int y;
	private int z;
	
	public Coords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Coords)) {
			return false;
		}
		
		Coords coords = (Coords) obj;
		return x == coords.x && y == coords.y && z == coords.z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
	
	
}
