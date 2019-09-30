package io.jayms.serenno.player;

import org.bukkit.entity.Player;

import io.jayms.serenno.rank.Rank;

public class CommonPlayer {
	
	private final Player bukkitPlayer;
	
	private Rank rank;
	
	public CommonPlayer(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
	}
	
	public Player getBukkitPlayer() {
		return bukkitPlayer;
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
	public Rank getRank() {
		return rank;
	}

}
