package io.jayms.serenno.game;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class DuelUITeam implements UITeam {

	private ChatColor colour;
	
	public DuelUITeam(ChatColor colour) {
		this.colour = colour;
	}
	
	@Override
	public String getName() {
		return "team " + colour.name();
	}

	@Override
	public ChatColor getColour() {
		return colour;
	}

	
	
}
