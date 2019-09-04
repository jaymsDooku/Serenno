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

@CivCommand(id = "regionsetdisplayname")
public class RegionSetDisplayNameCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return true;
		}
		
		String displayName = args[1];
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		region.setDisplayName(displayName);
		region.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have set display name of region " 
		+ ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " to " + displayName);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
