package io.jayms.serenno.model.citadel.reinforcement;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.group.Group;

public class Reinforcement {
	
	private UUID id;
	private UUID placer;
	private ReinforcementBlueprint blueprint;
	private double health;
	private Location loc;
	private long creationTime;
	private Group group;
	private boolean inMemory;
	private boolean broken;
	
	public Reinforcement(Builder builder) {
		this.id = builder.getId();
		this.placer = builder.getPlacer();
		this.blueprint = builder.getBlueprint();
		this.health = builder.getHealth();
		this.loc = builder.getLoc();
		this.creationTime = builder.getCreationTime();
		this.group = builder.getGroup();
		this.inMemory = builder.isInMemory();
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
	
	public Group getGroup() {
		return group;
	}
	
	public boolean isInMemory() {
		return inMemory;
	}
	
	public boolean isBroken() {
		return broken;
	}
	
	public boolean hasPermission(Player player, String permission) {
		return group.isAuthorized(player, permission);
	}
	
	// alive = true, dead = false
	public boolean damage(double dmg) {
		health -= dmg;
		if (health < 0 ) {
			health = 0;
			return false;
		}
		return true;
	}
	
	public void destroy() {
		broken = true;
		if (health != 0) health = 0;
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().destroyReinforcement(this);
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
