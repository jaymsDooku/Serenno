package io.jayms.serenno.listener;

import java.text.DecimalFormat;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.ReinforcementMode;
import io.jayms.serenno.model.citadel.ReinforcementMode.ReinforceMethod;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.NumberUtils;
import io.jayms.serenno.util.PlayerTools;
import net.md_5.bungee.api.ChatColor;

public class CitadelListener implements Listener {

	private CitadelManager cm;
	private ReinforcementManager rm;
	private BastionManager bm;
	
	private final DecimalFormat df = new DecimalFormat("##.##");
	
	public CitadelListener(CitadelManager cm, ReinforcementManager rm, BastionManager bm) {
		this.cm = cm;
		this.rm = rm;
		this.bm = bm;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player); 
		Block b = e.getBlock();
		
		boolean bastionPrevent = bm.placeBlock(cp, b);
		boolean reinPrevent = rm.placeBlock(cp, b);
		
		e.setCancelled(reinPrevent || bastionPrevent);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block b = e.getBlock();
		e.setCancelled(rm.breakBlock(cp, b));
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}
		
		Reinforcement reinforcement = rm.getReinforcement(clickedBlock);
		
		if (reinforcement != null) {
			if (cp.isReinforcementInfo()) {
				ReinforcementBlueprint rb = reinforcement.getBlueprint();
				Group group = reinforcement.getGroup();
				boolean apartOf = group.isMember(player);
				ChatColor grpColor = apartOf ? ChatColor.GREEN : ChatColor.RED;
				String grpName = apartOf ? group.getName() : "Unknown";
				ChatColor healthPC = NumberUtils.getPrimaryColor(reinforcement.getHealthAsPercentage());
				ChatColor healthSC = NumberUtils.getSecondaryColor(reinforcement.getHealthAsPercentage());
				double health = reinforcement.getHealth();
				double maxHealth = rb.getMaxHealth();
				String healthStr = df.format(reinforcement.getHealthAsPercentage() * 100) + "%";
				
				player.sendMessage(rb.getDisplayName() + ChatColor.YELLOW + " | "
						+ healthPC + healthStr + healthSC + " (" + healthPC + health + healthSC + "/" + healthPC + maxHealth + healthSC + ")" + ChatColor.YELLOW + " | "
						+ ChatColor.GOLD + "Group: " + grpColor + grpName 
						);
			}
		} else {
			ReinforcementMode reinMode = cp.getReinforcementMode();
			if (reinMode != null && PlayerTools.isLeftClick(e.getAction()) && reinMode.getMethod() == ReinforceMethod.REINFORCE) {
				rm.reinforceBlock(cp, clickedBlock);
			}
		}
		
		Set<Bastion> bastions = bm.getBastions(clickedBlock.getLocation());
		
		if (!bastions.isEmpty() && (cp.isBastionInfo() && PlayerTools.isRightClick(e.getAction()))) {
			Bastion bastion = bastions.iterator().next();
			Reinforcement rein = bastion.getReinforcement();
			Group group = rein.getGroup();
			if (group.isMember(player)) {
				player.sendMessage(ChatColor.GREEN + "Friendly bastion" + ChatColor.YELLOW + " | " + ChatColor.GOLD + "Group: " + ChatColor.GREEN + group.getName());
			} else {
				player.sendMessage(ChatColor.RED + "Unfriendly bastion.");
			}
		}
	}
	
}
