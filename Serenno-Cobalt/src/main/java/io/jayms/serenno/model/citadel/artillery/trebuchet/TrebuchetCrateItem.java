package io.jayms.serenno.model.citadel.artillery.trebuchet;

import io.jayms.serenno.model.citadel.artillery.ArtilleryCrateItem;

public class TrebuchetCrateItem extends ArtilleryCrateItem {
	
	public TrebuchetCrateItem() {
		super();
	}

	@Override
	public String getDisplayName() {
		return TrebuchetCrate.displayName();
	}
	
}
