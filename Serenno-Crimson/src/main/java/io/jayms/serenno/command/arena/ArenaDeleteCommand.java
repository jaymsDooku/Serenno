package io.jayms.serenno.command.arena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.ArenaManager;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenadelete")
public class ArenaDeleteCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		arenaManager.deleteArena(arenaManager.getArena(regionName));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
