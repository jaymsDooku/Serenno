package io.jayms.serenno.player.ui;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class AllyTeam implements UITeam {

	public static final AllyTeam TEAM = new AllyTeam();
	
	private AllyTeam() {
	}
	
	@Override
	public String getName() {
		return "ally";
	}
	
	@Override
	public ChatColor getColour() {
		return ChatColor.GREEN;
	}
	
}
