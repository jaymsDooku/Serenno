package io.jayms.serenno.game.statistics;

import java.util.UUID;

public class Crit {

	private UUID damagerID;
	private UUID victimID;
	private double distance;
	private double critMultiplier;
	private long time;
	
	public Crit(UUID damagerID, UUID victimID, double distance, double critMultiplier, long time) {
		super();
		this.damagerID = damagerID;
		this.victimID = victimID;
		this.distance = distance;
		this.critMultiplier = critMultiplier;
		this.time = time;
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
	
	public double getCritMultiplier() {
		return critMultiplier;
	}
	
	public long getTime() {
		return time;
	}
	
}
