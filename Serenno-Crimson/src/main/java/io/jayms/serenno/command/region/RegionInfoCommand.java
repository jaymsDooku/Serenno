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

@CivCommand(id = "regioninfo")
public class RegionInfoCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return true;
		}
		
		sender.sendMessage(ChatColor.GOLD + region.getName() + ChatColor.YELLOW + "'s region information");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------");
		sender.sendMessage(ChatColor.GOLD + "Display Name: " + region.getDisplayName());
		sender.sendMessage(ChatColor.GOLD + "Parent World: " + ChatColor.YELLOW + region.getParentWorld().getName());
		sender.sendMessage(ChatColor.GOLD + "Point 1: " + ChatColor.YELLOW + region.getPoint1().toString());
		sender.sendMessage(ChatColor.GOLD + "Point 2: " + ChatColor.YELLOW + region.getPoint2().toString());
		
		StringBuilder flagSB = new StringBuilder();
		flagSB.append(ChatColor.GOLD + "{");
		int i = 1;
		for (String flag : region.getPossibleFlags()) {
			flagSB.append(ChatColor.YELLOW + flag);
			flagSB.append(ChatColor.GOLD + "=");
			flagSB.append(region.getFlags().contains(flag) ? ChatColor.GREEN  + "true": ChatColor.RED + "false");
			if (i < region.getPossibleFlags().size()) {
				flagSB.append(ChatColor.GOLD + ",");
			}
			i++;
		}
		flagSB.append(ChatColor.GOLD + "}");
		sender.sendMessage(ChatColor.GOLD + "Flags: " + ChatColor.YELLOW + flagSB.toString());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
