package io.jayms.serenno.event.reinforcement;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;

public class ReinforcementCreationEvent extends ReinforcementEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	public ReinforcementCreationEvent(Reinforcement reinforcement, ReinforcementDataSource dataSource) {
		super(reinforcement, dataSource);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
