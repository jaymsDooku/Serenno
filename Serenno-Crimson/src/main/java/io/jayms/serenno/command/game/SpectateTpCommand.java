package io.jayms.serenno.command.game;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

import java.util.LinkedList;
import java.util.List;

@CivCommand(id = "spectp")
public class SpectateTpCommand extends StandaloneCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
        if (!sp.inDuel()) {
            sender.sendMessage(ChatColor.RED + "You need to be spectating.");
            return true;
        }

        Duel spDuel = sp.getDuel();
        if (spDuel == null || spDuel.isSpectating(sp)) {
            sender.sendMessage(ChatColor.RED + "You need to be spectating to use this command.");
            return true;
        }

        String target = args[0];
        Player playerTarget = Bukkit.getPlayer(target);
        if (playerTarget == null) {
            sender.sendMessage(ChatColor.RED + "That player isn't online.");
            return true;
        }

        SerennoPlayer toTeleport = SerennoCrimson.get().getPlayerManager().get(playerTarget);
        if (!toTeleport.inDuel()) {
            sender.sendMessage(ChatColor.RED + "That player isn't in a duel at the moment.");
            return true;
        }

        Duel teleportDuel = toTeleport.getDuel();
        if (teleportDuel.getID() != spDuel.getID()) {
            sender.sendMessage(ChatColor.RED + "You must be in the same duel.");
            return true;
        }

        sp.teleport(playerTarget.getLocation());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new LinkedList<>();
    }

}
