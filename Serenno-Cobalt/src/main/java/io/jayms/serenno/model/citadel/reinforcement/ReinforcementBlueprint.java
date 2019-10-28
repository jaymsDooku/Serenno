package io.jayms.serenno.model.citadel.reinforcement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.RegenRate;
import net.md_5.bungee.api.ChatColor;

public class ReinforcementBlueprint {
	
	private String name;
	private String displayName;
	private ItemStack itemStack;
	private RegenRate regenRate;
	private double maxHealth;
	private double maturationScale;
	private long maturationTime;
	private long acidTime;
	private long damageCooldown;
	private double defaultDamage;
	private List<Material> reinforceableMaterials;
	private List<Material> unreinforceableMaterials;
	
	private ReinforcementBlueprint(Builder builder) {
		this.name = builder.getName();
		this.displayName = builder.getDisplayName();
		this.itemStack = builder.getItemStack();
		this.regenRate = builder.getRegenRate();
		this.maxHealth = builder.getMaxHealth();
		this.maturationScale = builder.getMaturationScale();
		this.maturationTime = builder.getMaturationTime();
		this.acidTime = builder.getAcidTime();
		this.damageCooldown = builder.getDamageCooldown();
		this.defaultDamage = builder.getDefaultDamage();
		this.reinforceableMaterials = builder.getReinforceableMaterials() == null ? new ArrayList<>() : builder.getReinforceableMaterials();
		this.unreinforceableMaterials = builder.getUnreinforceableMaterials() == null ? new ArrayList<>() : builder.getUnreinforceableMaterials();
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public void setRegenRate(RegenRate regenRate) {
		this.regenRate = regenRate;
	}
	
	public void setAcidTime(long acidTime) {
		this.acidTime = acidTime;
	}
	
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public void setMaturationTime(long maturationTime) {
		this.maturationTime = maturationTime;
	}
	
	public void setDamageCooldown(long damageCooldown) {
		this.damageCooldown = damageCooldown;
	}
	
	public void setDefaultDamage(double defaultDamage) {
		this.defaultDamage = defaultDamage;
	}
	
	public void setMaturationScale(double maturationScale) {
		this.maturationScale = maturationScale;
	}
	
	public double getMaturationScale() {
		return maturationScale;
	}
	
	public List<Material> getUnreinforceableMaterials() {
		return unreinforceableMaterials;
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
	
	public List<Material> getReinforceableMaterials() {
		return reinforceableMaterials;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReinforcementBlueprint)) {
			return false;
		}
		
		ReinforcementBlueprint blueprint = (ReinforcementBlueprint) obj;
		return getName().equals(blueprint.getName());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW + "Name: " + ChatColor.GOLD + name + "\n");
		sb.append(ChatColor.YELLOW + "Display Name: " + ChatColor.GOLD + displayName + "\n");
		sb.append(ChatColor.YELLOW + "Item Stack: " + ChatColor.GOLD + "[" + ChatColor.YELLOW + itemStack.getType().toString() + ChatColor.GOLD + "|" + ChatColor.YELLOW + itemStack.getAmount() + ChatColor.GOLD + "]\n");
		sb.append(ChatColor.YELLOW + "Max Health: " + ChatColor.GOLD + maxHealth + "\n");
		sb.append(ChatColor.YELLOW + "Maturation Time: " + ChatColor.GOLD + maturationTime + "\n");
		sb.append(ChatColor.YELLOW + "Maturation Scale: " + ChatColor.GOLD + maturationScale + "\n");
		sb.append(ChatColor.YELLOW + "Acid Time: " + ChatColor.GOLD + acidTime + "\n");
		sb.append(ChatColor.YELLOW + "Regen Rate: " + ChatColor.GOLD + regenRate + "\n");
		sb.append(ChatColor.YELLOW + "Damage Cooldown: " + ChatColor.GOLD + damageCooldown + "\n");
		sb.append(ChatColor.YELLOW + "Default Damage: " + ChatColor.GOLD + defaultDamage + "\n");
		sb.append(ChatColor.YELLOW + "Reinforceable Materials: " + ChatColor.GOLD + reinforceableMaterials + "\n");
		sb.append(ChatColor.YELLOW + "Unreinforceable Materials: " + ChatColor.GOLD + unreinforceableMaterials + "\n");
		return sb.toString();
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
		private double maturationScale;
		private long maturationTime;
		private long acidTime;
		private long damageCooldown;
		private double defaultDamage;
		private List<Material> reinforceableMaterials;
		private List<Material> unreinforceableMaterials;
		
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
		
		public Builder maturationScale(double set) {
			this.maturationScale = set;
			return this;
		}
		
		public double getMaturationScale() {
			return maturationScale;
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
		
		public Builder reinforceableMaterials(List<Material> reinforceableMaterials) {
			this.reinforceableMaterials = reinforceableMaterials;
			return this;
		}
		
		public List<Material> getReinforceableMaterials() {
			return reinforceableMaterials;
		}
		
		public Builder unreinforceableMaterials(List<Material> unreinforceableMaterials) {
			this.unreinforceableMaterials = unreinforceableMaterials;
			return this;
		}
		
		public List<Material> getUnreinforceableMaterials() {
			return unreinforceableMaterials;
		}
		
		public ReinforcementBlueprint build() {
			return new ReinforcementBlueprint(this);
		}
		
	}
	
}
