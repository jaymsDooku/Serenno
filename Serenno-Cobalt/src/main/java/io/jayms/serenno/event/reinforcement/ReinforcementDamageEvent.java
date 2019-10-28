package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class ReinforcementDamageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Reinforcement reinforcement;
	private double damage;
	private boolean cancelled = false;
	
	public ReinforcementDamageEvent(Reinforcement reinforcement, double damage) {
		this.reinforcement = reinforcement;
		this.damage = damage;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}

