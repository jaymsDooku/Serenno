package io.jayms.serenno.lobby;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.item.DuelMenuItem;
import io.jayms.serenno.game.item.KitEditorItem;
import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.lobby.item.SetRespawnItem;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.rank.Permissions;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.team.TeamManager;
import io.jayms.serenno.team.item.CreateTeamItem;
import io.jayms.serenno.team.item.LeaveTeamItem;
import io.jayms.serenno.team.item.ManageTeamItem;
import io.jayms.serenno.team.item.ViewTeamItem;
import io.jayms.serenno.util.PlayerTools;
import io.jayms.serenno.vault.item.ManageVaultsItem;
import net.md_5.bungee.api.ChatColor;

public class Lobby implements Listener {
	
	private Location lobbySpawn;
	private Set<SerennoPlayer> inLobby = Sets.newConcurrentHashSet();
	private Set<Damager> damagers = Sets.newHashSet();
	
	private Map<SerennoPlayer, Location> lobbyRespawns = Maps.newHashMap();
	
	private Kit normalItems;
	private Kit vaultManagementItems;
	private Kit teamLeaderItems;
	private Kit teamMemberItems;
	
	public Lobby() {
		lobbySpawn = SerennoCrimson.get().getConfigManager().getLobbySpawn();
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
		
		ConfigurationSection configSection = SerennoCrimson.get().getConfigManager().getDamagersConfigSection();
		Set<String> keys = configSection.getKeys(false);
		for (String key : keys) {
			Location p1 = Location.deserialize(configSection.getConfigurationSection(key + ".p1").getValues(false));
			Location p2 = Location.deserialize(configSection.getConfigurationSection(key + ".p2").getValues(false));
			double damage = configSection.getDouble(key + ".damage");
			Damager damager = new Damager(key, p1, p2, damage);
			damagers.add(damager);
		}
		
		normalItems = normalItems();
		vaultManagementItems = vaultManagementItems();
		teamMemberItems = teamMemberItems();
		teamLeaderItems = teamLeaderItems();
	}
	
	private Kit normalItems() {
		return new Kit()
				.set(0, CustomItemManager.getCustomItemManager().getCustomItem(DuelMenuItem.class).getItemStack())
				.set(4, CustomItemManager.getCustomItemManager().getCustomItem(CreateTeamItem.class).getItemStack())
				.set(6, CustomItemManager.getCustomItemManager().getCustomItem(KitEditorItem.class).getItemStack())
				.set(8, CustomItemManager.getCustomItemManager().getCustomItem(SetRespawnItem.class).getItemStack());
	}
	
	private Kit vaultManagementItems() {
		return new Kit()
				.set(0, CustomItemManager.getCustomItemManager().getCustomItem(DuelMenuItem.class).getItemStack())
				.set(4, CustomItemManager.getCustomItemManager().getCustomItem(CreateTeamItem.class).getItemStack())
				.set(5, CustomItemManager.getCustomItemManager().getCustomItem(ManageVaultsItem.class).getItemStack())
				.set(6, CustomItemManager.getCustomItemManager().getCustomItem(KitEditorItem.class).getItemStack())
				.set(8, CustomItemManager.getCustomItemManager().getCustomItem(SetRespawnItem.class).getItemStack());
	}
	
	private Kit teamMemberItems() {
		return normalItems()
				.set(0, CustomItemManager.getCustomItemManager().getCustomItem(ViewTeamItem.class).getItemStack())
				.set(8, CustomItemManager.getCustomItemManager().getCustomItem(LeaveTeamItem.class).getItemStack());
	}
	
	private Kit teamLeaderItems() {
		return teamMemberItems()
				.set(4, CustomItemManager.getCustomItemManager().getCustomItem(ManageTeamItem.class).getItemStack());
	}
	
	public void newDamager(String name, Location p1, Location p2, double damage) {
		if (getDamager(p1) != null) {
			SerennoCrimson.get().getLogger().warning("Damager already exists between " + p1 + " and " + p2);
			return;
		}
		
		if (getDamager(p2) != null) {
			SerennoCrimson.get().getLogger().warning("Damager already exists between " + p1 + " and " + p2);
			return;
		}
		
		Damager damager = new Damager(name, p1, p2, damage);
		damagers.add(damager);
	}
	
	public Damager getDamager(Location loc) {
		return damagers.stream()
				.filter(d -> d.in(loc))
				.findFirst()
				.orElse(null);
	}
	
	public Damager getDamager(Player p) {
		return damagers.stream()
				.filter(d -> d.isDamaging(p))
				.findFirst()
				.orElse(null);
	}
	
	public void setRespawnLocation(SerennoPlayer sp, Location respawn) {
		lobbyRespawns.put(sp, respawn);
		sp.sendMessage(ChatColor.GREEN + "You have set your respawn location.");
	}
	
	public Location getRespawnLocation(SerennoPlayer sp) {
		return lobbyRespawns.get(sp);
	}
	
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
	public void giveItems(SerennoPlayer player) {
		player.getBukkitPlayer().getInventory().clear();
		
		TeamManager teamManager = SerennoCrimson.get().getTeamManager();
		Team team = teamManager.getTeam(player);
		
		if (team != null) {
			if (team.isLeader(player)) {
				teamLeaderItems.load(player.getBukkitPlayer());
			} else {
				teamMemberItems.load(player.getBukkitPlayer());
			}
		} else {
			if (player.getBukkitPlayer().hasPermission(Permissions.VAULT_MANAGEMENT)) {
				vaultManagementItems.load(player.getBukkitPlayer());
				return;
			}
			normalItems.load(player.getBukkitPlayer());
		}
	}
	
	public void sendToLobby(SerennoPlayer player) {
		new BukkitRunnable() {
			
			public void run() {
				Player p = player.getBukkitPlayer();
				if (p == null) return;
				PlayerTools.clean(p);
				p.teleport(lobbySpawn);
				giveItems(player);
			};
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
		inLobby.add(player);
	}
	
	public boolean inLobby(SerennoPlayer player) {
		return inLobby.contains(player);
	}
	
	public void depart(SerennoPlayer player) {
		inLobby.remove(player);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().get(player);
		sendToLobby(sPlayer);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().get(player);
		if (inLobby(sPlayer)) {
			depart(sPlayer);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getEntity();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		if (!inLobby(sp)) {
			return;
		}
		
		Damager damager = getDamager(player);
		if (damager != null) {
			e.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(e.getPlayer());
		if (!inLobby(sp)) {
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		ThrownPotion potion = e.getEntity();
		if (!(potion.getShooter() instanceof Player)) {
			return;
		}
		
		Player shooter = (Player) potion.getShooter();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(shooter);
		if (!inLobby(sp)) {
			return;
		}
		
		Damager damager = getDamager(shooter);
		if (damager == null) {
			return;
		}
		
		Collection<LivingEntity> affected = e.getAffectedEntities();
		for (LivingEntity le : affected) {
			if (le.getUniqueId().equals(shooter.getUniqueId())) continue;
			e.setIntensity(le, 0);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(e.getEntity());
		if (!inLobby(sp)) {
			return;
		}
		
		e.getDrops().clear();
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(e.getPlayer());
		if (!inLobby(sp)) {
			return;
		}
		
		Damager damager = getDamager(e.getPlayer());
		if (damager == null) {
			return;
		}
		damager.stopDamaging(e.getPlayer());
		
		Location respawnLoc = getRespawnLocation(sp);
		if (respawnLoc == null) {
			respawnLoc = lobbySpawn;
		}
		e.setRespawnLocation(respawnLoc);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (!inLobby(SerennoCrimson.get().getPlayerManager().get(e.getPlayer()))) {
			return;
		}
		
		if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
				e.getFrom().getBlockY() == e.getTo().getBlockY() &&
				e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
			return;
		}
		
		Damager currentDamager = getDamager(e.getFrom());
		Damager damager = getDamager(e.getTo());
		
		if (damager != null && !damager.isDamaging(e.getPlayer())) {
			damager.startDamaging(e.getPlayer());
		}
		
		if (currentDamager != null && (damager == null || !currentDamager.getName().equals(damager.getName()))) {
			currentDamager.stopDamaging(e.getPlayer());
		}
	}
}
