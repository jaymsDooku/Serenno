package io.jayms.serenno.event.bastion;

import org.bukkit.event.Event;

import io.jayms.serenno.model.citadel.bastion.Bastion;

public abstract class BastionEvent extends Event {
	
	private Bastion bastion;
	
	public BastionEvent(Bastion bastion) {
		this.bastion = bastion;
	}
	
	public Bastion getBastion() {
		return bastion;
	}

}

