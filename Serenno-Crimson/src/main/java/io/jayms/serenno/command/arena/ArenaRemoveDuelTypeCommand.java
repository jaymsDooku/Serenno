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

@CivCommand(id = "arenaremovedueltype")
public class ArenaRemoveDuelTypeCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		String duelTypeStr = args[1].toUpperCase();
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		Arena arena = arenaManager.getArena(regionName);
		DuelType duelType = DuelType.valueOf(duelTypeStr);
		arena.getDuelTypes().add(duelType);
		arena.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have removed " + ChatColor.GOLD + duelType + ChatColor.YELLOW + " from " + ChatColor.GOLD + arena.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}

