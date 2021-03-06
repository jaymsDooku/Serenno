package io.jayms.serenno.event.bastion;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.bastion.Bastion;

public class BastionDestroyEvent extends BastionEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	public BastionDestroyEvent(Bastion bastion) {
		super(bastion);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
