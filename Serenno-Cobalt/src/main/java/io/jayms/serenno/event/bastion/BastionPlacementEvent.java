package io.jayms.serenno.event.bastion;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class BastionPlacementEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private CitadelPlayer placer;
	private Reinforcement reinforcement;
	private ItemStack itemPlaced;
	private BastionBlueprint blueprint;
	
	public BastionPlacementEvent(CitadelPlayer placer, Reinforcement reinforcement, ItemStack itemPlaced, BastionBlueprint blueprint) {
		this.placer = placer;
		this.reinforcement = reinforcement;
		this.itemPlaced = itemPlaced;
		this.blueprint = blueprint;
	}
	
	public CitadelPlayer getPlacer() {
		return placer;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public ItemStack getItemPlaced() {
		return itemPlaced;
	}
	
	public BastionBlueprint getBlueprint() {
		return blueprint;
	}
	
	public void setBlueprint(BastionBlueprint blueprint) {
		this.blueprint = blueprint;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
