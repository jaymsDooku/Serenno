package io.jayms.serenno.game.statistics;

import java.util.UUID;

public class Hit {

	private UUID damagerID;
	private UUID victimID;
	private double distance;
	private double damage;
	private long time;
	private Double critMultiplier;
	
	public Hit(UUID damagerID, UUID victimID, double distance, double damage, long time, Double critMultiplier) {
		this.damagerID = damagerID;
		this.victimID = victimID;
		this.distance = distance;
		this.damage = damage;
		this.time = time;
		this.critMultiplier = critMultiplier;
	}

	public UUID getDamagerID() {
		return damagerID;
	}

	public UUID getVictimID() {
		return victimID;
	}

	public double getDistance() {
		return distance;
	}

	public double getDamage() {
		return damage;
	}
	
	public long getTime() {
		return time;
	}
	
	public Double getCritMultiplier() {
		return critMultiplier;
	}
	
	public boolean isCrit() {
		return critMultiplier != null;
	}
	
}
