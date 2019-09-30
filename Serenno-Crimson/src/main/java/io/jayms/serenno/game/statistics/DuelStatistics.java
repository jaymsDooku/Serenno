package io.jayms.serenno.game.statistics;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.PlayerTools;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;

public class DuelStatistics {

	private Set<Hit> hits = Sets.newConcurrentHashSet();
	private Set<Crit> crits = Sets.newConcurrentHashSet();
	private Set<PotionThrow> potionThrows = Sets.newConcurrentHashSet();
	private Set<ArrowShot> arrowShots = Sets.newConcurrentHashSet();
	private Set<Death> deaths = Sets.newConcurrentHashSet();
	
	public DuelStatistics() {
	}
	
	public void hit(Player damager, Player victim, double damage) {
		double distance = damager.getEyeLocation().distance(victim.getEyeLocation());
		long time = System.currentTimeMillis();
		hits.add(new Hit(damager.getUniqueId(), victim.getUniqueId(), distance, damage, time));
	}
	
	public void crit(Player damager, Player victim, double critMult) {
		double distance = damager.getEyeLocation().distance(victim.getEyeLocation());
		long time = System.currentTimeMillis();
		crits.add(new Crit(damager.getUniqueId(), victim.getUniqueId(), distance, critMult, time));
	}
	
	public void potionThrow(Player thrower, ThrownPotion pot, Map<LivingEntity, Double> affectedEntities) {
		long time = System.currentTimeMillis();
		potionThrows.add(new PotionThrow(thrower.getUniqueId(), Lists.newArrayList(pot.getEffects()), time, affectedEntities));
	}
	
	public void arrowShot(Player shooter, Entity hitEntity, double damage) {
		double distance = shooter.getLocation().distance(hitEntity.getLocation());
		long time = System.currentTimeMillis();
		arrowShots.add(new ArrowShot(shooter.getUniqueId(), hitEntity.getUniqueId(), distance, damage, time));
	}
	
	public void death(Player dead) {
		Map<PotionEffectType, Long> durations = PlayerTools.getPotionDurations(dead);
		Kit finalInv = new Kit(dead);
		deaths.add(new Death(dead.getUniqueId(), durations, finalInv, dead.getHealth(), dead.getFoodLevel()));
	}
	
	public Death getDeath(SerennoPlayer dead) {
		return getDeath(dead.getID());
	}
	
	public Death getDeath(UUID id) {
		return deaths.stream()
				.filter(d -> d.getDeadID().equals(id))
				.findFirst().orElse(null);
	}
	
	public double getDamage(SerennoPlayer player, PlayerType type) {
		return getDamage(player.getID(), type);
	}
	
	public double getDamage(UUID uid, PlayerType type) {
		return hits.stream().filter(h -> {
			if (type == PlayerType.DAMAGER) {
				return uid.equals(h.getDamagerID());
			} else {
				return uid.equals(h.getVictimID());
			}
		}).mapToDouble(h -> h.getDamage()).sum();
	}
	
	public double getDamage(DuelTeam team, PlayerType type) {
		return team.getTeam().getAll().stream()
			.mapToDouble(p -> getDamage(p, type))
			.sum();
	}
	
	public double getDamagePercent(DuelTeam team, PlayerType type) {
		double dmg = getDamage(team, type);
		double totalDmg = getTotalDamage();
		if (totalDmg == 0) {
			return Double.NaN;
		}
		return dmg / totalDmg;
	}
	
	public double getHitsPercent(DuelTeam team, PlayerType type) {
		double hits = getHits(team, type);
		double totalHits = getTotalHits();
		if (totalHits == 0) {
			return Double.NaN;
		}
		return hits / totalHits;
	}
	
	public double getCritsPercent(DuelTeam team, PlayerType type) {
		double crits = getCrits(team, type);
		double totalCrits = getTotalCrits();
		if (totalCrits == 0) {
			return Double.NaN;
		}
		return crits / totalCrits;
	}
	
	public double getPotionsThrownPercent(DuelTeam team) {
		double potionsThrown = getPotionsThrown(team);
		double totalPotionsThrown = getPotionsThrown();
		if (totalPotionsThrown == 0) {
			return Double.NaN;
		}
		return potionsThrown / totalPotionsThrown;
	}
	
	public int getHits(SerennoPlayer player, PlayerType type) {
		return getHits(player.getID(), type);
	}
	
	public int getHits(UUID uid, PlayerType type) {
		return (int) hits.stream().filter(h -> {
			if (type == PlayerType.DAMAGER) {
				return uid.equals(h.getDamagerID());
			} else {
				return uid.equals(h.getVictimID());
			}
		}).count();
	}
	
	public int getHits(DuelTeam team, PlayerType type) {
		return team.getTeam().getAll().stream()
			.mapToInt(p -> getHits(p, type))
			.sum();
	}
	
	public int getCrits(SerennoPlayer player, PlayerType type) {
		return getCrits(player.getID(), type);
	}
	
	public int getCrits(UUID uid, PlayerType type) {
		return (int) crits.stream().filter(h -> {
			if (type == PlayerType.DAMAGER) {
				return uid.equals(h.getDamagerID());
			} else {
				return uid.equals(h.getVictimID());
			}
		}).count();
	}
	
	public int getCrits(DuelTeam team, PlayerType type) {
		return team.getTeam().getAll().stream()
			.mapToInt(p -> getCrits(p, type))
			.sum();
	}
	
	public int getPotionsThrown(SerennoPlayer thrower) {
		return (int) potionThrows.stream().filter(p -> {
			return thrower.getID().equals(p.getThrowerID());
		}).count();
	}
	
	public int getPotionsThrown(DuelTeam team) {
		return team.getTeam().getAll().stream()
			.mapToInt(p -> getPotionsThrown(p))
			.sum();
	}
	
	public int getPotionsMissed(SerennoPlayer thrower) {
		long missed = potionThrows.stream()
				.filter(pt -> pt.getThrowerID().equals(thrower.getID()))
				.filter(pt -> {
					Map<LivingEntity, Double> affected = pt.getAffectedEntities();
					return !affected.keySet().stream().filter(le -> le.getUniqueId().equals(thrower.getID())).findFirst().isPresent();
				})
				.count();
		return (int) missed;
	}
	
	public double getTotalPotionIntensity(SerennoPlayer thrower) {
		return potionThrows.stream()
				.filter(pt -> pt.getThrowerID().equals(thrower.getID()))
				.mapToDouble(pt -> {
					Map<LivingEntity, Double> affected = pt.getAffectedEntities();
					for (Entry<LivingEntity, Double> affectedEn : affected.entrySet()) {
						if (affectedEn.getKey().getUniqueId().equals(thrower.getID())) {
							return affectedEn.getValue();
						}
					}
					return 0;
				}).sum();
	}
	
	public double getPotionAccuracy(SerennoPlayer thrower) {
		return getTotalPotionIntensity(thrower) / (double) getPotionsThrown(thrower);
	}
	
	public double getArrowDamageDealt(SerennoPlayer shooter) {
		return arrowShots.stream()
				.filter(s -> s.getShooterID().equals(shooter.getID()))
				.mapToDouble(s -> s.getDamage())
				.sum();
	}
	
	public double getArrowDamageDealt(DuelTeam team) {
		return team.getTeam().getAll().stream()
			.mapToDouble(p -> getArrowDamageDealt(p))
			.sum();
	}
	
	public double getTotalDamage() {
		return hits.stream()
				.mapToDouble(h -> h.getDamage())
				.sum();
	}
	
	public int getTotalHits() {
		return hits.size();
	}
	
	public int getTotalCrits() {
		return crits.size();
	}
	
	public int getPotionsThrown() {
		return potionThrows.size();
	}
	
	private DecimalFormat dp1 = new DecimalFormat("#.#");
	
	public void loreStatsTeamTotal(ItemStack is, DuelTeam own, DuelTeam team) {
		ChatColor color1 = own == null ? ChatColor.YELLOW : (own.equals(team) ? ChatColor.GREEN : ChatColor.RED);
		ChatColor color2 = own == null ? ChatColor.GOLD : own.equals(team) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED;
		
		double damagePercent = getDamagePercent(team, PlayerType.DAMAGER) * 100;
		double hitsPercent = getHitsPercent(team, PlayerType.DAMAGER) * 100;
		double critsPercent = getCritsPercent(team, PlayerType.DAMAGER) * 100;
		double thrownPercent = getPotionsThrownPercent(team) * 100;
		
		String[] lore = {ChatColor.RED 
				+ "Damage: " 
				+ ChatColor.WHITE + dp1.format(getDamage(team, PlayerType.DAMAGER))
				+ color2 
				+ (!Double.isNaN(damagePercent) ? " (" + color1 + dp1.format(damagePercent) + color2 + "%)" : ""),
				ChatColor.RED 
				+ "Hits: " 
				+ ChatColor.WHITE + getHits(team, PlayerType.DAMAGER)
				+ color2 
				+ (!Double.isNaN(hitsPercent) ? " (" + color1 + dp1.format(hitsPercent) + color2 + "%)" : ""),
				ChatColor.RED 
				+ "Crits: " 
				+ ChatColor.WHITE + getCrits(team, PlayerType.DAMAGER)
				+ color2 
				+ (!Double.isNaN(critsPercent) ? " (" + color1 + dp1.format(critsPercent) + color2 + "%)" : ""),
				ChatColor.RED 
				+ "Potions Thrown: " 
				+ ChatColor.WHITE + getPotionsThrown(team)
				+ color2
				+ (!Double.isNaN(thrownPercent) ? " (" + color1 + dp1.format(thrownPercent) + color2 + "%)" : "")};
		
		ISUtils.setLore(is, lore);
	}
	
	public void loreStatsTotal(ItemStack is, DuelTeam team) {
		double damagePercent = getDamagePercent(team, PlayerType.DAMAGER) * 100;
		double hitsPercent = getHitsPercent(team, PlayerType.DAMAGER) * 100;
		double critsPercent = getCritsPercent(team, PlayerType.DAMAGER) * 100;
		double thrownPercent = getPotionsThrownPercent(team) * 100;
		
		String[] lore = {ChatColor.RED 
				+ "Total Damage: " 
				+ ChatColor.WHITE 
				+ (Double.isNaN(damagePercent) ? "N/A" :
					dp1.format(getTotalDamage()))
				+ (team != null && !Double.isNaN(critsPercent) ? ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + dp1.format(damagePercent) + ChatColor.DARK_GREEN + "%)" : ""),
				ChatColor.RED 
				+ "Total Hits: " 
				+ ChatColor.WHITE 
				+ (Double.isNaN(hitsPercent) ? "N/A" :
					dp1.format(getTotalHits()))
				+ (team != null && !Double.isNaN(critsPercent) ? ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + dp1.format(hitsPercent) + ChatColor.DARK_GREEN + "%)" : ""),
				ChatColor.RED 
				+ "Total Crits: " 
				+ ChatColor.WHITE 
				+ (Double.isNaN(critsPercent) ? "N/A" :
					dp1.format(getTotalCrits()))
				+ (team != null && !Double.isNaN(critsPercent) ? ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + dp1.format(critsPercent) + ChatColor.DARK_GREEN + "%)" : ""),
				ChatColor.RED 
				+ "Potions Thrown: " 
				+ ChatColor.WHITE 
				+ (Double.isNaN(thrownPercent) ? "N/A" :
					dp1.format(getPotionsThrown()))
				+ (team != null && !Double.isNaN(critsPercent) ? ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + dp1.format(thrownPercent) + ChatColor.DARK_GREEN + "%)" : "")};
		
		ISUtils.setLore(is, lore);
	} 
	
	public enum PlayerType {
		DAMAGER, VICTIM;
	}
}
