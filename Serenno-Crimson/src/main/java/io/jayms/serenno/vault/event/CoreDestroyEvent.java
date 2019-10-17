package io.jayms.serenno.vault.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.vault.Core;

public class CoreDestroyEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player destroyer;
	private Core core;
	
	public CoreDestroyEvent(Player destroyer, Core core) {
		this.destroyer = destroyer;
		this.core = core;
	}
	
	public Player getDestroyer() {
		return destroyer;
	}
	
	public Core getCore() {
		return core;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
