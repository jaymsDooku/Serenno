package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileState;
import io.jayms.serenno.util.ParticleEffect;

public class TrebuchetMissileProgressState implements ArtilleryMissileState<TrebuchetMissile> {

	@Override
	public ArtilleryMissileState<TrebuchetMissile> update(TrebuchetMissile missile) {
		FallingBlock fallBlock = missile.getMissileBlock();
		
		if (fallBlock == null) {
			return TrebuchetMissile.FINISH;
		}
		
		if (missile.isLaunching()) {
			Vector dir = missile.getLocation().getDirection();
			if (missile.getPrevLocation() != null) {
				dir = missile.getPrevLocation().clone().toVector().subtract(missile.getLocation().toVector());
			}
			fallBlock.setVelocity(dir.multiply(2));
			missile.setLaunching(false);
		}
		
		ParticleEffect.FLAME.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 15);
		ParticleEffect.SMOKE_NORMAL.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 7);
		ParticleEffect.SMOKE_LARGE.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 2);
		return this;
	}

}
