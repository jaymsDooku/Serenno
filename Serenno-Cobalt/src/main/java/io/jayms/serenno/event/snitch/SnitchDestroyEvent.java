package io.jayms.serenno.event.snitch;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.snitch.Snitch;

public class SnitchDestroyEvent extends SnitchEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	public SnitchDestroyEvent(Snitch snitch) {
		super(snitch);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
