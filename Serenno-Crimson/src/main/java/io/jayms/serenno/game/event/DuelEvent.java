package io.jayms.serenno.game.event;

import org.bukkit.event.Event;

import io.jayms.serenno.game.Duel;

public abstract class DuelEvent extends Event {
 
	private final Duel duel;

	protected DuelEvent(Duel duel) {
		this.duel = duel;
	}

	public Duel getDuel() {
		return duel;
	}
}
