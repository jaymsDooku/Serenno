package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.Game;
import io.jayms.serenno.game.GameManager;
import io.jayms.serenno.game.Team;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.PlayerTools;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.MultiPageView;

@CivCommand(id = "gamestats")
public class GameStatsCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String gameIdStr = args[0];
		int gameId = NumberConversions.toInt(gameIdStr);
		GameManager gameManager = SerennoCrimson.get().getGameManager();
		Game game = gameManager.getGame(gameId);
		
		if (game == null) {
			sender.sendMessage(ChatColor.RED + "Game with that ID doesn't exist.");
			return true;
		}
		
		if (!(game instanceof Duel)) {
			sender.sendMessage(ChatColor.RED + "Unsupported game type.");
			return true;
		}
		
		Duel duel = (Duel) game;
		DuelStatistics stats = duel.getStatistics();
		
		Player player = (Player) sender;
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().getPlayer(player);
		DuelTeam team = duel.getTeam(sPlayer);
		
		List<IClickable> clicks = new LinkedList<IClickable>();
		for (int i = 0; i < 9; i++) {
			clicks.add(new DecorationStack(new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1).durability((short)15).build()));
		}
		ItemStack totalStats = new ItemStack(Material.PAPER, 1);
		stats.loreStatsTotal(totalStats, team);
		
		clicks.set(4, new DecorationStack(totalStats));
		
		DuelTeam winners = duel.getWinner();
		Team winnerTeam = winners.getTeam();
		String winnerName = winnerTeam.getLeader().getName();
		ItemStack winnerIt = PlayerTools.getHead(winnerTeam.getLeader().getUniqueId(), ChatColor.DARK_GREEN + winnerName + (winnerTeam.size() > 1 ? "'s Team" : ""));
		stats.loreStatsTeamTotal(winnerIt, team, winners);
		clicks.set(2, new Clickable(winnerIt) {
			
			@Override
			public void clicked(Player p) {
				
			}
			
		});
		
		DuelTeam losers = duel.getLoser();
		Team loserTeam = losers.getTeam();
		String loserName = loserTeam.getLeader().getName();
		ItemStack loserIt = PlayerTools.getHead(loserTeam.getLeader().getUniqueId(), ChatColor.DARK_GREEN + loserName + (winnerTeam.size() > 1 ? "'s Team" : ""));
		stats.loreStatsTeamTotal(loserIt, team, losers);
		clicks.set(6, new Clickable(loserIt) {
			
			@Override
			public void clicked(Player p) {
				
			}
			
		});
		
		MultiPageView pageView = new MultiPageView((Player) sender, clicks,
				ChatColor.WHITE + "Game " + ChatColor.RED + "#" + game.getID(), true);
		pageView.showScreen();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
