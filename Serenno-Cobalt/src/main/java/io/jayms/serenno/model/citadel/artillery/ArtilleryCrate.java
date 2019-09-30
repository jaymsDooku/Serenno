package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.menu.Menu;

public interface ArtilleryCrate {
	
	String getDisplayName();

	Artillery getArtillery();
	
	void setLocation(Location loc);
	
	Location getLocation();
	
	boolean hasBeenPlaced();
	
	ItemStack getItemStack();
	
	Menu getInterface();
	
	boolean isAssembled();
	
}
