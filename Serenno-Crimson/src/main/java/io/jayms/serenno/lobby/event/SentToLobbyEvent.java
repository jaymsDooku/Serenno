package io.jayms.serenno.lobby.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SentToLobbyEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	
	public SentToLobbyEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
