package io.jayms.serenno.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;

public class ViewReinforcementBlueprintEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private String blueprintName;
	private ReinforcementBlueprint reinforcementBlueprint;
	
	public ViewReinforcementBlueprintEvent(Player player, String blueprintName, ReinforcementBlueprint bastionBlueprint) {
		this.player = player;
		this.blueprintName = blueprintName;
		this.reinforcementBlueprint = bastionBlueprint;
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
