package io.jayms.serenno.event.reinforcement;

import org.bukkit.event.Event;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;

public abstract class ReinforcementEvent extends Event {
	
	private Reinforcement reinforcement;
	private ReinforcementDataSource dataSource;
	
	public ReinforcementEvent(Reinforcement reinforcement, ReinforcementDataSource dataSource) {
		this.reinforcement = reinforcement;
		this.dataSource = dataSource;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}

}
