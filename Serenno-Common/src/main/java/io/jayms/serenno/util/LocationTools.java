package io.jayms.serenno.util;

import org.bukkit.Location;

public final class LocationTools {

	public static boolean isBetween(Location l1, Location l2, Location p) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return false;
		}
		
		if (!(l1.getWorld().getUID().equals(p.getWorld().getUID()))) {
			return false;
		}
		
		int minX;
		int minY;
		int minZ;
		
		int maxX;
		int maxY;
		int maxZ;
		
		if (l1.getBlockX() > l2.getBlockX()) {
			minX = l2.getBlockX();
			maxX = l1.getBlockX();
		} else {
			minX = l1.getBlockX();
			maxX = l2.getBlockX();
		}
		
		if (l1.getBlockY() > l2.getBlockY()) {
			minY = l2.getBlockY();
			maxY = l1.getBlockY();
		} else {
			minY = l1.getBlockY();
			maxY = l2.getBlockY();
		}
		
		if (l1.getBlockZ() > l2.getBlockZ()) {
			minZ = l2.getBlockZ();
			maxZ = l1.getBlockZ();
		} else {
			minZ = l1.getBlockZ();
			maxZ = l2.getBlockZ();
		}
		
		int x = p.getBlockX();
		int y = p.getBlockY();
		int z = p.getBlockZ();
		
		return (minX <= x && minY <= y && minZ <= z) && (maxX >= x && maxY >= y && maxZ >= z);
	}
	
}
