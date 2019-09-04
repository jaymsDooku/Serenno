package io.jayms.serenno.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

	private CitadelManager cm = SerennoCobalt.get().getCitadelManager();
	
	@Subcommand("give")
	public void give(Player player, String thingType, String thingName, int amount) {
		ItemStack giveIt;
		if (thingType.equalsIgnoreCase("reinforcement")) {
			ReinforcementManager rm = cm.getReinforcementManager();
			ReinforcementBlueprint rb = rm.getReinforcementBlueprint(thingName);
			giveIt = rb.getItemStack();
			giveIt.setAmount(amount);
		} else if (thingType.equalsIgnoreCase("bastion")) {
			BastionManager bm = cm.getBastionManager();
			BastionBlueprint bb = bm.getBastionBlueprint(thingName);
			giveIt = bb.getItemStack();
			giveIt.setAmount(amount);
		} else {
			player.sendMessage(ChatColor.RED + "Citadel doesn't have that thing to give you.");
			return;
		}
		player.getInventory().addItem(giveIt);
	}
	
}
