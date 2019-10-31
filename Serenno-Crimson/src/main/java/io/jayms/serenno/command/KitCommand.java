package io.jayms.serenno.command;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DefaultKits;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.ItemUtil;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("kit")
public class KitCommand extends BaseCommand {

	@Subcommand("clearall")
	public void clearAll(Player player, String targetName) {
		Player target = Bukkit.getPlayer(targetName);
		if (target == null) {
			player.sendMessage(ChatColor.RED + "That player is not online.");
			return;
		}
		
		SerennoPlayer serennoPlayer = SerennoCrimson.get().getPlayerManager().get(target);
		serennoPlayer.setKits(new ConcurrentHashMap<>());
		player.sendMessage(ChatColor.YELLOW + "You have cleared all of " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s kits.");
	}
	
	@Subcommand("give")
	public void giveItem(Player player, String item, int amount) {
		item = item.toLowerCase();
		ItemStack itemStack;
		switch (item) {
			case "fres":
			case "fire-resistance":
				itemStack = DefaultKits.fres(8 * 60);
				break;
			case "regen":
			case "regeneration":
				itemStack = DefaultKits.regen(150);
				break;
			case "strength":
				itemStack = DefaultKits.strength(150);
				break;
			case "speed":
				itemStack = DefaultKits.speed(150);
				break;
			case "poison":
				itemStack = DefaultKits.poisonExtended(33);
				break;
			case "slowness":
				itemStack = DefaultKits.slow(67);
				break;
			case "dhelmet":
				itemStack = DefaultKits.dhelmet();
				break;
			case "dchest":
				itemStack = DefaultKits.dchest();
				break;
			case "dlegs":
				itemStack = DefaultKits.dlegs();
				break;
			case "dboots":
				itemStack = DefaultKits.dboots();
				break;
			case "lhelmet":
				itemStack = DefaultKits.lhelmet();
				break;
			case "lchest":
				itemStack = DefaultKits.lchest();
				break;
			case "llegs":
				itemStack = DefaultKits.llegs();
				break;
			case "lboots":
				itemStack = DefaultKits.lboots();
				break;
			case "ihelmet":
				itemStack = DefaultKits.ihelmet();
				break;
			case "ichest":
				itemStack = DefaultKits.ichest();
				break;
			case "ilegs":
				itemStack = DefaultKits.ilegs();
				break;
			case "iboots":
				itemStack = DefaultKits.iboots();
				break;
			case "bow":
				itemStack = DefaultKits.bow();
				break;
			default:
				itemStack = null;
				break;
		}
		
		if (itemStack == null) {
			player.sendMessage(ChatColor.RED + "That item doesn't exist.");
			return;
		}
		
		itemStack.setAmount(amount);
		player.getInventory().addItem(itemStack);
		player.sendMessage(ChatColor.YELLOW + "You have been given " + ChatColor.WHITE + amount + " " + ItemUtil.getName(itemStack));
	}
	
}
