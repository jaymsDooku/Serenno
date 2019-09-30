package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.artillery.AbstractArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryType;

public class TrebuchetCrate extends AbstractArtilleryCrate {
	
	public TrebuchetCrate() {
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

	private TrebuchetCrateItem crateItem;
	
	@Override
	public ItemStack getItemStack() {
		if (crateItem == null) {
			crateItem = (TrebuchetCrateItem) CustomItemManager.getCustomItemManager().createCustomItem(TrebuchetCrateItem.class);
			crateItem.setTrebuchetCrate(this);
		}
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
