package io.jayms.serenno.game;

import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.player.SerennoPlayer;

public class DuelRequest {

	private SerennoPlayer sender;
	private DuelType duelType;
	private Arena map;
	
	public DuelRequest(SerennoPlayer sender, DuelType duelType, Arena map) {
		this.sender = sender;
		this.duelType = duelType;
		this.map = map;
	}
	
	public SerennoPlayer getSender() {
		return sender;
	}

	public DuelType getDuelType() {
		return duelType;
	}

	public Arena getMap() {
		return map;
	}
	
}
