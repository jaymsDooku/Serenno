package io.jayms.serenno.command.arena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import io.jayms.serenno.game.DuelType;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenaadddueltype")
public class ArenaAddDuelTypeCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		String duelTypeStr = args[1].toUpperCase();
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return true;
		}
		DuelType duelType = DuelType.valueOf(duelTypeStr);
		if (duelType == null) {
			sender.sendMessage(ChatColor.RED + "That duel type doesn't exist.");
			return true;
		}
		arena.getDuelTypes().add(duelType);
		arena.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.GOLD + duelType + ChatColor.YELLOW + " to " + ChatColor.GOLD + arena.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
