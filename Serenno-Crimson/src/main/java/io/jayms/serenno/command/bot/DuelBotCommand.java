package io.jayms.serenno.command.bot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.bot.Bot;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "duelbot")
public class DuelBotCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String name = args[0];
		if (name.length() > 16) {
			sender.sendMessage(ChatColor.RED + "Bot name can't be greater than 16 characters long.");
			return true;
		}
		
		Bot opponent = new Bot(name);
		opponent.spawn((botPlayer) -> {
			Player trainee = (Player) sender;
			DuelType duelType = DuelType.NODEBUFF;
			
			SerennoPlayer sBotPlayer = SerennoCrimson.get().getPlayerManager().get(botPlayer);
			
			Map<String, Object> initData = new HashMap<>();
			initData.put("toDuel", sBotPlayer);
			initData.put("duelType", duelType);
			SerennoCrimson.get().getGameManager().getArenaSelectMenu().open(trainee, initData);
		});
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
