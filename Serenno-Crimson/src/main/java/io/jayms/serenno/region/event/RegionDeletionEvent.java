package io.jayms.serenno.region.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.region.Region;

public class RegionDeletionEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Region deleted;

	public Region getDeleted() {
		return deleted;
	}
	
	public RegionDeletionEvent(Region deleted) {
		this.deleted = deleted;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}

