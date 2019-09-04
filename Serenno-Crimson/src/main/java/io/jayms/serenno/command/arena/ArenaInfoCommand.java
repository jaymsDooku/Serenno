package io.jayms.serenno.command.arena;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenainfo")
public class ArenaInfoCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String arenaName = args[0];
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		Arena arena = arenaManager.getArena(arenaName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return true;
		}
		
		sender.sendMessage(ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + "'s arena information");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------");
		sender.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.YELLOW + arena.getDescription());
		sender.sendMessage(ChatColor.GOLD + "Creators: " + ChatColor.YELLOW + arena.getCreators());
		sender.sendMessage(ChatColor.GOLD + "Duel Types: " + ChatColor.GOLD + "[" + arena.getDuelTypes().stream().map(d -> d.getDisplayName()).collect(Collectors.joining(",")) + ChatColor.GOLD + "]");
		
		StringBuilder spawnsSB = new StringBuilder();
		spawnsSB.append(ChatColor.GOLD + "{");
		int i = 1;
		for (Entry<ChatColor, Location> spawnEn : arena.getSpawnPoints().entrySet()) {
			ChatColor teamColor = spawnEn.getKey();
			spawnsSB.append(teamColor + teamColor.getName());
			spawnsSB.append(ChatColor.GOLD + "=");
			spawnsSB.append(ChatColor.YELLOW + spawnEn.getValue().toString());
			if (i < arena.getSpawnPoints().size()) {
				spawnsSB.append(ChatColor.GOLD + ",");
			}
			i++;
		}
		spawnsSB.append(ChatColor.GOLD + "}");
		sender.sendMessage(ChatColor.GOLD + "Spawns: " + ChatColor.YELLOW + spawnsSB.toString());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}

