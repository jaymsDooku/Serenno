package io.jayms.serenno.command.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "duel")
public class DuelCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String opponent = args[0];
		Player playerOpp = Bukkit.getPlayer(opponent);
		if (playerOpp == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		
		Player senderPlayer = (Player) sender;
		if (senderPlayer.getUniqueId().equals(playerOpp.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You can't duel yourself.");
			return true;
		}
		
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(senderPlayer);
		SerennoPlayer sOpp = SerennoCrimson.get().getPlayerManager().get(playerOpp);
		Team team = SerennoCrimson.get().getTeamManager().getTeam(sp);
		if (team != null) {
			if (!team.isLeader(sp)) {
				senderPlayer.sendMessage(ChatColor.RED + "Only the team leader can duel other teams.");
				return true;
			}
			if (team.inTeam(sOpp)) {
				senderPlayer.sendMessage(ChatColor.RED + "You can't duel members of your own team.");
				return true;
			}
		}
		
		Map<String, Object> initData = new HashMap<>();
		initData.put("toDuel", sOpp);
		SerennoCrimson.get().getGameManager().getDuelTypeMenu().open(senderPlayer, initData);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}

