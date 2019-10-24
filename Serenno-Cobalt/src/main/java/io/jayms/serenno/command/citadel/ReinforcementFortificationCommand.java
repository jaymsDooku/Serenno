package io.jayms.serenno.command.citadel;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.PlayerFortificationModeEvent;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.ReinforcementMode;
import io.jayms.serenno.model.citadel.ReinforcementMode.ReinforceMethod;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupPermissions;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "ctf")
public class ReinforcementFortificationCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		ReinforcementBlueprint blueprint = cm.getReinforcementManager().getReinforcementBlueprint(mainHand);
		Group group = cp.getDefaultGroup();
		String argGroupName = null;
		
		if (args.length > 0) {
			argGroupName = args[0].toLowerCase();
			group = SerennoCobalt.get().getGroupManager().getGroup(player, argGroupName);
		}
		
		PlayerFortificationModeEvent event = new PlayerFortificationModeEvent(player, group, argGroupName, mainHand, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		group = event.getGroup();
		blueprint = event.getBlueprint();
		
		if (group == null) {
			player.sendMessage(ChatColor.RED + "Set a default group.");
			return true;
		}
		if (!group.isAuthorized(player, GroupPermissions.REINFORCEMENT_BYPASS)) {
			player.sendMessage(ChatColor.RED + "You do not have permission to fortify on this group.");
			return true;
		}
		ReinforcementMode currentFortMode = cp.getReinforcementMode();
		if (currentFortMode != null) {
			Group curGroup = currentFortMode.getGroupToReinforce();
			ReinforcementBlueprint curBlueprint = currentFortMode.getReinforcementBlueprint();
			
			if (group == curGroup && blueprint == curBlueprint) {
				cp.setReinforcementMode(null);
				player.sendMessage(ChatColor.YELLOW + "You are " + ChatColor.RED + "no longer fortifying");
				return true;
			}
		}
		
		group = event.getGroup();
		blueprint = event.getBlueprint();
		
		cp.setReinforcementMode(new ReinforcementMode(blueprint, group, ReinforceMethod.FORTIFY));
		player.sendMessage(ChatColor.YELLOW + "You are " + ChatColor.GREEN + "now fortifying" 
				+ ChatColor.YELLOW + " with " + blueprint.getDisplayName() + ChatColor.YELLOW + " to " + ChatColor.GOLD + group.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
 