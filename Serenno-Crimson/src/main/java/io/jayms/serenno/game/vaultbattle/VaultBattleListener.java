package io.jayms.serenno.game.vaultbattle;

import java.util.List;
import java.util.Set;

import com.github.maxopoly.finale.classes.archer.event.ArrowHitEvent;
import io.jayms.serenno.event.TeleporterKillEvent;
import io.jayms.serenno.game.DeathCause;
import io.jayms.serenno.game.GameManager;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.player.SerennoPlayer;
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

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.util.LocationTools;
import io.jayms.serenno.util.ParticleEffect;
import io.jayms.serenno.vault.Core;
import io.jayms.serenno.vault.event.CoreDamageEvent;
import io.jayms.serenno.vault.event.CoreDestroyEvent;
import net.md_5.bungee.api.ChatColor;

public class VaultBattleListener implements Listener {

	@EventHandler
	public void onTeleporterKillEvent(TeleporterKillEvent e) {
		Player killed = e.getKilled();
		VaultBattle battle = SerennoCrimson.get().getVaultMapManager().getVaultBattleFromWorld(killed.getWorld());
		if (battle == null) {
			return;
		}

		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(killed);
		battle.die(sp, DeathCause.ENVIRONMENT);
		battle.broadcast(sp.getName() + ChatColor.YELLOW + " tried to use a vehicle teleporter and died.");
	}

	@EventHandler
	public void onCoreDamage(CoreDamageEvent e) {
		Player damager = e.getDamager();
		Core core = e.getCore();
		VaultBattle battle = core.getBattle();
		if (battle == null) {
			return;
		}
		DuelTeam damagerTeam = battle.getTeam(SerennoCrimson.get().getPlayerManager().get(damager));
		DuelTeam coreTeam = battle.getTeam(core.getTeamColor());
		
		if (damagerTeam.getTeam().getID().equals(coreTeam.getTeam().getID())) {
			e.setCancelled(true);
			damager.sendMessage(ChatColor.RED + "You aren't allowed to break your own core.");
		}
	}
	
	@EventHandler
	public void onCoreDestroy(CoreDestroyEvent e) {
		Player destroyer = e.getDestroyer();
		Core core = e.getCore();
		VaultBattle battle = core.getBattle();
		if (battle == null) {
			return;
		}
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
	public void onSiegeArrowHit(ArrowHitEvent ev) {
		if (!(ev.getArrow() instanceof SiegeArrow)) {
			return;
		}

		SiegeArrow siegeArrow = (SiegeArrow) ev.getArrow();
		ArcherConfig config = Finale.getPlugin().getManager().getArcherConfig();
		ProjectileHitEvent e = ev.getHitEvent();
		ArcherPlayer archer = ev.getShooter();
		
		if (archer.getImpact() == ArrowImpactForm.CONCENTRATED) {
			if (e.getHitBlock() == null) {
				return;
			}
			
			Block hitBlock = e.getHitBlock();
			Artillery artillery = SerennoCobalt.get().getCitadelManager().getArtilleryManager().getArtillery(hitBlock.getLocation());
			if (artillery != null) {
				artillery.getReinforcement().damage();
			}
			Set<Bastion> bastions = SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(hitBlock.getLocation());
			Reinforcement rein = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcement(hitBlock);
			if (rein != null) {
				rein.damage(config.getSiegeDamageReinforcement());
			}
			if (!bastions.isEmpty()) {
				ReinforcementWorld reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(hitBlock.getWorld());
				for (Bastion bastion : bastions) {
					bastion.getReinforcement(reinforcementWorld).damage(config.getSiegeDamageBastion());
				}
			}
		} else if (archer.getImpact() == ArrowImpactForm.EXPLOSIVE) {
			Location explosiveLoc = siegeArrow.getArrow(archer).getLocation();
			Set<Bastion> bastions = SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(explosiveLoc);
			if (!bastions.isEmpty()) {
				ReinforcementWorld reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(explosiveLoc.getWorld());
				for (Bastion bastion : bastions) {
					bastion.getReinforcement(reinforcementWorld).damage(config.getSiegeDamageExplosiveBastion());
				}
			}
			
			explode(archer, explosiveLoc, config, siegeArrow);
		}
	}
	
	public void explode(ArcherPlayer shooter, Location loc, ArcherConfig config, SiegeArrow siegeArrow) {
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.5f);
		
		ParticleEffect.EXPLOSION_NORMAL.display(loc, 3, 3, 3, 0, 5);
		ParticleEffect.ENCHANTMENT_TABLE.display(loc, 3, 3, 3, 0, 15);
		
		int explosiveRadius = config.getSiegeExplosiveRadius();
		List<Location> explodeLocs = LocationTools.getCircle(loc, explosiveRadius, explosiveRadius, false, true, 0);
		for (Location explodeLoc : explodeLocs) {
			Artillery artillery = SerennoCobalt.get().getCitadelManager().getArtilleryManager().getArtillery(explodeLoc);
			if (artillery != null) {
				artillery.getReinforcement().damage();
			}
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
