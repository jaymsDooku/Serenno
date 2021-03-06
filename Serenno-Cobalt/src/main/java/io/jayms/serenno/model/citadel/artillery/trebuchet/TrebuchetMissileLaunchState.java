package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileState;
import io.jayms.serenno.util.MathTools;
import io.jayms.serenno.util.ParticleEffect;
import io.jayms.serenno.util.ParticleTools;

public class TrebuchetMissileLaunchState implements ArtilleryMissileState<TrebuchetMissile> {
	
	@Override
	public ArtilleryMissileState<TrebuchetMissile> update(TrebuchetMissile missile) {
		Trebuchet trebuchet = missile.getArtillery();
		BlockFace dir = trebuchet.getDirection();
		RotAxis axis = getAxis(dir);
		boolean negative = dir == BlockFace.NORTH || dir == BlockFace.EAST;
		
		ArtilleryMissileState<TrebuchetMissile> result = this;
		Location rotPoint = trebuchet.getRotationPoint();
		Location loc = missile.getLocation();
		double rotation = missile.getRotation();
		missile.setPrevLocation(loc);
		if (missile.isLaunching()) {
			Vector vDir = loc.clone().toVector().subtract(rotPoint.toVector());
			
			double dRotate = 6;
			double rotate = negative ? -dRotate : dRotate;
			//double dRotate = missile.getdRotation();
			if (axis == RotAxis.X) {
				vDir = MathTools.rotateAroundAxisX(vDir, Math.toRadians(rotate));
			} else if (axis == RotAxis.Z) {
				vDir = MathTools.rotateAroundAxisZ(vDir, Math.toRadians(rotate));
			}
			
			if (negative) {
				rotation -= dRotate;
			} else {
				rotation += dRotate;
			}
			missile.setRotation(rotation);
			missile.setdRotation(dRotate);
			
			loc = rotPoint.clone().add(vDir.normalize().multiply(10));
		} else {
			Location lowStart = missile.getLowStart();
			Vector vDir = loc.clone().toVector().subtract(lowStart.toVector());
			
			loc = loc.add(vDir);
			if (loc.distanceSquared(lowStart) <= 1*1) {
				missile.setLaunching(true);
			}
		}
		missile.setLocation(loc);
		
		FallingBlock fallBlock = missile.getMissileBlock();
		Vector velocity = loc.clone().toVector().subtract(fallBlock.getLocation().toVector());
		fallBlock.setVelocity(velocity);
		loc.getWorld().playSound(loc, Sound.ENTITY_ARROW_SHOOT, 0.75f, 0.2f);
		ParticleEffect.FLAME.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 15);
		ParticleEffect.SMOKE_NORMAL.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 7);
		ParticleEffect.SMOKE_LARGE.display(fallBlock.getLocation(), 0.5f, 0.5f, 0.5f, 0f, 2);
		
		ParticleTools.drawLine(rotPoint, fallBlock.getLocation(), 20, (l) -> {
			ParticleTools.displayColoredParticle(l, "#855F08");
		});
		
		if (negative) {
			if (rotation < -trebuchet.getFiringAngleThreshold()) {
				result = TrebuchetMissile.PROGRESS;
			}
		} else {
			if (rotation > trebuchet.getFiringAngleThreshold()) {
				result = TrebuchetMissile.PROGRESS;
			}
		}
		
		return result;
	}
	
	private RotAxis getAxis(BlockFace face) {
		switch (face) {
		case NORTH:
		case SOUTH:
			return RotAxis.X;
		case WEST:
		case EAST:
		default:
			return RotAxis.Z;
		}
	}
	
	private enum RotAxis {
		
		X, Z;
		
	}

}
