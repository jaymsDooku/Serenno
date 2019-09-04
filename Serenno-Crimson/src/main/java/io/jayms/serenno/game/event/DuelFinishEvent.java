package io.jayms.serenno.game.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.Team;

public class DuelFinishEvent extends DuelEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private Team winners;
	private Team losers;
	
	public DuelFinishEvent(Duel duel) {
		super(duel);
	}
	
	public Team getWinners() {
		return winners;
	}
	
	public Team getLosers() {
		return losers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
