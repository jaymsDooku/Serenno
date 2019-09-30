package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;

public abstract class AbstractArtilleryCrate implements ArtilleryCrate {

	protected Artillery artillery;
	
	private Location location;
	
	@Override
	public void setLocation(Location loc) {
		this.location = loc;
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	@Override
	public boolean hasBeenPlaced() {
		return location != null;
	}
	
}
