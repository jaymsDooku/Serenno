package io.jayms.serenno;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

import net.md_5.bungee.api.ChatColor;

public class SerennoCommonListener implements Listener {

	private SerennoCommonConfigManager config;
	
	public SerennoCommonListener(SerennoCommonConfigManager config) {
		this.config = config;
	}
	
	private static final List<String> MOTD = Arrays.asList(ChatColor.RED + "Who do you think you are?",
			ChatColor.RED + "What gives you the right?",
			ChatColor.RED + "Mmm, that's sort of a oaky afterbirth.",
			ChatColor.RED + "There he is! There's the traitor!",
			ChatColor.RED + "Live and let live!",
			ChatColor.RED + "No rest for the sick.",
			ChatColor.RED + "I am not to be truffled with."
			);
	
	private static int motdInd = 0; 
	
	@EventHandler
	public void onServerPing(ServerListPingEvent e) {
		e.setMotd(ChatColor.DARK_RED + "Serenno" + ChatColor.DARK_RED + " [" + ChatColor.RED + ChatColor.ITALIC + "Crimson" + ChatColor.RESET + ChatColor.DARK_RED + "]"  + ChatColor.WHITE + " - " + MOTD.get(motdInd));
		motdInd++;
		if (motdInd >= MOTD.size()) {
			motdInd = 0;
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (!config.isMaintenanceEnabled()) {
			return;
		}
		
		if (config.getMaintenanceWhitelist().contains(e.getPlayer().getName())) {
			return;
		}
		
		e.setKickMessage(ChatColor.RED + "Undergoing maintenance. Come back later.");
		e.setResult(Result.KICK_OTHER);
	}
	
}
