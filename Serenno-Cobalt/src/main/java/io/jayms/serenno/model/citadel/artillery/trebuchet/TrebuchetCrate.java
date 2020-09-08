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
	
	private TrebuchetCrateItem crateItem;
	
	public TrebuchetCrate(TrebuchetCrateItem crateItem) {
		this.crateItem = crateItem;
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
		return SerennoCobalt.get().getCitadelManager().getArtilleryManager().getArtilleryCrateMenu();
	}

	@Override
	public boolean isAssembled() {
		if (artillery == null) {
			return false;
		}
		return artillery.isAssembled();
	}

	@Override
	public ItemStack getRequiredStarterItem() {
		return new ItemStack(Material.DIAMOND_BLOCK, 1);
	}
	
}
