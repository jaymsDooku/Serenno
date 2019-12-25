package io.jayms.serenno.event.snitch;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.snitch.Snitch;

public class SnitchExitEvent extends SnitchEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player exiting;
	
	public SnitchExitEvent(Snitch snitch, Player exiting) {
		super(snitch);
		this.exiting = exiting;
	}
	
	public Player getExiting() {
		return exiting;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
