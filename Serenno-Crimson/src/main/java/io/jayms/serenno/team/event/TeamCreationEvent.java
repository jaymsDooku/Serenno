package io.jayms.serenno.team.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;

public class TeamCreationEvent extends TeamEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private SerennoPlayer creator;
	
	public TeamCreationEvent(SerennoPlayer creator, Team team) {
		super(team);
		this.creator = creator;
	}
	
	public SerennoPlayer getCreator() {
		return creator;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
