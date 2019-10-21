package io.jayms.serenno.game.vaultbattle;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.github.maxopoly.finale.Finale;
import com.github.maxopoly.finale.classes.archer.ArcherConfig;
import com.github.maxopoly.finale.classes.archer.ArcherPlayer;
import com.github.maxopoly.finale.classes.archer.ArrowImpactForm;
import com.github.maxopoly.finale.classes.archer.arrows.SiegeArrow;
import com.github.maxopoly.finale.classes.archer.event.SiegeArrowHitEvent;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.util.LocationTools;
import io.jayms.serenno.util.ParticleEffect;
import io.jayms.serenno.vault.Core;
import io.jayms.serenno.vault.event.CoreDestroyEvent;
import net.md_5.bungee.api.ChatColor;

public class VaultBattleListener implements Listener {

	@EventHandler
	public void onCoreDestroy(CoreDestroyEvent e) {
		Player destroyer = e.getDestroyer();
		Core core = e.getCore();
		VaultBattle battle = core.getVaultMapDatabase().getBattle();
		DuelTeam losers = battle.getTeam(core.getTeamColor());
		DuelTeam winners = battle.getOtherTeam(losers);
		
		Team losersTeam = losers.getTeam();
		
		String message = destroyer == null ? 
				losers.getTeamColor() + losersTeam.getName() + "'s " + ChatColor.YELLOW + " core has been destroyed!" :
				ChatColor.GOLD + destroyer.getName() + ChatColor.YELLOW + " has destroyed " + losers.getTeamColor() + losersTeam.getName() + "'s " + ChatColor.YELLOW + " core!";
		
		battle.broadcast(message);
		battle.finish(winners, losers);
	}
	
	@EventHandler
	public void onSiegeArrowHit(SiegeArrowHitEvent ev) {
		ArcherConfig config = Finale.getPlugin().getManager().getArcherConfig();
		ProjectileHitEvent e = ev.getHitEvent();
		ArcherPlayer archer = ev.getShooter();
		
		if (archer.getImpact() == ArrowImpactForm.CONCENTRATED) {
			if (e.getHitBlock() == null) {
				return;
			}
			
			Block hitBlock = e.getHitBlock();
			Set<Bastion> bastions = SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(hitBlock.getLocation());
			Reinforcement rein = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcement(hitBlock);
			if (rein != null) {
				rein.damage(config.getSiegeDamageReinforcement());
			}
			if (!bastions.isEmpty()) {
				for (Bastion bastion : bastions) {
					bastion.damage(config.getSiegeDamageBastion());
				}
			}
		} else if (archer.getImpact() == ArrowImpactForm.EXPLOSIVE) {
			Location explosiveLoc = ev.getSiegeArrow().getArrow(archer).getLocation();
			Set<Bastion> bastions = SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(explosiveLoc);
			if (!bastions.isEmpty()) {
				for (Bastion bastion : bastions) {
					bastion.damage(config.getSiegeDamageExplosiveBastion());
				}
			}
			
			explode(archer, explosiveLoc, config, ev.getSiegeArrow());
		}
	}
	
	public void explode(ArcherPlayer shooter, Location loc, ArcherConfig config, SiegeArrow siegeArrow) {
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.5f);
		
		ParticleEffect.EXPLOSION_NORMAL.display(loc, 3, 3, 3, 0, 5);
		ParticleEffect.ENCHANTMENT_TABLE.display(loc, 3, 3, 3, 0, 15);
		
		int explosiveRadius = config.getSiegeExplosiveRadius();
		List<Location> explodeLocs = LocationTools.getCircle(loc, explosiveRadius, explosiveRadius, false, true, 0);
		for (Location explodeLoc : explodeLocs) {
			Block explodeBlock = explodeLoc.getBlock();
			if (explodeBlock.getType() != Material.AIR) {
				Reinforcement rein = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcement(explodeBlock);
				if (rein != null) {
					shooter.consumeEnergy(siegeArrow.getAdditionalEnergyConsumption());
					siegeArrow.addCooldown(shooter, siegeArrow.getAdditionalCooldown());
					rein.damage(config.getSiegeDamageExplosiveReinforcement());
				}
			}
		}
		
		siegeArrow.disable(shooter);
	}
	
}
