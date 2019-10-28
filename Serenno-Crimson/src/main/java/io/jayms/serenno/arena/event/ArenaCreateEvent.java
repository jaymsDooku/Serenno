package io.jayms.serenno.arena.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.arena.Arena;

public class ArenaCreateEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Arena arena;
	
	public ArenaCreateEvent(Arena arena) {
		this.arena = arena;
	}
	
	public void setArena(Arena arena) {
		this.arena = arena;
	}
	
	public Arena getArena() {
		return arena;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
