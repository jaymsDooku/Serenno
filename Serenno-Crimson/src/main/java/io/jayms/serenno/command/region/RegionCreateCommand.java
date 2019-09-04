package io.jayms.serenno.command.region;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "regioncreate")
public class RegionCreateCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.createRegion((Player) sender, regionName);
		sender.sendMessage(ChatColor.YELLOW + "You have created a new region: " + ChatColor.GOLD + region.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
