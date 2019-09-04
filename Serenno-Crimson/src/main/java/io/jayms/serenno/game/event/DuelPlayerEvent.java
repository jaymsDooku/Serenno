package io.jayms.serenno.game.event;

import io.jayms.serenno.game.Duel;
import io.jayms.serenno.player.SerennoPlayer;

public abstract class DuelPlayerEvent extends DuelEvent {

	private SerennoPlayer player;
	
	public DuelPlayerEvent(Duel duel, SerennoPlayer player) {
		super(duel);
		this.player = player;
	}
	
	public SerennoPlayer getPlayer() {
		return player;
	}
	
}
