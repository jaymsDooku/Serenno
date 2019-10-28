package io.jayms.serenno.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.github.maxopoly.finale.classes.archer.ArcherPlayer;
import com.github.maxopoly.finale.classes.archer.event.ArcherLinkPlayerEvent;
import com.github.maxopoly.finale.classes.archer.event.ArcherLinkPlayerEvent.LinkType;
import com.github.maxopoly.finale.combat.event.CritHitEvent;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.bot.Bot;
import io.jayms.serenno.bot.BotTrait;
import io.jayms.serenno.game.event.DuelPlayerDeathEvent;
import io.jayms.serenno.game.event.DuelPlayerStartEvent;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;

public class SimpleDuelListener implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		SerennoPlayer serennoPlayer = SerennoCrimson.get().getPlayerManager().get(player);
		Duel duel = serennoPlayer.getDuel();
		
		if (duel == null) {
			return;
		}
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			if (item == null) {
				return;
			}
			if (item.getType() != Material.BOOK) {
				return;
			}
			int kitIndex = new NBTItem(item).getInteger("index");
			Kit kit = serennoPlayer.getDuelingKit(duel.getDuelType(), kitIndex);
			kit.load(player);
			player.sendMessage(ChatColor.YELLOW + "You have loaded kit " + ChatColor.GOLD + "#" + (kitIndex+1));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		if (e instanceof EntityDamageByEntityEvent) {
			return;
		}
		
		Player victim = (Player) e.getEntity();
		SerennoPlayer serennoVictim = SerennoCrimson.get().getPlayerManager().get(victim);
		Duel victimDuel = serennoVictim.getDuel();
		
		if (victimDuel == null) {
			return;
		}
		
		if (!victimDuel.isRunning()) {
			e.setCancelled(true);
			return;
		}
		
		double finalDmg = e.getFinalDamage();
		double newHealth = (victim.getHealth() - finalDmg);
		
		if (newHealth <= 0) { // Dead
			e.setCancelled(true);
			DuelPlayerDeathEvent deathEvent = new DuelPlayerDeathEvent(victimDuel, serennoVictim, e);
			Bukkit.getPluginManager().callEvent(deathEvent);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player || (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) 
				|| !(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player damager;
		if (e.getDamager() instanceof Projectile) {
			damager = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			damager = (Player) e.getDamager();
		}
		Player victim = (Player) e.getEntity();
		
		SerennoPlayer damagerSP = SerennoCrimson.get().getPlayerManager().get(damager);
		SerennoPlayer victimSP = SerennoCrimson.get().getPlayerManager().get(victim);
		
		Duel damagerDuel = damagerSP.getDuel();
		Duel victimDuel = victimSP.getDuel();
		if (damagerDuel == null && victimDuel == null) {
			return;
		}
		
		if (damagerDuel == null || victimDuel == null || !damagerDuel.isRunning() || !victimDuel.isRunning() || damagerDuel.getID() != victimDuel.getID()) {
			e.setCancelled(true);
			return;
		}
		
		if (damagerDuel.isSpectating(damagerSP) || damagerDuel.isSpectating(victimSP)) {
			e.setCancelled(true);
			return;
		}
		
		double finalDmg = e.getFinalDamage();
		
		DuelStatistics duelStats = damagerDuel.getStatistics();
		duelStats.hit(damager, victim, finalDmg);
		double newHealth = (victim.getHealth() - finalDmg);
		
		if (newHealth <= 0) { // Dead
			e.setCancelled(true);
			DuelPlayerDeathEvent deathEvent = new DuelPlayerDeathEvent(victimDuel, victimSP, e);
			Bukkit.getPluginManager().callEvent(deathEvent);
		}
	}
	
	@EventHandler
	public void onCrit(CritHitEvent e) {
		Player attacker = e.getAttacker();
		LivingEntity victim = e.getVictim();
		if (!(victim instanceof Player)) {
			return;
		}
		
		Player playerVictim = (Player) victim;
		SerennoPlayer serennoAttacker = SerennoCrimson.get().getPlayerManager().get(attacker);
		SerennoPlayer serennoVictim = SerennoCrimson.get().getPlayerManager().get(playerVictim);
		
		Duel attackerDuel = serennoAttacker.getDuel();
		Duel victimDuel = serennoVictim.getDuel();
		
		if (attackerDuel == null || victimDuel == null || !attackerDuel.isRunning() || !victimDuel.isRunning() || attackerDuel.getID() != victimDuel.getID()) {
			return;
		}
		
		DuelStatistics duelStats = attackerDuel.getStatistics();
		duelStats.crit(attacker, playerVictim, e.getCritMultiplier());
	}
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		ThrownPotion pot = e.getEntity();
		ProjectileSource shooter = pot.getShooter();
		
		if (!(shooter instanceof Player)) {
			return;
		}
		
		Player playerShooter = (Player) shooter;
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(playerShooter);
		Duel duel = sp.getDuel();
		if (duel == null) {
			return;
		}
		
		Map<LivingEntity, Double> affectedEntities = new HashMap<>();
		for (LivingEntity le : e.getAffectedEntities()) {
			affectedEntities.put(le, e.getIntensity(le));
		}
		
		DuelStatistics duelStats = duel.getStatistics();
		duelStats.potionThrow(playerShooter, pot, affectedEntities);
	}
	
	@EventHandler
	public void onDeath(DuelPlayerDeathEvent e) {
		Duel duel = e.getDuel();
		SerennoPlayer deadPlayer = e.getDead();
		duel.die(deadPlayer);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		Duel duel = sp.getDuel();
		
		if (duel == null) {
			return;
		}
		
		duel.die(sp);
		duel.broadcast("%p% " + ChatColor.YELLOW + "disconnected.", sp);
	}
	
	@EventHandler
	public void onStart(DuelPlayerStartEvent e) {
		SerennoPlayer player = e.getPlayer();
		Duel duel = e.getDuel();
		
		if (player instanceof SerennoBot) {
			SerennoBot bot = (SerennoBot) player;
			Bot.loadKit(bot.getBot(), duel.getDuelType().getDefaultKitArray()[0]);
			
			DuelTeam allyTeam = duel.getTeam(player);
			DuelTeam enemyTeam = duel.getOtherTeam(allyTeam);
			
			SerennoPlayer leader = enemyTeam.getTeam().getLeader();
			BotTrait botTrait = bot.getBot().getBotTrait();
			botTrait.setBot(bot.getBot());
			botTrait.setTarget(leader.getBukkitPlayer());
		}
	}
	
	@EventHandler
	public void onArcherLink(ArcherLinkPlayerEvent e) {
		if (e.getLinkType()  != LinkType.LINK) {
			return;
		}
		
		ArcherPlayer archer = e.getArcher();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(archer.getBukkitPlayer());
		Duel duel = sp.getDuel();
		
		if (duel == null) {
			return;
		}
		
		DuelTeam team = duel.getTeam(sp);
		Player linked = e.getLinked();
		SerennoPlayer linkedSP = SerennoCrimson.get().getPlayerManager().get(linked);
		
		if (team.getTeam().inTeam(linkedSP)) {
			return;
		}
		
		archer.getBukkitPlayer().sendMessage(ChatColor.RED + "Are you really sure you want to link with the enemy?");
	}
	
	@EventHandler
	public void onPickUpItem(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		Duel duel = sp.getDuel();
		if (duel == null) {
			return;
		}
		
		if (!duel.isRunning() || duel.isSpectating(sp)) {
			e.setCancelled(true);
		}
	}
}
