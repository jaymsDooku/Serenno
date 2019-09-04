package io.jayms.serenno;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class SerennoCrimsonConfigManager extends CoreConfigManager {
	
	public SerennoCrimsonConfigManager(ACivMod plugin) {
		super(plugin);
	}

	@Override
	protected boolean parseInternal(ConfigurationSection config) {
		lobbySpawn = Location.deserialize(config.getConfigurationSection("lobby.spawn").getValues(false));
		
		maxReach = config.getDouble("combat.maxReach");
		cpsLimit = config.getInt("combat.cpsLimit");
		critMultiplier = config.getInt("combat.critMultiplier");
		strengthMultiplierAddition = config.getDouble("combat.strengthMultiplierAddition");
		
		horizontalKb = config.getDouble("combat.kb.horizontal.normal");
		verticalKb = config.getDouble("combat.kb.vertical.normal");
		sprintHorizontalKb = config.getDouble("combat.kb.horizontal.sprint");
		sprintVerticalKb = config.getDouble("combat.kb.vertical.sprint");
		
		interruptSprint = config.getBoolean("combat.sprintInterrupt");
		
		potionPower = config.getDouble("combat.potion.power");
		potionVertical = config.getDouble("combat.potion.vertical");
		potionHorizontal = config.getDouble("combat.potion.horizontal");
		potionOffset = config.getDouble("combat.potion.offset");
		
		return true;
	}
	
	private Location lobbySpawn;
	
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
	private double maxReach;
	private int cpsLimit;
	private double critMultiplier;

	public double getMaxReach() {
		return maxReach;
	}

	public int getCpsLimit() {
		return cpsLimit;
	}
	
	public double getCritMultiplier() {
		return critMultiplier;
	}
	
	private double horizontalKb;
	private double verticalKb;
	private double sprintHorizontalKb;
	private double sprintVerticalKb;
	private boolean interruptSprint;

	public double getHorizontalKb() {
		return horizontalKb;
	}

	public double getVerticalKb() {
		return verticalKb;
	}
	
	public double getSprintHorizontalKb() {
		return sprintHorizontalKb;
	}
	
	public double getSprintVerticalKb() {
		return sprintVerticalKb;
	}
	
	public boolean isInterruptSprint() {
		return interruptSprint;
	}
	
	private double potionPower;
	private double potionHorizontal;
	private double potionVertical;
	private double potionOffset;

	public double getPotionPower() {
		return potionPower;
	}

	public double getPotionHorizontal() {
		return potionHorizontal;
	}

	public double getPotionVertical() {
		return potionVertical;
	}

	public double getPotionOffset() {
		return potionOffset;
	}
	
	private double strengthMultiplierAddition;
	
	public double getStrengthMultiplierAddition() {
		return strengthMultiplierAddition;
	}
	
}
