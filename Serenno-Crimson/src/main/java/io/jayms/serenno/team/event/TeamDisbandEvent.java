package io.jayms.serenno.team.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.team.Team;

public class TeamDisbandEvent extends TeamEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	public TeamDisbandEvent(Team team) {
		super(team);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
