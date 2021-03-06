package io.jayms.serenno.model.citadel.artillery;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;

import vg.civcraft.mc.civmodcore.locations.QTBox;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;

public class ArtilleryWorld {

	private World world;
	private SparseQuadTree artilleries;
	
	public ArtilleryWorld(World world) {
		this.world = world;
		this.artilleries = new SparseQuadTree(900);
	}
	
	public void addArtillery(Artillery artillery) {
		artilleries.add(artillery);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Artillery> getArtilleries(Location loc) {
		Set<? extends QTBox> boxes = artilleries.find(loc.getBlockX(), loc.getBlockZ());
		if (boxes.isEmpty()) {
			return (Set<Artillery>) boxes;
		}
		return ((Set<Artillery>) boxes).stream().filter(b -> {
			int y = b.getLocation().getBlockY();
			int upperY = b.getUpperY();
			int lowerY = b.getLowerY();
			return loc.getBlockY() >= lowerY && loc.getBlockY() <= upperY;
		}).collect(Collectors.toSet());
	}
	
	@SuppressWarnings("unchecked")
	public Set<Artillery> getAllArtilleries() {
		Set<? extends QTBox> boxes = artilleries.getBoxes();
		return (Set<Artillery>) boxes;
	}
	
	public void deleteArtillery(Artillery artillery) {
		artilleries.remove(artillery);
	}
	
	public World getWorld() {
		return world;
	}
	
}
