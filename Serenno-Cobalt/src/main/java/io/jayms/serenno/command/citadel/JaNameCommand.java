package io.jayms.serenno.command.citadel;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.group.GroupPermissions;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "janame")
public class JaNameCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		SnitchManager sm = cm.getSnitchManager();
		Location playerLoc = player.getLocation();
		Set<Snitch> snitches = sm.getSnitches(playerLoc);
		if (snitches.isEmpty()) {
			player.sendMessage(ChatColor.RED + "There are no snitches nearby.");
			return true;
		}
		Snitch toName = null;
		if (snitches.size() == 1) {
			toName = snitches.iterator().next();
		} else {
			double lowestDist = Double.MAX_VALUE;
			for (Snitch snitch : snitches) {
				Reinforcement rein = snitch.getReinforcement();
				if (!rein.hasPermission(player, GroupPermissions.SNITCH_RENAME)) {
					continue;
				}
				double dist = snitch.getLocation().distanceSquared(playerLoc);
				if (dist < lowestDist) {
					toName = snitch;
					lowestDist = dist;
				}
			}
		}
		
		if (toName == null) {
			player.sendMessage(ChatColor.RED + "There are no snitches nearby.");
			return true;
		}
		
		String name = args[0];
		toName.setName(name);
		
		Location toNameLoc = toName.getLocation();
		player.sendMessage(ChatColor.AQUA + "You have renamed snitch at " 
				+ ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + toNameLoc.getBlockX() + ChatColor.DARK_AQUA + ", " + ChatColor.DARK_AQUA + ", " + ChatColor.DARK_AQUA + "]" 
				+ ChatColor.AQUA + " to " + ChatColor.DARK_AQUA + name);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
