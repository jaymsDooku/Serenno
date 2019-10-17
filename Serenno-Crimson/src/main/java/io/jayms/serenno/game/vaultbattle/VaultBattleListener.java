package io.jayms.serenno.game.vaultbattle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.vault.Core;
import io.jayms.serenno.vault.event.CoreDestroyEvent;
import net.md_5.bungee.api.ChatColor;

public class VaultBattleListener implements Listener {

	@EventHandler
	public void onCoreDestroy(CoreDestroyEvent e) {
		Player destroyer = e.getDestroyer();
		Core core = e.getCore();
		VaultBattle battle = core.getVaultMapDatabase().getBattle();
		DuelTeam losers = battle.getTeam(core.getTeamColor());
		DuelTeam winners = battle.getOtherTeam(losers);
		
		Team losersTeam = losers.getTeam();
		
		String message = destroyer == null ? 
				losers.getTeamColor() + losersTeam.getName() + "'s " + ChatColor.YELLOW + " core has been destroyed!" :
				ChatColor.GOLD + destroyer.getName() + ChatColor.YELLOW + " has destroyed " + losers.getTeamColor() + losersTeam.getName() + "'s " + ChatColor.YELLOW + " core!";
		
		battle.broadcast(message);
		battle.finish(winners, losers);
	}
	
}
