package io.jayms.serenno.team.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;

public class PlayerJoinTeamEvent extends TeamEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private SerennoPlayer joining;
	
	public PlayerJoinTeamEvent(SerennoPlayer joining, Team team) {
		super(team);
		this.joining = joining;
	}
	
	public SerennoPlayer getJoining() {
		return joining;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
