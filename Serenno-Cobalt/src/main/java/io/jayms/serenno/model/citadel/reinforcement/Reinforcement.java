package io.jayms.serenno.model.citadel.reinforcement;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDamageEvent;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.Cooldown;
import net.md_5.bungee.api.ChatColor;

public class Reinforcement {
	
	private UUID id;
	private UUID placer;
	private ReinforcementBlueprint blueprint;
	private double health;
	private Location loc;
	
	private Cooldown<Player> lastReinforcementDamage;
	private long creationTime;
	
	private Group group;
	private boolean inMemory;
	private boolean broken;
	private boolean dirty;
	
	public Reinforcement(Builder builder) {
		this.id = builder.getId();
		this.placer = builder.getPlacer();
		this.blueprint = builder.getBlueprint();
		this.health = builder.getHealth();
		this.loc = builder.getLoc();
		this.creationTime = builder.getCreationTime();
		this.group = builder.getGroup();
		this.inMemory = builder.isInMemory();
		this.dirty = inMemory;
	}
	
	public UUID getID() {
		return id;
	}
	
	public UUID getPlacer() {
		return placer;
	}
	
	public ReinforcementBlueprint getBlueprint() {
		return blueprint;
	}
	
	public double getHealth() {
		return health;
	}
	
	public double getHealthAsPercentage() {
		return health / blueprint.getMaxHealth();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public double getProgress() {
		long maturationTime = blueprint.getMaturationTime();
		if (maturationTime <= 0) {
			return 1;
		}
		long curTime = System.currentTimeMillis();
		long elapsedTime = curTime - getCreationTime();
		double maturation = (double) elapsedTime / maturationTime;
		return maturation > 1 ? 1 : maturation;
	}
	
	public boolean isMature() {
		return getProgress() >= 1;
	}
	
	public double getDamage() {
		double progress = getProgress();
		double damage = blueprint.getDefaultDamage();
		if (progress < 1.0) {
			damage *= progress;
			damage *= blueprint.getMaturationScale();
		}
		return damage;
	}
	
	public double getAcidProgress() {
		long acidTime = blueprint.getAcidTime();
		if (acidTime <= 0) {
			return 1;
		}
		long curTime = System.currentTimeMillis();
		long elapsedTime = curTime - getCreationTime();
		double acid = (double) elapsedTime / acidTime;
		return acid > 1 ? 1 : acid;
	}
	
	public long getAcidTimeRemaining() {
		return (getCreationTime() + blueprint.getAcidTime()) - System.currentTimeMillis(); 
	}
	
	public Group getGroup() {
		return group;
	}
	
	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}
	
	public boolean isInMemory() {
		return inMemory;
	}
	
	public boolean isBroken() {
		return broken;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean hasPermission(Player player, String permission) {
		return group.isAuthorized(player, permission);
	}
	
	public boolean damage() {
		return damage(getDamage());
	}
	
	public boolean damage(double dmg) {
		return damage(null, dmg);
	}
	
	public boolean damage(Player player) {
		return damage(player, getDamage());
	}
	
	// alive = true, dead = false
	public boolean damage(Player player, double dmg) {
		PlayerReinforcementDamageEvent event = new PlayerReinforcementDamageEvent(player, this, dmg);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled()) {
			return true;
		}
		
		dmg = event.getDamage();
		
		if (lastReinforcementDamage == null) {
			lastReinforcementDamage = new Cooldown<>();
		}
		if (lastReinforcementDamage.isOnCooldown(player)) {
			player.sendMessage(ChatColor.RED + "Reinforcement is still on cooldown for " + lastReinforcementDamage.getReadableTimeLeft(player));
			return true;
		}
		
		Block block = loc.getBlock();
		block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 1);
		
		health -= dmg;
		lastReinforcementDamage.putOnCooldown(player, blueprint.getDamageCooldown());
		if (health < 0 ) {
			destroy();
			return false;
		}
		setDirty(true);
		return true;
	}
	
	public void destroy() {
		destroy(null);
	}
	
	public void destroy(Player player) {
		broken = true;
		if (health != 0) health = 0;
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().destroyReinforcement(player, this);
		setDirty(true);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reinforcement)) {
			return false;
		}
		
		Reinforcement rein = (Reinforcement) obj;
		return rein.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return "Reinforcement [id=" + id + ", placer=" + placer + ", blueprint=" + blueprint + ", health=" + health
				+ ", loc=" + loc + ", creationTime=" + creationTime + ", group=" + group + ", inMemory=" + inMemory
				+ ", broken=" + broken + "]";
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private UUID id;
		private UUID placer;
		private ReinforcementBlueprint blueprint;
		private double health;
		private Location loc;
		private long creationTime = -1;
		private Group group;
		private boolean inMemory = true;
		
		public Builder id(UUID id) {
			this.id = id;
			return this;
		}
		
		public Builder placer(Player player) {
			this.placer = player.getUniqueId();
			return this;
		}
		
		public Builder blueprint(ReinforcementBlueprint blueprint) {
			this.blueprint = blueprint;
			return this;
		}
		
		public Builder health(double health) {
			this.health = health;
			return this;
		}
		
		public Builder loc(Location loc) {
			this.loc = loc;
			return this;
		}
		
		public Builder creationTime(long creationTime) {
			this.creationTime = creationTime;
			return this;
		}
		
		public Builder group(Group group) {
			this.group = group;
			return this;
		}
		
		public Builder inMemory(boolean inMemory) {
			this.inMemory = inMemory;
			return this;
		}
		
		public UUID getId() {
			return id;
		}
		
		public UUID getPlacer() {
			return placer;
		}
		
		public ReinforcementBlueprint getBlueprint() {
			return blueprint;
		}
		
		public double getHealth() {
			return health;
		}
		
		public Location getLoc() {
			return loc;
		}
		
		public long getCreationTime() {
			return creationTime;
		}
		
		public Group getGroup() {
			return group;
		}
		
		public boolean isInMemory() {
			return inMemory;
		}
		
		public Reinforcement build() {
			if (id == null) id = UUID.randomUUID();
			if (creationTime == -1) creationTime = System.currentTimeMillis();
			return new Reinforcement(this);
		}
	}

}
