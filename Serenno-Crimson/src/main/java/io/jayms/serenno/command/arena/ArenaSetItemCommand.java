package io.jayms.serenno.command.arena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "arenasetitem")
public class ArenaSetItemCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return true;
		}
		
		ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
		arena.setDisplayItem(mainHand);
		arena.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have set the arena display item of "
				+ ChatColor.GOLD + arena.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
