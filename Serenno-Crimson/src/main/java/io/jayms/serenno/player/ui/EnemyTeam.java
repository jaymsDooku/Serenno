package io.jayms.serenno.player.ui;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class EnemyTeam implements UITeam {

	public static final EnemyTeam TEAM = new EnemyTeam();
	
	private EnemyTeam() {
	}
	
	@Override
	public String getName() {
		return "enemy";
	}
	
	@Override
	public ChatColor getColour() {
		return ChatColor.RED;
	}
	
}
