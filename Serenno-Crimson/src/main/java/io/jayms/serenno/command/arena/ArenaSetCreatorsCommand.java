package io.jayms.serenno.command.arena;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenasetcreators")
public class ArenaSetCreatorsCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return true;
		}
		
		String[] creatorsArr = Arrays.copyOfRange(args, 1, args.length);
		String creators = Arrays.stream(creatorsArr)
			.map(s -> ChatColor.translateAlternateColorCodes('&', s))
			.collect(Collectors.joining(" "));
		arena.setCreators(creators);
		arena.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have set the arena creators of "
				+ ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + " to: " + ChatColor.WHITE + creators);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}