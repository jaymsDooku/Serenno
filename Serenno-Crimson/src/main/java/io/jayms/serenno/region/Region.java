package io.jayms.serenno.region;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public interface Region {

	String getName();
	
	void setDisplayName(String set);
	
	String getDisplayName();
	
	void setParentWorld(World world);
	
	World getParentWorld();
	
	Set<World> getChildWorlds();
	
	void setPoint1(Vector v);
	
	void setPoint2(Vector v);
	
	Vector getPoint1();
	
	Vector getPoint2();
	
	Location getParentLocation1();
	
	Location getParentLocation2();
	
	Location getChildLocation1(World childWorld);
	
	Location getChildLocation2(World childWorld);
	
	void newChildWorld(World world);
	
	boolean hasChildWorld(World world);
	
	Set<String> getFlags();
	
	List<String> getPossibleFlags();
	
	boolean isFlagEnabled(String flagName);
	
	boolean isInside(Location loc);
	
	boolean overlaps(Region region);
	
	boolean overlaps(Region region, World childWorld);
	
	void setDirty(boolean set);
	
	boolean isDirty();

	
}
