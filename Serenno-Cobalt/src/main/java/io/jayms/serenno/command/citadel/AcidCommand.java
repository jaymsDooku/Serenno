package io.jayms.serenno.command.citadel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.PlayerAcidBlockEvent;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.group.GroupPermissions;
import io.jayms.serenno.util.MathTools;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "ctacid")
public class AcidCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		ReinforcementManager rm = cm.getReinforcementManager();
		ReinforcementWorld rw = rm.getReinforcementWorld(player.getWorld());
		ReinforcementDataSource dataSource = rw.getDataSource();
		Iterator<Block> it = new BlockIterator(player, 40);
		while (it.hasNext()) {
			Block b = it.next();
			if (b.getType() == Material.AIR) {
				continue;
			}
			boolean isntAcidBlock = b.getType() != Material.GOLD_BLOCK; 
			if (dataSource != null) {
				isntAcidBlock = !dataSource.isAcidBlock(b.getType());
			}
			if (isntAcidBlock) {
				player.sendMessage(ChatColor.RED + "That is not a acid block material.");
				return true;
			}
			Reinforcement rein = rm.getReinforcement(b);
			if (rein == null) {
				player.sendMessage(ChatColor.RED + "That block is not reinforced.");
				return true;
			}
			if (!rein.hasPermission(player, GroupPermissions.ACID)) {
				player.sendMessage(ChatColor.RED + "You do not have sufficient permission to use acid blocks on this group.");
				return true;
			}
			
			double acidProgress = rein.getAcidProgress();
			if (acidProgress < 1) {
				player.sendMessage(ChatColor.RED + "That acid block will be mature in " + MathTools.formatDuration(rein.getAcidTimeRemaining(), TimeUnit.MILLISECONDS));
				return true;
			}
			Block topFace = b.getRelative(BlockFace.UP);
			if (topFace == null || topFace.getType() == Material.AIR) {
				player.sendMessage(ChatColor.RED + "There is no block above the acid block.");
				return true;
			}
			
			Reinforcement topRein = rm.getReinforcement(topFace);
			if (topRein == null) {
				player.sendMessage(ChatColor.RED + "The block above is not reinforced.");
				return true;
			}
			if (!topRein.getBlueprint().equals(rein.getBlueprint())) {
				String topReinDisplay = topRein.getBlueprint().getDisplayName();
				String reinDisplay = rein.getBlueprint().getDisplayName();
				player.sendMessage(reinDisplay + ChatColor.RED + " can not acid away " + topReinDisplay);
				return true;
			}
			
			PlayerAcidBlockEvent event = new PlayerAcidBlockEvent(player, rein, topRein);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				return true;
			}
			
			rein.destroy();
			topRein.destroy();
			b.breakNaturally();
			topFace.breakNaturally();
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
