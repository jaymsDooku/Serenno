package io.jayms.serenno.game;

import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.player.SerennoPlayer;

public class DuelRequest {

	private Duelable sender;
	private DuelType duelType;
	private Arena map;
	
	public DuelRequest(Duelable sender, DuelType duelType, Arena map) {
		this.sender = sender;
		this.duelType = duelType;
		this.map = map;
	}
	
	public Duelable getSender() {
		return sender;
	}

	public DuelType getDuelType() {
		return duelType;
	}

	public Arena getMap() {
		return map;
	}
	
}
