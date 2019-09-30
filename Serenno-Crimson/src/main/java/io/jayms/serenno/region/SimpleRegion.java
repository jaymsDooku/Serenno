package io.jayms.serenno.region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SimpleRegion implements Region {

	private String name;
	private String displayName;
	private World parentWorld;
	private Set<World> childWorlds;
	private Vector p1;
	private Vector p2;
	private Set<String> flags = new HashSet<>();
	private List<String> possibleFlags = new ArrayList<>();
	private boolean dirty = true;
	
	public SimpleRegion(String name, Location l1, Location l2) {
		parentWorld = l1.getWorld();
		
		if (!parentWorld.getUID().equals(l2.getWorld().getUID())) {
			throw new IllegalArgumentException("Tried to create a region with 2 points, not in the same world.");
		}
		
		this.name = name;
		this.displayName = name;
		
		Vector p1 = l1.toVector();
		Vector p2 = l2.toVector();
		
		this.childWorlds = new HashSet<>();
		this.p1 = Vector.getMinimum(p1, p2);
		this.p2 = Vector.getMaximum(p1, p2);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setDisplayName(String set) {
		displayName = set;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public void setParentWorld(World world) {
		this.parentWorld = world;
	}
	
	@Override
	public World getParentWorld() {
		return parentWorld;
	}
	
	@Override
	public Set<World> getChildWorlds() {
		return childWorlds;
	}
	
	@Override
	public void setPoint1(Vector v) {
		this.p1 = v;
	}

	@Override
	public Vector getPoint1() {
		return p1;
	}
	
	@Override
	public void setPoint2(Vector v) {
		this.p2 = v;
	}

	@Override
	public Vector getPoint2() {
		return p2;
	}
	
	@Override
	public Location getParentLocation1() {
		return new Location(parentWorld, p1.getX(), p1.getY(), p1.getZ());
	}

	@Override
	public Location getParentLocation2() {
		return new Location(parentWorld, p2.getX(), p2.getY(), p2.getZ());
	}

	@Override
	public Location getChildLocation1(World world) {
		if (!hasChildWorld(world)) {
			return null;
		}
		
		return new Location(world, p1.getX(), p1.getY(), p1.getZ());
	}

	@Override
	public Location getChildLocation2(World world) {
		if (!hasChildWorld(world)) {
			return null;
		}
		
		return new Location(world, p2.getX(), p2.getY(), p2.getZ());
	}
	
	@Override
	public boolean hasChildWorld(World world) {
		return childWorlds.stream().filter(w -> w.getUID().equals(world.getUID())).findFirst().isPresent();
	}
	
	@Override
	public void newChildWorld(World world) {
		childWorlds.add(world);
	}

	@Override
	public Set<String> getFlags() {
		return flags;
	}
	
	@Override
	public List<String> getPossibleFlags() {
		return possibleFlags;
	}

	@Override
	public boolean isFlagEnabled(String flagName) {
		return flags.contains(flagName);
	}

	@Override
	public boolean isInside(Location loc) {
		Vector vec = loc.toVector();
		return (p1.getX() <= vec.getX() &&
				p1.getY() <= vec.getY() && 
				p1.getZ() <= vec.getZ()) && 
				(p2.getX() >= vec.getX() &&
				 p2.getY() >= vec.getY() &&
				 p2.getZ() >= vec.getZ());
	}
	
	@Override
	public boolean overlaps(Region region) {
		if (isInside(region.getParentLocation1())) {
			return true;
		}
		if (isInside(region.getParentLocation2())) {
			return true;
		}
		if (region.isInside(getParentLocation1())) {
			return true;
		}
		if (region.isInside(getParentLocation2())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean overlaps(Region region, World world) {
		if (isInside(region.getChildLocation1(world))) {
			return true;
		}
		if (isInside(region.getChildLocation2(world))) {
			return true;
		}
		if (region.isInside(getChildLocation1(world))) {
			return true;
		}
		if (region.isInside(getChildLocation2(world))) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setDirty(boolean set) {
		dirty = set;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

}
