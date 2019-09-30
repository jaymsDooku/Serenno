package io.jayms.serenno.team.event;

import org.bukkit.event.Event;

import io.jayms.serenno.team.Team;

public abstract class TeamEvent extends Event {
	 
	private final Team team;

	protected TeamEvent(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}
}
