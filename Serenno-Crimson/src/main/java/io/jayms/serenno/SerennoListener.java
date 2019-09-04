package io.jayms.serenno;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.maxopoly.finale.Finale;
import com.github.maxopoly.finale.combat.CPSHandler;
import com.github.maxopoly.finale.combat.Hit;

import io.jayms.serenno.bot.BotTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.ai.event.NavigationBeginEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;

public class SerennoListener implements Listener {
	
	/*@EventHandler
	public void navigateBegin(NavigationBeginEvent e) {
		if (!e.getNPC().getUniqueId().equals(bot.getUniqueId())) {
			return;
		}
		Player player = (Player) e.getNPC().getEntity();
		player.setSprinting(true);
	}*/
	
}

