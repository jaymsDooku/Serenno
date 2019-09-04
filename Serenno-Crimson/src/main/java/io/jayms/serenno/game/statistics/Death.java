package io.jayms.serenno.game.statistics;

import java.util.Map;
import java.util.UUID;

import org.bukkit.potion.PotionEffectType;

import io.jayms.serenno.kit.Kit;

public class Death {

	private UUID deadID;
	private Map<PotionEffectType, Double> potionDurations;
	private Kit finalInventory;
	private double finalHealth;
	private int finalHunger;
	
	public Death(UUID deadID, Map<PotionEffectType, Double> potionDurations, Kit finalInventory, double finalHealth, int finalHunger) {
		super();
		this.deadID = deadID;
		this.potionDurations = potionDurations;
		this.finalInventory = finalInventory;
		this.finalHealth = finalHealth;
		this.finalHunger = finalHunger;
	}

	public UUID getDeadID() {
		return deadID;
	}

	public Map<PotionEffectType, Double> getPotionDurations() {
		return potionDurations;
	}

	public Kit getFinalInventory() {
		return finalInventory;
	}
	
	public double getFinalHealth() {
		return finalHealth;
	}
	
	public int getFinalHunger() {
		return finalHunger;
	}
	
}
