package io.jayms.serenno.model.citadel.bastion;

import java.util.Objects;
import java.util.UUID;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bukkit.Location;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import vg.civcraft.mc.civmodcore.locations.QTBox;

public class Bastion implements QTBox, Comparable<Bastion> {

	private UUID reinforcementID;
	private BastionBlueprint blueprint;
	private BastionFieldLogic fieldLogic;
	private int blockX;
	private int blockY;
	private int blockZ;

	public Bastion(Reinforcement reinforcement, BastionBlueprint blueprint) {
		this(reinforcement.getID(), blueprint, reinforcement.getLocation().getBlockX(), reinforcement.getLocation().getBlockY(), reinforcement.getLocation().getBlockZ());
	}

	public Bastion(UUID reinforcementID, BastionBlueprint blueprint, int blockX, int blockY, int blockZ) {
		this.reinforcementID = reinforcementID;
		this.blueprint = blueprint;
		this.fieldLogic = BastionShape.getFieldLogic(blueprint.getShape(), this);
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
	}

	public UUID getReinforcementID() {
		return reinforcementID;
	}

	public BastionFieldLogic getFieldLogic() {
		return fieldLogic;
	}

	public void setBlueprint(BastionBlueprint blueprint) {
		this.blueprint = blueprint;
	}
	
	public BastionBlueprint getBlueprint() {
		return blueprint;
	}
	
	public Reinforcement getReinforcement(ReinforcementWorld world) {
		return world.getReinforcement(reinforcementID);
	}
	
	public int getRadius() {
		return blueprint.getRadius();
	}
	
	@Override
	public int compareTo(Bastion other) {
		int otherX = other.blockX;
		int otherY = other.blockY;
		int otherZ = other.blockZ;

		if (blockX < otherX) {
			return -1;
		}
		if (blockX > otherX) {
			return 1;
		}

		if (blockY < otherY) {
			return -1;
		}
		if (blockY > otherY) {
			return 1;
		}
		
		if (blockZ < otherZ) {
			return -1;
		}
		if (blockZ > otherZ) {
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
		return blockX;
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
		return blockZ;
	}

	@Override
	public int qtZMax() {
		return qtZMid() + getRadius();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(qtXMid(), blockY, qtZMid());
	}
	
}
