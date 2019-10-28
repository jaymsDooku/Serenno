package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.artillery.AbstractArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryType;

public class TrebuchetCrate extends AbstractArtilleryCrate {
	
	public static String displayName() {
		return Trebuchet.DISPLAY_NAME + " Crate";
	}
	
	private Material starterItemType;
	private int starterItemAmount;
	private TrebuchetCrateItem crateItem;
	
	public TrebuchetCrate(TrebuchetCrateItem crateItem) {
		this.starterItemType = Material.DIAMOND_BLOCK;
		this.crateItem = crateItem;
	}
	
	public Material getStarterItemType() {
		return starterItemType;
	}
	
	public int getStarterItemAmount() {
		return starterItemAmount;
	}
	
	@Override
	public String getDisplayName() {
		return Trebuchet.DISPLAY_NAME + " Crate";
	}
	
	@Override
	public Artillery getArtillery() {
		if (artillery == null) {
			artillery = ArtilleryType.TREBUCHET.getArtillery(this);
		}
		return artillery; 
	}
	
	@Override
	public ItemStack getItemStack() {
		return crateItem.getItemStack();
	}

	@Override
	public Menu getInterface() {
		return SerennoCobalt.get().getCitadelManager().getArtilleryManager().getTrebuchetCrateMenu();
	}

	@Override
	public boolean isAssembled() {
		if (artillery == null) {
			return false;
		}
		return artillery.isAssembled();
	}
	
}
