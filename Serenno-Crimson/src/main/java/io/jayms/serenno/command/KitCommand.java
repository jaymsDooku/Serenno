package io.jayms.serenno.command;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("kit")
public class KitCommand extends BaseCommand {

	@Subcommand("clearall")
	public void clearAll(Player player, Player target) {
		SerennoPlayer serennoPlayer = SerennoCrimson.get().getPlayerManager().get(target);
		serennoPlayer.setKits(new ConcurrentHashMap<>());
		player.sendMessage(ChatColor.YELLOW + "You have cleared all of " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s kits.");
	}
	
}
