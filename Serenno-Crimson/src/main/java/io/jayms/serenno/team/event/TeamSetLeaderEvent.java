package io.jayms.serenno.team.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;

public class TeamSetLeaderEvent extends TeamEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private SerennoPlayer oldLeader;
	private SerennoPlayer newLeader;
	
	public TeamSetLeaderEvent(SerennoPlayer oldLeader, SerennoPlayer newLeader, Team team) {
		super(team);
		this.oldLeader = oldLeader;
		this.newLeader = newLeader;
	}
	
	public SerennoPlayer getOldLeader() {
		return oldLeader;
	}
	
	public SerennoPlayer getNewLeader() {
		return newLeader;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
	