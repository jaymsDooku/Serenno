package io.jayms.serenno.event.snitch;

import org.bukkit.event.Event;

import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchDataSource;

public abstract class SnitchEvent extends Event {
	
	private Snitch snitch;
	private SnitchDataSource dataSource;
	
	public SnitchEvent(Snitch snitch, SnitchDataSource dataSource) {
		this.snitch = snitch;
		this.dataSource = dataSource;
	}
	
	public Snitch getSnitch() {
		return snitch;
	}
	
	public SnitchDataSource getDataSource() {
		return dataSource;
	}

}