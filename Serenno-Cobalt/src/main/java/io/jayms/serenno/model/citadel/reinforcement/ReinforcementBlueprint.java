package io.jayms.serenno.model.citadel.reinforcement;

import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.RegenRate;

public class ReinforcementBlueprint {
	
	private String name;
	private String displayName;
	private ItemStack itemStack;
	private RegenRate regenRate;
	private double maxHealth;
	private long maturationTime;
	private long acidTime;
	private long damageCooldown;
	private double defaultDamage;
	
	private ReinforcementBlueprint(Builder builder) {
		this.name = builder.getName();
		this.displayName = builder.getDisplayName();
		this.itemStack = builder.getItemStack();
		this.regenRate = builder.getRegenRate();
		this.maxHealth = builder.getMaxHealth();
		this.maturationTime = builder.getMaturationTime();
		this.acidTime = builder.getAcidTime();
		this.damageCooldown = builder.getDamageCooldown();
		this.defaultDamage = builder.getDefaultDamage();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public RegenRate getRegenRate() {
		return regenRate;
	}
	
	public double getMaxHealth() {
		return maxHealth;
	}
	
	public long getMaturationTime() {
		return maturationTime;
	}
	
	public long getAcidTime() {
		return acidTime;
	}
	
	public long getDamageCooldown() {
		return damageCooldown;
	}
	
	public double getDefaultDamage() {
		return defaultDamage;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String displayName;
		private ItemStack itemStack;
		private RegenRate regenRate;
		private double maxHealth;
		private long maturationTime;
		private long acidTime;
		private long damageCooldown;
		private double defaultDamage;
		
		private Builder() {
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public Builder itemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
			return this;
		}
		
		public ItemStack getItemStack() {
			return itemStack;
		}
		
		public Builder regenRate(RegenRate regenRate) {
			this.regenRate = regenRate;
			return this;
		}
		
		public RegenRate getRegenRate() {
			return regenRate;
		}
		
		public Builder maxHealth(double maxHealth) {
			this.maxHealth = maxHealth;
			return this;
		}
		
		public double getMaxHealth() {
			return maxHealth;
		}
		
		public Builder maturationTime(long maturationTime) {
			this.maturationTime = maturationTime;
			return this;
		}
		
		public long getMaturationTime() {
			return maturationTime;
		}
		
		public Builder acidTime(long acidTime) {
			this.acidTime = acidTime;
			return this;
		}
		
		public long getAcidTime() {
			return acidTime;
		}
		
		public Builder damageCooldown(long damageCooldown) {
			this.damageCooldown = damageCooldown;
			return this;
		}
		
		public long getDamageCooldown() {
			return damageCooldown;
		}
		
		public Builder defaultDamage(double defaultDamage) {
			this.defaultDamage = defaultDamage;
			return this;
		}
		
		public double getDefaultDamage() {
			return defaultDamage;
		}
		
		public ReinforcementBlueprint build() {
			return new ReinforcementBlueprint(this);
		}
		
	}
	
}
