package io.jayms.serenno.command;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.PrebuiltManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.prebuilt.PrebuiltStructure;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("prebuilt")
public class PrebuiltCommand extends BaseCommand {

	private PrebuiltManager pm = SerennoCobalt.get().getPrebuiltManager();
	private CitadelManager cm = SerennoCobalt.get().getCitadelManager();
	
	@Subcommand("save")
	public void save(Player player, String pbName) {
		pm.save(player, pbName);
		player.sendMessage(ChatColor.YELLOW + "You have saved a prebuilt structure to: " + ChatColor.GOLD + pbName);
	}
	
	@Subcommand("load")
	public void load(Player player, String pbName) {
		PrebuiltStructure ps = pm.load(player, pbName);
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		ps.load(player, cp.getDefaultGroup(), player.getWorld());
		player.sendMessage(ChatColor.YELLOW + "You have loaded a prebuilt structure to: " + ChatColor.GOLD + pbName);
	}
	
}
