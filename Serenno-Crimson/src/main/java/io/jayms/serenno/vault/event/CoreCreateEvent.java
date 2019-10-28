package io.jayms.serenno.vault.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.vault.Core;

public class CoreCreateEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player creator;
	private Core core;
	
	public CoreCreateEvent(Player creator, Core core) {
		this.creator = creator;
		this.core = core;
	}
	
	public Player getCreator() {
		return creator;
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
