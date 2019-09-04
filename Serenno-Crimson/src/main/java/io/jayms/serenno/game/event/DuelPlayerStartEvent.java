package io.jayms.serenno.game.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.game.Duel;
import io.jayms.serenno.player.SerennoPlayer;

public class DuelPlayerStartEvent extends DuelPlayerEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	public DuelPlayerStartEvent(Duel duel, SerennoPlayer player) {
		super(duel, player);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
