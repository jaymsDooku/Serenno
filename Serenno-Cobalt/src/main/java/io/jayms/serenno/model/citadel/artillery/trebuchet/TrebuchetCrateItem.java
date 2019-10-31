package io.jayms.serenno.model.citadel.artillery.trebuchet;

import io.jayms.serenno.model.citadel.artillery.ArtilleryCrateItem;

public class TrebuchetCrateItem extends ArtilleryCrateItem {
	
	public static final int ID = 200;
	
	public TrebuchetCrateItem(int id) {
		super(id);
	}

	@Override
	public String getDisplayName() {
		return TrebuchetCrate.displayName();
	}
	
}
