package io.jayms.serenno.event.bastion;

import org.bukkit.event.Event;

import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;

public abstract class BastionEvent extends Event {
	
	private Bastion bastion;
	private BastionDataSource dataSource;
	
	public BastionEvent(Bastion bastion, BastionDataSource dataSource) {
		this.bastion = bastion;
		this.dataSource = dataSource;
	}
	
	public Bastion getBastion() {
		return bastion;
	}
	
	public BastionDataSource getDataSource() {
		return dataSource;
	}

}

