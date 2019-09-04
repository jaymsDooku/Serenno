package io.jayms.serenno.game.statistics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class PotionThrow {

	private UUID throwerID;
	private List<PotionEffect> effects;
	private long time;
	private Map<LivingEntity, Double> affectedEntities;
	
	public PotionThrow(UUID throwerID, List<PotionEffect> effects, long time, Map<LivingEntity, Double> affectedEntities) {
		super();
		this.throwerID = throwerID;
		this.effects = effects;
		this.time = time;
		this.affectedEntities = affectedEntities;
	}

	public UUID getThrowerID() {
		return throwerID;
	}

	public List<PotionEffect> getEffects() {
		return effects;
	}

	public long getTime() {
		return time;
	}

	public Map<LivingEntity, Double> getAffectedEntities() {
		return affectedEntities;
	}
	
}
