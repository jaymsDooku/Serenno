package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;

public class PlayerReinforcementDestroyEvent extends ReinforcementDestroyEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player destroyer;
	
	public PlayerReinforcementDestroyEvent(Player destroyer, Reinforcement reinforcement, ReinforcementDataSource dataSource) {
		super(reinforcement, dataSource);
		this.destroyer = destroyer;
	}
	
	public Player getDestroyer() {
		return destroyer;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
