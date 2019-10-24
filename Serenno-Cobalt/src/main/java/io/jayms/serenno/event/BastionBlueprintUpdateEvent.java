package io.jayms.serenno.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;

public class BastionBlueprintUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private String blueprintName;
	private BastionBlueprint bastionBlueprint;
	
	public BastionBlueprintUpdateEvent(Player player, String blueprintName, BastionBlueprint bastionBlueprint) {
		this.player = player;
		this.blueprintName = blueprintName;
		this.bastionBlueprint = bastionBlueprint;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getBlueprintName() {
		return blueprintName;
	}
	
	public void setBastionBlueprint(BastionBlueprint bastionBlueprint) {
		this.bastionBlueprint = bastionBlueprint;
	}
	
	public BastionBlueprint getBastionBlueprint() {
		return bastionBlueprint;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
