package io.jayms.serenno.command;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "lobby")
public class LobbyCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		SerennoPlayer serennoPlayer = SerennoCrimson.get().getPlayerManager().get(player);
		player.sendMessage(ChatColor.YELLOW + "Sending you to the lobby...");
		SerennoCrimson.get().getLobby().sendToLobby(serennoPlayer);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}