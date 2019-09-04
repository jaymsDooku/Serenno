package io.jayms.serenno.command.citadel;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "bsi")
public class BastionInfoCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		cp.setBastionInfo(!cp.isBastionInfo());
		player.sendMessage(ChatColor.YELLOW + "Bastion Information: " 
				+ (cp.isBastionInfo() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
