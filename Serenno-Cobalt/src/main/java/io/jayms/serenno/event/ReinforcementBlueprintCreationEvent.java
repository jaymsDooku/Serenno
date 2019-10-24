package io.jayms.serenno.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;

public class ReinforcementBlueprintCreationEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private ReinforcementBlueprint reinforcementBlueprint;
	private boolean cancelled = false;
	
	public ReinforcementBlueprintCreationEvent(Player player, ReinforcementBlueprint bastionBlueprint) {
		this.player = player;
		this.reinforcementBlueprint = bastionBlueprint;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setReinforcementBlueprint(ReinforcementBlueprint reinforcementBlueprint) {
		this.reinforcementBlueprint = reinforcementBlueprint;
	}
	
	public ReinforcementBlueprint getReinforcementBlueprint() {
		return reinforcementBlueprint;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
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
