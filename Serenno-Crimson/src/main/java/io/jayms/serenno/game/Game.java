package io.jayms.serenno.game;

import java.util.List;

import org.bukkit.Location;

import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.player.SerennoPlayer;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public interface Game {
	
	int getID();
	
	int getDuration();
	
	void start();
	
	void resume();
	
	void pause(String reason);
	
	void stop(String reason);
	
	boolean isRunning();
	
	List<SerennoPlayer> getPlaying();
	
	boolean isPlaying(SerennoPlayer player);
	
	void broadcast(FancyMessage message);
	
	void broadcast(String message);
	
	void broadcast(String message, SerennoPlayer player);
	
	List<SerennoPlayer> getSpectators();
	
	void startSpectating(SerennoPlayer spectator, Location loc);
	
	void startSpectating(SerennoPlayer spectator, SerennoPlayer toSpectate);
	
	boolean isSpectating(SerennoPlayer spectator);
	
	void stopSpectating(SerennoPlayer spectator);
	
	int getCountdown();
	
	Location getSpawnPoint(ChatColor teamColor);
	
	Arena getMap();
	
}
