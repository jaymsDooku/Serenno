package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Location;
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
		
		Location loc = fallBlock.getLocation();
		Location prevLoc = missile.getPrevLocation();
		
		if (Math.abs(prevLoc.getBlockX() - loc.getBlockX()) < 0.00001
				&& Math.abs(prevLoc.getBlockZ() - loc.getBlockZ()) < 0.00001) {
			fallBlock.remove();
			missile.setMissileBlock(null);
			return TrebuchetMissile.FINISH;
		}
		
		if (missile.isLaunching()) {
			Vector dir = missile.getLocation().getDirection();
			if (prevLoc != null) {
				dir = missile.getLocation().clone().toVector().subtract(missile.getPrevLocation().toVector());
			}
			fallBlock.setVelocity(dir.multiply(missile.getArtillery().getFiringPower()));
			missile.getArtillery().setFiring(false);
			missile.setLaunching(false);
		}
		
		ParticleEffect.FLAME.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 15);
		ParticleEffect.SMOKE_NORMAL.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 7);
		ParticleEffect.SMOKE_LARGE.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 2);
		missile.setLocation(loc);
		missile.setPrevLocation(loc);
		return this;
	}

}
