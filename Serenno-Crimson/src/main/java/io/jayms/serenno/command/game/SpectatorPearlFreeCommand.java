package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.vaultbattle.VaultBattle;
import io.jayms.serenno.game.vaultbattle.pearling.SpectatorPearlItem;
import io.jayms.serenno.game.vaultbattle.pearling.SpectatorPearlManager;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "spfree")
public class SpectatorPearlFreeCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		Duel duel = sp.getDuel();
		if (duel == null || !(duel instanceof VaultBattle)) {
			player.sendMessage(ChatColor.RED + "You can only use this command in a vault battle.");
			return true;
		}
		
		ItemStack pearlStack = player.getInventory().getItemInMainHand();
		if (pearlStack.getType() != Material.ENDER_PEARL) {
			player.sendMessage(ChatColor.RED + "You must be holding a pearl.");
			return true;
		}
		
		CustomItem item = CustomItemManager.getCustomItemManager().getCustomItem(pearlStack);
		if (item == null || !(item instanceof SpectatorPearlItem)) {
			player.sendMessage(ChatColor.RED + "You must be holding a pearl.");
			return true;
		}
		
		VaultBattle vb = (VaultBattle) duel;
		SpectatorPearlManager pearlManager = vb.getPearlManager();
		pearlManager.free(sp, (SpectatorPearlItem) item, pearlStack);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}

