package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "accept")
public class AcceptCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String opponent = args[0];
		Player playerOpp = Bukkit.getPlayer(opponent);
		if (playerOpp == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		Player senderPlayer = (Player) sender;
		SerennoPlayer serennoSender = SerennoCrimson.get().getPlayerManager().get(senderPlayer);
		if (!serennoSender.hasRequest(opponent)) {
			sender.sendMessage(ChatColor.RED + "You do not have a pending invite from this player.");
			return true;
		}
		serennoSender.acceptRequest(opponent);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
