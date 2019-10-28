package io.jayms.serenno.vault.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.vault.Core;

public class CoreDamageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player damager;
	private Core core;
	private double damage;
	private boolean cancelled = false;
	
	public CoreDamageEvent(Player damager, Core core, double damage) {
		this.damager = damager;
		this.core = core;
		this.damage = damage;
	}
	
	public Player getDamager() {
		return damager;
	}
	
	public Core getCore() {
		return core;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public double getDamage() {
		return damage;
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
