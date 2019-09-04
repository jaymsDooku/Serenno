package io.jayms.serenno.event.reinforcement;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;

public class ReinforcementCreationEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Reinforcement reinforcement;
	private ReinforcementDataSource dataSource;
	
	public ReinforcementCreationEvent(Reinforcement reinforcement, ReinforcementDataSource dataSource) {
		this.reinforcement = reinforcement;
		this.dataSource = dataSource;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public ReinforcementDataSource getDataSource() {
		return dataSource;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
