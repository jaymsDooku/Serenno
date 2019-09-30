package io.jayms.serenno.game;

import java.util.UUID;

import org.bukkit.Location;

import mkremins.fanciful.FancyMessage;

public interface Duelable {

	UUID getID();

	String getName();
	
	void sendMessage(String message);
	
	void sendMessage(FancyMessage message);
	
	boolean inDuel();
	
	boolean isSpectating();
	
	void teleport(Location loc);
	
	void showPlayer(Duelable duelable);
	
	void hidePlayer(Duelable duelable);
	
}
