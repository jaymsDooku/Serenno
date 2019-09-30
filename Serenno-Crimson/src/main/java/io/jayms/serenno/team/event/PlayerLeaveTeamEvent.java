package io.jayms.serenno.team.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;

public class PlayerLeaveTeamEvent extends TeamEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private SerennoPlayer leaving;
	
	public PlayerLeaveTeamEvent(SerennoPlayer leaving, Team team) {
		super(team);
		this.leaving = leaving;
	}
	
	public SerennoPlayer getLeaving() {
		return leaving;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
