package io.jayms.serenno.event.snitch;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class SnitchPlacementEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Reinforcement reinforcement;
	private String name;
	private int radius;
	
	public SnitchPlacementEvent(Reinforcement reinforcement, String name, int radius) {
		this.reinforcement = reinforcement;
		this.name = name;
		this.radius = radius;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getRadius() {
		return radius;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
