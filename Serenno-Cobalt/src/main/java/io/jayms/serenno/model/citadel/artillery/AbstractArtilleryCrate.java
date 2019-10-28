package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public abstract class AbstractArtilleryCrate implements ArtilleryCrate {

	protected Artillery artillery;
	
	private Reinforcement reinforcement;
	
	@Override
	public Location getLocation() {
		return reinforcement.getLocation();
	}
	
	@Override
	public void setReinforcement(Reinforcement reinforcement) {
		this.reinforcement = reinforcement;
	}
	
	@Override
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	@Override
	public boolean hasBeenPlaced() {
		return reinforcement != null;
	}
	
}
