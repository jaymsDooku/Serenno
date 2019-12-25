package io.jayms.serenno.event.snitch;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.snitch.Snitch;

public class SnitchEnterEvent extends SnitchEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player entering;
	
	public SnitchEnterEvent(Snitch snitch, Player entering) {
		super(snitch);
		this.entering = entering;
	}
	
	public Player getEntering() {
		return entering;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
