package io.jayms.serenno.command;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.Game;
import io.jayms.serenno.game.GameManager;

@CommandAlias("game")
public class GameCommand extends BaseCommand {
	
	private GameManager gameManager = SerennoCrimson.get().getGameManager();

	@Subcommand("info")
	public void info(CommandSender sender, int gameId) {
	}
	
	@Subcommand("list")
	public void list(CommandSender sender) {
		List<Game> games = gameManager.listGames();
		sender.sendMessage(ChatColor.YELLOW + "Games");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		games.stream().forEach(g -> {
			sender.sendMessage(ChatColor.YELLOW + Integer.toString(g.getID()) + ChatColor.GOLD 
					+ " : " + ChatColor.YELLOW + g.getMap().getName() + ChatColor.GOLD 
					+ " : " + ChatColor.YELLOW + g.getDuration());
		});
	}
	
	@Subcommand("stats")
	public void stats(Player player, int gameId) {
		GameManager gameManager = SerennoCrimson.get().getGameManager();
		Game game = gameManager.getGame(gameId);
		
		if (game == null) {
			player.sendMessage(ChatColor.RED + "Game with that ID doesn't exist.");
			return;
		}
		
		if (!(game instanceof Duel)) {
			player.sendMessage(ChatColor.RED + "Unsupported game type.");
			return;
		}
		
		Duel duel = (Duel) game;
		duel.getMatchReportMenu().open(player, new HashMap<>());
	}
	
}
