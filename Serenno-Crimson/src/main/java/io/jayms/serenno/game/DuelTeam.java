package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.List;

import io.jayms.serenno.player.SerennoPlayer;
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
	
	public void die(SerennoPlayer player) {
		if (!team.inTeam(player)) {
			return;
		}
		
		System.out.println("TEAM DIE");
		dead.add(player);
		alive.remove(player);
	}
	
	public int alive() {
		return alive.size();
	}
	
}
