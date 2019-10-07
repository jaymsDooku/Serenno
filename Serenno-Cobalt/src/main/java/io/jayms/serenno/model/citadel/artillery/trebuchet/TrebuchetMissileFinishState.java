package io.jayms.serenno.model.citadel.artillery.trebuchet;

import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileState;

public class TrebuchetMissileFinishState implements ArtilleryMissileState<TrebuchetMissile> {

	@Override
	public boolean isAcceptingState() {
		return true;
	}
	
	@Override
	public ArtilleryMissileState<TrebuchetMissile> update(TrebuchetMissile missile) {
		
		return this;
	}

}
