package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.MultiPageView;

@CivCommand(id = "duel")
public class DuelCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String opponent = args[0];
		Player playerOpp = Bukkit.getPlayer(opponent);
		if (playerOpp == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		
		Player senderPlayer = (Player) sender;
		if (senderPlayer.getUniqueId().equals(playerOpp.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You can't duel yourself.");
			return true;
		}
		
		SerennoPlayer sSender = SerennoCrimson.get().getPlayerManager().getPlayer(senderPlayer);
		SerennoPlayer sOpp = SerennoCrimson.get().getPlayerManager().getPlayer(playerOpp);
		
		List<IClickable> duelTypeClicks = new LinkedList<>();
		DuelType[] duelTypes = DuelType.values();
		for (int i = 0; i < duelTypes.length; i++) {
			DuelType duelType = duelTypes[i];
			ItemStack is = duelType.getDisplayItem();
			IClickable duelTypeClick = new Clickable(is) {
				
				@Override
				public void clicked(Player p) {
					ClickableInventory.forceCloseInventory(senderPlayer);
					List<IClickable> mapClicks = Arena.getArenaClickables(sSender, sOpp, duelType);
					if (mapClicks.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "No maps exist for this gamemode. :(");
						return;
					}
					
					MultiPageView mapView = new MultiPageView(senderPlayer, mapClicks,
							ChatColor.YELLOW + "Choose Map", true);
					mapView.showScreen();
				}
				
				@Override
				public void addedToInventory(ClickableInventory inv, int slot) {
					super.addedToInventory(inv, slot);
					if (inv.getInventory() != null) {
						inv.getInventory().setItem(slot, getItemStack());
					}
				}
				
			};
			duelTypeClicks.add(duelTypeClick);
		}
		MultiPageView duelTypeView = new MultiPageView(senderPlayer, duelTypeClicks,
				ChatColor.YELLOW + "Choose Duel Type", true);
		duelTypeView.showScreen();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}

