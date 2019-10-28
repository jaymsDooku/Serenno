package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class PlayerAcidBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private Reinforcement acidReinforcement;
	private Reinforcement victimReinforcement;
	private boolean cancelled = false;
	
	public PlayerAcidBlockEvent(Player player, Reinforcement acidReinforcement, Reinforcement victimReinforcement) {
		this.player = player;
		this.acidReinforcement = acidReinforcement;
		this.victimReinforcement = victimReinforcement;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Reinforcement getAcidReinforcement() {
		return acidReinforcement;
	}
	
	public Reinforcement getVictimReinforcement() {
		return victimReinforcement;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
