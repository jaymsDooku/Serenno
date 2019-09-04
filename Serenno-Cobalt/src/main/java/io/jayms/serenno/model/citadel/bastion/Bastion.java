package io.jayms.serenno.model.citadel.bastion;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import vg.civcraft.mc.civmodcore.locations.QTBox;

public class Bastion implements QTBox, Comparable<Bastion> {

	private Reinforcement reinforcement;
	private BastionBlueprint blueprint;
	private BastionFieldLogic fieldLogic;
	
	public Bastion(Reinforcement reinforcement, BastionBlueprint blueprint) {
		this.reinforcement = reinforcement;
		this.blueprint = blueprint;
		this.fieldLogic = BastionShape.getFieldLogic(blueprint.getShape(), this);
	}
	
	public Location getLocation() {
		return fieldLogic.getLocation();
	}
	
	public boolean inField(Location loc) {
		return fieldLogic.inField(loc);
	}
	
	public BastionBlueprint getBlueprint() {
		return blueprint;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public int getRadius() {
		return blueprint.getRadius();
	}
	
	public void damage(double dmg) {
		reinforcement.damage(dmg);
	}
	
	public void destroy() {
		reinforcement.destroy();
	}
	
	@Override
	public int compareTo(Bastion other) {
		Location location = getLocation();
		UUID thisWorld = location.getWorld().getUID();
		int thisX = location.getBlockX();
		int thisY = location.getBlockY();
		int thisZ = location.getBlockZ();

		Location otherLocation = other.getLocation();
		UUID otherWorld = otherLocation.getWorld().getUID();
		int otherX = otherLocation.getBlockX();
		int otherY = otherLocation.getBlockY();
		int otherZ = otherLocation.getBlockZ();

		int worldCompare = thisWorld.compareTo(otherWorld);
		if (worldCompare != 0) {
			return worldCompare;
		}

		if (thisX < otherX) {
			return -1;
		}
		if (thisX > otherX) {
			return 1;
		}

		if (thisY < otherY) {
			return -1;
		}
		if (thisY > otherY) {
			return 1;
		}
		
		if (thisZ < otherZ) {
			return -1;
		}
		if (thisZ > otherZ) {
			return 1;
		}

		return 0;
	}

	@Override
	public int qtXMin() {
		return qtXMid() - getRadius();
	}

	@Override
	public int qtXMid() {
		return getLocation().getBlockX();
	}

	@Override
	public int qtXMax() {
		return qtXMid() + getRadius();
	}

	@Override
	public int qtZMin() {
		return qtZMid() - getRadius();
	}

	@Override
	public int qtZMid() {
		return getLocation().getBlockZ();
	}

	@Override
	public int qtZMax() {
		return qtZMid() + getRadius();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(qtXMid(), getLocation().getBlockY(), qtZMid());
	}
	
}
