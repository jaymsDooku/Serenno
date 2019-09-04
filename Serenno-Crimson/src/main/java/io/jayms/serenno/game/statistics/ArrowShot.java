package io.jayms.serenno.game.statistics;

import java.util.UUID;

public class ArrowShot {

	private UUID shooterID;
	private UUID hitEntityID;
	private double distance;
	private double damage;
	private long time;
	
	public ArrowShot(UUID shooterID, UUID hitEntityID, double distance, double damage, long time) {
		super();
		this.shooterID = shooterID;
		this.hitEntityID = hitEntityID;
		this.distance = distance;
		this.damage = damage;
		this.time = time;
	}

	public UUID getShooterID() {
		return shooterID;
	}
	
	public UUID getHitEntityID() {
		return hitEntityID;
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
	
}
