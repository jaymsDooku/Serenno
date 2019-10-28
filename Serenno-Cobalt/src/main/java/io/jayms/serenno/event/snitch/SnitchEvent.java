package io.jayms.serenno.event.snitch;

import org.bukkit.event.Event;

import io.jayms.serenno.model.citadel.snitch.Snitch;

public abstract class SnitchEvent extends Event {
	
	private Snitch snitch;
	
	public SnitchEvent(Snitch snitch) {
		this.snitch = snitch;
	}
	
	public Snitch getSnitch() {
		return snitch;
	}

}