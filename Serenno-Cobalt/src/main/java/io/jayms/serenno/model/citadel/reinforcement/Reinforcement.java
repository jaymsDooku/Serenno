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
	private int chunkX;
	private int chunkZ;
	
	private Cooldown<Player> lastReinforcementDamage;
	private long creationTime;
	
	private String group;
	private boolean inMemory;
	private boolean broken;
	private boolean dirty;

	private ReinforcementWorld reinforcementWorld;

	private Reinforcement() {
	}

	private Reinforcement(Builder builder) {
		this.id = builder.getId();
		this.placer = builder.getPlacer();
		this.blueprint = builder.getBlueprint();
		this.health = builder.getHealth();
		this.loc = builder.getLoc();
		this.chunkX = builder.getChunkX();
		this.chunkZ = builder.getChunkZ();
		this.creationTime = builder.getCreationTime();
		this.group = builder.getGroup();
		this.inMemory = builder.isInMemory();
		this.dirty = inMemory;
	}

	public Reinforcement clone(ReinforcementWorld world) {
		Reinforcement reinforcement = new Reinforcement();
		reinforcement.id = this.id;
		reinforcement.placer = this.placer;
		reinforcement.health = this.health;
		reinforcement.blueprint = this.blueprint;

		reinforcement.loc = this.loc.clone();
		reinforcement.loc.setWorld(world.getWorld());
		reinforcement.chunkX = chunkX;
		reinforcement.chunkZ = chunkZ;

		reinforcement.inMemory = this.inMemory;
		reinforcement.dirty = inMemory;
		return reinforcement;
	}

	public void setReinforcementWorld(ReinforcementWorld reinforcementWorld) {
		this.reinforcementWorld = reinforcementWorld;
	}

	public boolean repair() {
		int cx = loc.getChunk().getX();
		int cz = loc.getChunk().getZ();
		boolean repaired = false;
		if (cx != chunkX) {
			chunkX = cx;
			setDirty(true);
			repaired = true;
		}
		if (cz != chunkZ) {
			chunkZ = cz;
			setDirty(true);
			repaired = true;
		}
		return repaired;
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

	public void setHealth(double health) {
		this.health = health;
	}

	public double getHealth() {
		double health = this.health;
		if (reinforcementWorld != null) {
			health *= reinforcementWorld.getScaling();
		}
		return health;
	}

	public double getMaxHealth() {
		double maxHealth = blueprint.getMaxHealth();
		if (reinforcementWorld != null) {
			maxHealth *= reinforcementWorld.getScaling();
		}
		return maxHealth;
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
			damage /= progress;
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

	public String getGroupName() {
		return group;
	}
	
	public Group getGroup() {
		return reinforcementWorld.getGroupMap().get(group.toLowerCase());
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
		return getGroup().isAuthorized(player, permission);
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
		dmg /= reinforcementWorld.getScaling();
		
		if (lastReinforcementDamage == null) {
			lastReinforcementDamage = new Cooldown<>();
		}
		if (player != null && lastReinforcementDamage.isOnCooldown(player) && lastReinforcementDamage.getTimeLeft(player) > 500) {
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
		UUID id;
		if (obj instanceof Reinforcement) {
			Reinforcement rein = (Reinforcement) obj;
			id = rein.getID();
		} else if (obj instanceof UUID) {
			id = (UUID) obj;
		} else {
			return false;
		}
		return this.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return "Reinforcement [id=" + id + ", placer=" + placer + ", blueprint=" + blueprint.getName() + ", health=" + health
				+ ", loc=" + loc + ", creationTime=" + creationTime + ", group=" + group + ", inMemory=" + inMemory
				+ ", broken=" + broken + "]";
	}

	public static Builder builder() {
		return new Builder();
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public static class Builder {
		
		private UUID id;
		private UUID placer;
		private ReinforcementBlueprint blueprint;
		private double health;
		private Location loc;
		private int chunkX;
		private int chunkZ;
		private long creationTime = -1;
		private String group;
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

		public Builder chunkX(int chunkX) {
			this.chunkX = chunkX;
			return this;
		}

		public Builder chunkZ(int chunkZ) {
			this.chunkZ = chunkZ;
			return this;
		}
		
		public Builder creationTime(long creationTime) {
			this.creationTime = creationTime;
			return this;
		}
		
		public Builder group(String group) {
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

		public int getChunkX() {
			return chunkX;
		}

		public int getChunkZ() {
			return chunkZ;
		}

		public long getCreationTime() {
			return creationTime;
		}
		
		public String getGroup() {
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
