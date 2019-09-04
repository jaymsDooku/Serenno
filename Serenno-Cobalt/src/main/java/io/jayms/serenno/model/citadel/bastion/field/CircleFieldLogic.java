package io.jayms.serenno.model.citadel.bastion.field;

import org.bukkit.Location;
import org.bukkit.block.Block;

import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionFieldLogic;

public class CircleFieldLogic implements BastionFieldLogic {
	
	private Bastion bastion;
	
	public CircleFieldLogic(Bastion bastion) {
		this.bastion = bastion;
	}
	
	public Location getLocation() {
		return bastion.getReinforcement().getLocation();
	}

	@Override
	public boolean inField(Block block) {
		return inField(block.getLocation());
	}

	@Override
	public boolean inField(Location loc) {
		BastionBlueprint bb = bastion.getBlueprint();
		double radius = bb.getRadius();
		Location bastionLoc = getLocation();
		if (!loc.getWorld().getUID().equals(bastionLoc.getWorld().getUID())) {
			return false;
		}
		if (loc.getBlockY() < bastionLoc.getBlockY()) {
			return false;
		}
		double distX = loc.getBlockX() - bastionLoc.getBlockX();
		double distZ = loc.getBlockZ() - bastionLoc.getBlockZ();
		double distXSquared = distX * distX;
		double distZSquared = distZ * distZ;
		double radiusSquared = radius * radius;
		double distXZSquared = distXSquared + distZSquared;
		return distXZSquared < radiusSquared;
	}

}
