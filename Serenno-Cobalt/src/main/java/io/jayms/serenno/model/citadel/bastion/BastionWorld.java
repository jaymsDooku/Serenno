package io.jayms.serenno.model.citadel.bastion;

import java.util.Collection;
import java.util.Set;

import io.jayms.serenno.SerennoCobalt;
import org.bukkit.Location;
import org.bukkit.World;

import vg.civcraft.mc.civmodcore.locations.QTBox;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;

public class BastionWorld {

	private World world;
	private BastionDataSource dataSource;
	private SparseQuadTree bastions;
	
	public BastionWorld(World world, BastionDataSource dataSource) {
		this.world = world;
		this.dataSource = dataSource;
		this.bastions = new SparseQuadTree(900);
		if (dataSource != null) {
			Collection<Bastion> loadedBastions = dataSource.getAll(SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(world));
			for (Bastion b : loadedBastions) {
				bastions.add(b);
			}
		}
	}
	
	public void addBastion(Bastion b) {
		bastions.add(b);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Bastion> getBastions(Location loc) {
		Set<? extends QTBox> boxes = bastions.find(loc.getBlockX(), loc.getBlockZ());
		return (Set<Bastion>) boxes;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Bastion> getAllBastions() {
		Set<? extends QTBox> boxes = bastions.getBoxes();
		return (Set<Bastion>) boxes;
	}
	
	public void deleteBastion(Bastion bastion) {
		bastions.remove(bastion);
	}
	
	public World getWorld() {
		return world;
	}
	
	public BastionDataSource getDataSource() {
		return dataSource;
	}
	
}
