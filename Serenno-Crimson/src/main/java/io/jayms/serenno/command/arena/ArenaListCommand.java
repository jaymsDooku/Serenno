package io.jayms.serenno.command.arena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenalist")
public class ArenaListCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		List<Arena> arenas = arenaManager.listArenas();
		sender.sendMessage(ChatColor.YELLOW + "Arenas");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		arenas.stream().forEach(a -> {
			sender.sendMessage(ChatColor.YELLOW + a.getName());
		});
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
