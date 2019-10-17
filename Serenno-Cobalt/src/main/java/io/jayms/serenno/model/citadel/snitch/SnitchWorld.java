package io.jayms.serenno.model.citadel.snitch;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;

import vg.civcraft.mc.civmodcore.locations.QTBox;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;

public class SnitchWorld {

	private World world;
	private SnitchDataSource dataSource;
	private SparseQuadTree snitches;
	
	public SnitchWorld(World world, SnitchDataSource dataSource) {
		this.world = world;
		this.dataSource = dataSource;
		this.snitches = new SparseQuadTree(900);
		if (dataSource != null) {
			Set<Snitch> loadedSnitches = dataSource.getAll();
			for (Snitch s : loadedSnitches) {
				snitches.add(s);
			}
		}
	}
	
	public void addSnitch(Snitch s) {
		snitches.add(s);
		if (dataSource != null)
			dataSource.create(s);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Snitch> getSnitches(Location loc) {
		Set<? extends QTBox> boxes = snitches.find(loc.getBlockX(), loc.getBlockZ());
		if (boxes.isEmpty()) {
			return (Set<Snitch>) boxes;
		}
		return ((Set<Snitch>) boxes).stream().filter(b -> {
				int y = b.getLocation().getBlockY();
				int upperY = y + b.getRadius();
				int lowerY = y - b.getRadius();
				return loc.getBlockY() >= lowerY && loc.getBlockY() <= upperY;
			}).collect(Collectors.toSet());
	}
	
	@SuppressWarnings("unchecked")
	public Set<Snitch> getAllSnitches() {
		Set<? extends QTBox> boxes = snitches.getBoxes();
		return (Set<Snitch>) boxes;
	}
	
	public void deleteSnitch(Snitch snitch) {
		snitches.remove(snitch);
		if (dataSource != null) 
			dataSource.delete(snitch);
	}
	
	public World getWorld() {
		return world;
	}
	
	public SnitchDataSource getDataSource() {
		return dataSource;
	}
	
}
