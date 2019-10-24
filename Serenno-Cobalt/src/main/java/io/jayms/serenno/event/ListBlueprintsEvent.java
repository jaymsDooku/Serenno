package io.jayms.serenno.event;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;

public class ListBlueprintsEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private Collection<ReinforcementBlueprint> reinforcementBlueprints;
	private Collection<BastionBlueprint> bastionBlueprints;
	
	public ListBlueprintsEvent(Player player, Collection<ReinforcementBlueprint> reinforcementBlueprints, Collection<BastionBlueprint> bastionBlueprints) {
		this.player = player;
		this.reinforcementBlueprints = reinforcementBlueprints;
		this.bastionBlueprints = bastionBlueprints;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setBastionBlueprints(Collection<BastionBlueprint> bastionBlueprints) {
		this.bastionBlueprints = bastionBlueprints;
	}
	
	public void setReinforcementBlueprints(Collection<ReinforcementBlueprint> reinforcementBlueprints) {
		this.reinforcementBlueprints = reinforcementBlueprints;
	}
	
	public Collection<ReinforcementBlueprint> getReinforcementBlueprints() {
		return reinforcementBlueprints;
	}
	
	public Collection<BastionBlueprint> getBastionBlueprints() {
		return bastionBlueprints;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
