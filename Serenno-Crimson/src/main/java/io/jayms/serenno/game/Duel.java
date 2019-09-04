package io.jayms.serenno.game;

import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.player.SerennoPlayer;

public interface Duel extends Game {

	DuelTeam getTeam1();
	
	DuelTeam getTeam2();
	
	DuelTeam getWinner();
	
	DuelTeam getLoser();
	
	DuelTeam getTeam(SerennoPlayer player);
	
	DuelTeam getOtherTeam(DuelTeam team);
	
	DuelStatistics getStatistics();
	
	DuelType getDuelType();
	
	void die(SerennoPlayer player);
	
	void finish(DuelTeam winners, DuelTeam losers);
}
