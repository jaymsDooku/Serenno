package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.List;

import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;
import net.md_5.bungee.api.ChatColor;

public class DuelTeam {

	private ChatColor teamColor;
	private Team team;
	private boolean temporary;
	private List<SerennoPlayer> dead = new ArrayList<>();
	private List<SerennoPlayer> alive;
	
	public DuelTeam(ChatColor teamColor, Team team, boolean temporary) {
		this.teamColor = teamColor;
		this.team = team;
		this.temporary = temporary;
		this.alive = team.getAll();
	}
	
	public ChatColor getTeamColor() {
		return teamColor;
	}
	
	public Team getTeam() {
		return team;
	}

	public boolean isTemporary() {
		return temporary;
	}
	
	public List<SerennoPlayer> getAlive() {
		return alive;
	}
	
	public void die(SerennoPlayer player) {
		if (!team.inTeam(player)) {
			return;
		}
		
		dead.add(player);
		alive.remove(player);
	}
	
	public int alive() {
		return alive.size();
	}

	@Override
	public String toString() {
		return "DuelTeam [teamColor=" + teamColor.name() + ", team=" + team + ", temporary=" + temporary + ", dead=" + dead
				+ ", alive=" + alive + "]";
	}
	
}
