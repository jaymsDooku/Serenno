package io.jayms.serenno.player.ui;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class LobbyTeam implements UITeam {

	public static final LobbyTeam TEAM = new LobbyTeam();
	
	private LobbyTeam() {
	}
	
	@Override
	public String getName() {
		return "lobby";
	}
	
	@Override
	public ChatColor getColour() {
		return ChatColor.YELLOW;
	}
	
	
}
