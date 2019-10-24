package io.jayms.serenno.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;

public class BastionBlueprintCreationEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private BastionBlueprint bastionBlueprint;
	private boolean cancelled = false;
	
	public BastionBlueprintCreationEvent(Player player, BastionBlueprint bastionBlueprint) {
		this.player = player;
		this.bastionBlueprint = bastionBlueprint;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setBastionBlueprint(BastionBlueprint bastionBlueprint) {
		this.bastionBlueprint = bastionBlueprint;
	}
	
	public BastionBlueprint getBastionBlueprint() {
		return bastionBlueprint;
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
