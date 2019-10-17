package io.jayms.serenno.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.Duelable;
import io.jayms.serenno.game.Game;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.team.Team;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public class SerennoPlayer implements Duelable {

	private final Player bukkitPlayer;
	
	private Map<DuelType, Kit[]> kits = new ConcurrentHashMap<>();
	
	private Map<String, DuelRequest> requests = new HashMap<>();
	private Map<String, BukkitTask> requestExpireTasks = new HashMap<>();
	
	private Game currentGame;
	
	private boolean dirty = true;
	
	public SerennoPlayer(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
	}
	
	@Override
	public UUID getID() {
		return bukkitPlayer.getUniqueId();
	}
	
	public String getName() {
		return bukkitPlayer.getName();
	}
	
	public Player getBukkitPlayer() {
		return bukkitPlayer;
	}
	
	public CommonPlayer getCommonPlayer() {
		return SerennoCommon.get().getCommonPlayerManager().get(bukkitPlayer);
	}
	
	public boolean hasPermission(String permission) {
		return bukkitPlayer.hasPermission(permission);
	}
	
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
	
	public Game getCurrentGame() {
		return currentGame;
	}
	
	public Duel getDuel() {
		if (!(currentGame instanceof Duel)) {
			return null;
		}
		return (Duel) currentGame;
	}
	
	public Location getLocation() {
		return bukkitPlayer.getLocation();
	}
	
	@Override
	public void teleport(Location loc) {
		bukkitPlayer.teleport(loc);
	}
	
	@Override
	public void sendMessage(String message) {
		bukkitPlayer.sendMessage(message);
	}
	
	@Override
	public void sendMessage(FancyMessage message) {
		message.send(bukkitPlayer);
	}
	
	public Kit[] getDuelingKits(DuelType duelType) {
		Kit[] kits = this.kits.get(duelType);
		
		if (kits == null) {
			kits = duelType.getDefaultKitArray();
			this.kits.put(duelType, kits);
		}
		
		return kits;
	}
	
	public Kit getDuelingKit(DuelType duelType, int slot) {
		Kit kit = getDuelingKits(duelType)[slot];
		
		if (kit == null) {
			kit = duelType.getDefaultKitArray()[0];
			setDuelingKit(duelType, slot, kit);
		}
		
		return kit;
	}
	
	public void setDuelingKit(DuelType duelType, int slot, Kit kit) {
		Kit[] kits = this.kits.get(duelType);
		
		if (kits == null) {
			kits = duelType.getDefaultKitArray();
		}
		
		kits[slot] = kit;
	}
	
	public void setKits(Map<DuelType, Kit[]> set) {
		kits = set;
	}
	
	public Map<DuelType, Kit[]> getKits() {
		return kits;
	}
	
	public void sendRequest(DuelRequest request) {
		if (hasRequest(request.getSender().getName().toLowerCase())) {
			request.getSender().sendMessage(ChatColor.RED + "You have already sent a duel to this player.");
			return;
		}
		
		requests.put(request.getSender().getName().toLowerCase(), request);
		FancyMessage sentDuel = new FancyMessage(ChatColor.GOLD + request.getSender().getName() 
				+ ChatColor.YELLOW + " has sent you a " + request.getDuelType().getDisplayName() + ChatColor.YELLOW + " duel on " + ChatColor.GOLD + request.getMap().getName()
				+ ChatColor.YELLOW + "." 
				+ ChatColor.RED + " Click to accept.")
				.command("/accept " + request.getSender().getName());
		sentDuel.send(bukkitPlayer);
		request.getSender().sendMessage(ChatColor.YELLOW + "You have sent a " + request.getDuelType().getDisplayName() + ChatColor.YELLOW + " duel to " + ChatColor.GOLD + bukkitPlayer.getName() + ChatColor.YELLOW + " on " 
				+ ChatColor.GOLD + request.getMap().getName()
				+ ChatColor.YELLOW + ".");
		
		BukkitTask expireTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!requests.containsKey(request.getSender().getName().toLowerCase())) {
					cancel();
					return;
				}
				requests.remove(request.getSender().getName().toLowerCase());
				bukkitPlayer.sendMessage(ChatColor.YELLOW + "Your duel request from " + ChatColor.GOLD + request.getSender().getName() + ChatColor.YELLOW + " has " + ChatColor.RED + "expired" + ChatColor.YELLOW + ".");
				request.getSender().sendMessage(ChatColor.YELLOW + "Your duel request to " + ChatColor.GOLD + request.getSender().getName() + ChatColor.YELLOW + " has " + ChatColor.RED + "expired" + ChatColor.YELLOW + ".");
			}
		}.runTaskLater(SerennoCrimson.get(), 60 * 20);
		requestExpireTasks.put(request.getSender().getName().toLowerCase(), expireTask);
	}
	
	public boolean hasRequest(String from) {
		return requests.containsKey(from.toLowerCase());
	}
	
	public void acceptRequest(String from) {
		if (!hasRequest(from)) {
			return;
		}
		
		DuelRequest request = requests.remove(from.toLowerCase());
		BukkitTask expireTask = requestExpireTasks.remove(from.toLowerCase());
		expireTask.cancel();
		
		bukkitPlayer.sendMessage(ChatColor.YELLOW + "Duel accepted. Starting...");
		request.getSender().sendMessage(ChatColor.YELLOW + "Duel accepted. Starting...");
		
		SerennoCrimson.get().getGameManager().duel(this, request);
	}
	
	public void setDirty(boolean set) {
		dirty = set;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SerennoPlayer)) {
			return false;
		}
		
		SerennoPlayer sp = (SerennoPlayer) obj;
		return sp.bukkitPlayer.getUniqueId().equals(bukkitPlayer.getUniqueId());
	}
	
	@Override
	public int hashCode() {
		return bukkitPlayer.getUniqueId().hashCode();
	}

	@Override
	public boolean inDuel() {
		return currentGame != null;
	}

	@Override
	public boolean isSpectating() {
		return inDuel() && currentGame.isSpectating(this);
	}

	@Override
	public void showPlayer(Duelable duelable) {
		if (duelable instanceof SerennoPlayer) {
			showPlayer((SerennoPlayer) duelable);
		} else if (duelable instanceof Team) {
			Team team = (Team) duelable;
			List<SerennoPlayer> members = team.getAvailableMembers();
			members.forEach(m -> {
				showPlayer(m);
			});
		}
	}
	
	@Override
	public void hidePlayer(Duelable duelable) {
		if (duelable instanceof SerennoPlayer) {
			hidePlayer((SerennoPlayer) duelable);
		} else if (duelable instanceof Team) {
			Team team = (Team) duelable;
			List<SerennoPlayer> members = team.getAvailableMembers();
			members.forEach(m -> {
				hidePlayer(m);
			});
		}
	}
	
	private void showPlayer(SerennoPlayer player) {
		bukkitPlayer.showPlayer(SerennoCrimson.get(), player.getBukkitPlayer());
	}
	
	private void hidePlayer(SerennoPlayer player) {
		bukkitPlayer.hidePlayer(SerennoCrimson.get(), player.getBukkitPlayer());
	}
}
