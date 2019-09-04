package io.jayms.serenno.db.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DBConnectEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private boolean connected;
	
	public boolean isConnected() {
		return connected;
	}
	
	public DBConnectEvent(boolean connected) {
		this.connected = connected;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}

