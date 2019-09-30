package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.player.SerennoPlayer;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractGame implements Game {

	private int id;
	private boolean running = false;
	private List<SerennoPlayer> spectators = new ArrayList<>();
	private Arena map;
	private int duration;
	
	private BukkitTask counterTask;
	private BukkitTask pauseTask;
	
	protected AbstractGame(int id, Arena map) {
		this.id = id;
		this.map = map;
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public int getDuration() {
		return duration;
	}
	
	@Override
	public Arena getMap() {
		return map;
	}
	
	protected abstract void initGame();
	
	@Override
	public void start() {
		initGame();
		
		broadcast(ChatColor.GOLD + "You are now playing on " + map.getRegion().getDisplayName() + ChatColor.GOLD + " by " + ChatColor.BLACK + "[" + ChatColor.RESET + map.getCreators() + ChatColor.BLACK + "]");
		
		counterTask = new BukkitRunnable() {
			
			int c = getCountdown();
			
			@Override
			public void run() {
				if (!running) {
					if (c <= 0) {
						running = true;
						broadcast(ChatColor.YELLOW + "Match started.");
						this.cancel();
					} else {
						broadcast(ChatColor.YELLOW + Integer.toString(c));
						c--;
					}
				} else {
					duration++;
				}
			}
			
		}.runTaskTimer(SerennoCrimson.get(), 0L, 20L);
	}

	@Override
	public void resume() {
		running = true;
		broadcast(ChatColor.YELLOW + "Match" + ChatColor.GREEN + " resumed.");
		
		unpause();
	}

	@Override
	public void pause(String reason) {
		running = false;
		pauseTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				broadcast(reason);
			}
			
		}.runTaskTimer(SerennoCrimson.get(), 0L, 30 * 20L);
	}
	
	protected abstract void disposeGame();

	@Override
	public void stop(String reason) {
		running = false;
		if (reason != null && !reason.isEmpty()) {
			broadcast(reason);
		}
		
		unpause();
		
		Iterator<SerennoPlayer> specsIt = spectators.iterator();
		while (specsIt.hasNext()) {
			SerennoPlayer spectator = specsIt.next();
			getPlaying().forEach(p -> {
				p.showPlayer(spectator);
			});
			SerennoCrimson.get().getLobby().sendToLobby(spectator);
			specsIt.remove();
		}
		
		disposeGame();
		//SerennoCrimson.get().getGameManager().finishGame(this);
	}
	
	private void unpause() {
		if (pauseTask != null) {
			pauseTask.cancel();
			pauseTask = null;
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void broadcast(String message) {
		getPlaying().forEach(p -> {
			p.getBukkitPlayer().sendMessage(message);
		});
		getSpectators().forEach(p -> {
			p.getBukkitPlayer().sendMessage(message);
		});
	}
	
	@Override
	public void broadcast(FancyMessage message) {
		getPlaying().forEach(p -> {
			message.send(p.getBukkitPlayer());
		});
		getSpectators().forEach(p -> {
			message.send(p.getBukkitPlayer());
		});
	}
	
	@Override
	public List<SerennoPlayer> getSpectators() {
		return spectators;
	}
	
	@Override
	public void startSpectating(SerennoPlayer spectator, SerennoPlayer toSpectate) {
		startSpectating(spectator, toSpectate.getBukkitPlayer().getLocation());
	}
	
	private static final Kit spectatorKit = new Kit();

	@Override
	public void startSpectating(SerennoPlayer spectator, Location loc) {
		if (isSpectating(spectator)) {
			return;
		}
		
		getPlaying().forEach(p -> {
			p.hidePlayer(spectator);
		});
		new BukkitRunnable() {
			
			public void run() {
				spectator.teleport(loc);
				spectatorKit.load(spectator.getBukkitPlayer());
			};
			
		}.runTask(SerennoCrimson.get());
		broadcast(ChatColor.GOLD + spectator.getName() + ChatColor.YELLOW + " is spectating.");
		spectators.add(spectator);
	}

	@Override
	public boolean isSpectating(SerennoPlayer spectator) {
		return spectators.contains(spectator);
	}

	@Override
	public void stopSpectating(SerennoPlayer spectator) {
		if (!isSpectating(spectator)) {
			return;
		}
		
		getPlaying().forEach(p -> {
			p.showPlayer(spectator);
		});
		SerennoCrimson.get().getLobby().sendToLobby(spectator);
		spectators.remove(spectator);
		broadcast(ChatColor.GOLD + spectator.getName() + ChatColor.YELLOW + " is no longer spectating.");
	}

}
