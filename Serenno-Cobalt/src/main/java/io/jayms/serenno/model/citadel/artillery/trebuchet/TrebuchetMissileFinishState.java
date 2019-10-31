package io.jayms.serenno.model.citadel.artillery.trebuchet;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCobaltConfigManager;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileState;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.util.LocationTools;
import io.jayms.serenno.util.ParticleEffect;

public class TrebuchetMissileFinishState implements ArtilleryMissileState<TrebuchetMissile> {

	
	@Override
	public boolean isAcceptingState() {
		return true;
	}
	
	@Override
	public ArtilleryMissileState<TrebuchetMissile> update(TrebuchetMissile missile) {
		Random random = new Random();
		Location loc = missile.getLocation();
		SerennoCobaltConfigManager config = SerennoCobalt.get().getConfigManager();
		
		Set<Bastion> bastions = SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(loc);
		if (!bastions.isEmpty()) {
			for (Bastion bastion : bastions) {
				bastion.damage(config.getTrebuchetBastionDamage());
			}
		}
		
		int impactRadius = config.getTrebuchetImpactRadius();
		List<Location> explodeLocs = LocationTools.getCircle(loc, impactRadius, impactRadius, false, true, 0);
		for (Location explodeLoc : explodeLocs) {
			Block explodeBlock = explodeLoc.getBlock();
			if (explodeBlock.getType() != Material.AIR && explodeBlock.getType() != Material.BEDROCK && explodeBlock.getType() != Material.BARRIER) {
				Artillery artillery = SerennoCobalt.get().getCitadelManager().getArtilleryManager().getArtillery(explodeLoc);
				if (artillery != null) {
					artillery.getReinforcement().damage();
				}
				Reinforcement rein = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcement(explodeBlock);
				boolean notDead = rein != null;
				if (notDead) {
					notDead = rein.damage(missile.getReinforcementDamage());
				}
				
				if (!notDead) {
					Material type = Material.AIR;
					
					if (explodeBlock.getRelative(BlockFace.DOWN, 1).getType().isSolid() && random.nextInt(3) == 0) {
						type = Material.FIRE;
					}
					
					explodeBlock.setType(type);
				}
			}
		}
		
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.5f);
		ParticleEffect.EXPLOSION_HUGE.display(loc, 0f, 0f, 0f, 0, 5);
		ParticleEffect.FLAME.display(loc, 0.2f, 0.2f, 0.2f, 0, 10);
		ParticleEffect.LARGE_SMOKE.display(loc, 0.3f, 0.3f, 0.3f, 0, 5);
		
		Collection<LivingEntity> livingEntities = LocationTools.getNearbyLivingEntities(loc, impactRadius);
		for (LivingEntity le : livingEntities) {
			Vector dir = loc.clone().subtract(le.getEyeLocation()).toVector().normalize().multiply(-1);
			dir = dir.add(new Vector(0, config.getTrebuchetImpactVertical(), 0));
			dir = dir.multiply(new Vector(config.getTrebuchetImpactHorizontal(), 0, config.getTrebuchetImpactHorizontal()));
			le.setVelocity(dir);
			le.damage(config.getTrebuchetPlayerDamage());
		}
		
		return this;
	}

}
