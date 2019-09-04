package io.jayms.serenno.command.region;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionManager;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "regionlist")
public class RegionListCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		List<Region> regions = regionManager.listRegions();
		sender.sendMessage(ChatColor.YELLOW + "Regions");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		regions.stream().forEach(region -> {
			sender.sendMessage(ChatColor.YELLOW + region.getName());
		});
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
