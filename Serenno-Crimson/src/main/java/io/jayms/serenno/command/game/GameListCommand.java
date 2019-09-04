package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Game;
import io.jayms.serenno.game.GameManager;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "gamelist")
public class GameListCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		GameManager gameManager = SerennoCrimson.get().getGameManager();
		List<Game> games = gameManager.listGames();
		sender.sendMessage(ChatColor.YELLOW + "Games");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		games.stream().forEach(g -> {
			sender.sendMessage(ChatColor.YELLOW + Integer.toString(g.getID()) + ChatColor.GOLD 
					+ " : " + ChatColor.YELLOW + g.getMap().getName() + ChatColor.GOLD 
					+ " : " + ChatColor.YELLOW + g.getDuration());
		});
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
