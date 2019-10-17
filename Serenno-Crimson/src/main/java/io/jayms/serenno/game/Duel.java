package io.jayms.serenno.game;

import io.jayms.serenno.game.menu.MatchReportMenu;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public interface Duel extends Game {

	DuelTeam getTeam1();
	
	DuelTeam getTeam2();
	
	DuelTeam getWinner();
	
	DuelTeam getLoser();
	
	DuelTeam getTeam(SerennoPlayer player);
	
	DuelTeam getOtherTeam(DuelTeam team);
	
	DuelTeam getTeam(ChatColor teamColor);
	
	DuelStatistics getStatistics();
	
	MatchReportMenu getMatchReportMenu();
	
	DuelType getDuelType();
	
	void die(SerennoPlayer player);
	
	void finish(DuelTeam winners, DuelTeam losers);

}
