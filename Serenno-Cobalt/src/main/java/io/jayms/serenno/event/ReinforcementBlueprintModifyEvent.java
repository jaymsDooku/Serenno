package io.jayms.serenno.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;

public class ReinforcementBlueprintModifyEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private String blueprintName;
	private ReinforcementBlueprint reinforcementBlueprint;
	
	public ReinforcementBlueprintModifyEvent(Player player, String blueprintName, ReinforcementBlueprint reinforcementBlueprint) {
		this.player = player;
		this.blueprintName = blueprintName;
		this.reinforcementBlueprint = reinforcementBlueprint;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getBlueprintName() {
		return blueprintName;
	}
	
	public void setReinforcementBlueprint(ReinforcementBlueprint reinforcementBlueprint) {
		this.reinforcementBlueprint = reinforcementBlueprint;
	}
	
	public ReinforcementBlueprint getReinforcementBlueprint() {
		return reinforcementBlueprint;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
