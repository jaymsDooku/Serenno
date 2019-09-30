package io.jayms.serenno.lobby;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DamagerRunnable extends BukkitRunnable {

	private Player player;
	private Damager damager;
	
	public DamagerRunnable(Player player, Damager damager) {
		this.player = player;
		this.damager = damager;
	}
	
	@Override
	public void run() {
		if (player.isDead()) {
			if (!this.isCancelled()) cancel();
			return;
		}
		
		player.damage(damager.getDamage());
	}
	
}
