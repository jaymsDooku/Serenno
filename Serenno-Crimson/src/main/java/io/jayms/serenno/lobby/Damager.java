package io.jayms.serenno.lobby;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.maxopoly.finale.Finale;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DefaultKits;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class Damager {

	private String name;
	private Location p1;
	private Location p2;
	private double damage;
	private Map<Player, DamagerRunnable> damaging = new HashMap<>();
	
	public Damager(String name, Location p1, Location p2, double damage) {
		this.name = name;
		this.p1 = p1;
		this.p2 = p2;
		this.damage = damage;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean in(Location loc) {
		return LocationTools.isBetween(p1, p2, loc);
	}
	
	public double getDamage() {
		return damage;
	}
	
	private DecimalFormat dp1 = new DecimalFormat("##.#");
	
	public void startDamaging(Player player) {
		DamagerRunnable damagerRunnable = new DamagerRunnable(player, this);
		damagerRunnable.runTaskTimer(SerennoCrimson.get(), 0L, 1L);
		damaging.put(player, damagerRunnable);
		double heartDmg = damage / 2;
		player.sendMessage(ChatColor.YELLOW + "You have entered the " + ChatColor.RED + dp1.format(heartDmg) + "♥ Damager");
		
		Kit healthKit = new Kit().range(0, 35, DefaultKits.health());
		healthKit.load(player);
	}
	
	public void stopDamaging(Player player) {
		DamagerRunnable damagerRunnable = damaging.remove(player);
		if (!damagerRunnable.isCancelled()) damagerRunnable.cancel();
		
		double heartDmg = damage / 2;
		player.sendMessage(ChatColor.YELLOW + "You have exited the " + ChatColor.RED + dp1.format(heartDmg) + "♥ Damager");
		
		SerennoCrimson.get().getLobby().giveItems(SerennoCrimson.get().getPlayerManager().get(player));
	}
	
	public boolean isDamaging(Player player) {
		return damaging.containsKey(player);
	}

	@Override
	public String toString() {
		return "Damager [name=" + name + ", p1=" + p1 + ", p2=" + p2 + ", damage=" + damage + ", damaging=" + damaging
				+ "]";
	}
	
}
