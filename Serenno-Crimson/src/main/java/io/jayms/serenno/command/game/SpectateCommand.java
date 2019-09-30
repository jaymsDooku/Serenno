package io.jayms.serenno.command.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "spectate")
public class SpectateCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String target = args[0];
		Player playerTarget = Bukkit.getPlayer(target);
		if (playerTarget == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		
		SerennoPlayer toSpectate = SerennoCrimson.get().getPlayerManager().get(playerTarget);
		if (!toSpectate.inDuel()) {
			sender.sendMessage("That player isn't in a duel at the moment.");
			return true;
		}
		
		Player player = (Player) sender;
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		
		Duel duel = toSpectate.getDuel();
		duel.startSpectating(sp, toSpectate);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
