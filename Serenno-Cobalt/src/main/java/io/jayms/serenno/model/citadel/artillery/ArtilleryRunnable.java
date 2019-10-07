package io.jayms.serenno.model.citadel.artillery;

import java.util.Collection;

import io.jayms.serenno.SerennoCobalt;

public class ArtilleryRunnable implements Runnable {

	@Override
	public void run() {
		Collection<ArtilleryMissileRunner> missileRunners = SerennoCobalt.get().getCitadelManager().getArtilleryManager().getMissileRunners();
		if (missileRunners.isEmpty()) {
			return;
		}
		
		for (ArtilleryMissileRunner runner : missileRunners) {
			runner.update();
		}
	}

}
