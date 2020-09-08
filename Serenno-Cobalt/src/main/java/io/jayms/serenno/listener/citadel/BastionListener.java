package io.jayms.serenno.listener.citadel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;

import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupPermissions;
import net.md_5.bungee.api.ChatColor;

public class BastionListener implements Listener {

	private CitadelManager cm;
	private BastionManager bm;
	
	public BastionListener(CitadelManager cm, BastionManager bm) {
		this.cm = cm;
		this.bm = bm;
	}
	
	@EventHandler
	public void onReinforce(PlayerReinforcementCreationEvent e) {
		ItemStack item = e.getItemPlaced();
		if (item == null) {
			return;
		}
		
		bm.placeBlock(cm.getCitadelPlayer(e.getPlacer()), e.getReinforcement(), item);
	}
	
	@EventHandler
	public void onDestroy(ReinforcementDestroyEvent e) {
		bm.destroyBastion(e.getReinforcement());
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onWaterFlow(BlockFromToEvent e) {
		Block origin = e.getBlock();
		Block toBlock = e.getToBlock();

		if (bm.shouldStopBlock(origin, Arrays.asList(toBlock))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onTreeGrow(StructureGrowEvent e) {
		List<Block> blocks = e.getBlocks().stream()
				.map(bs -> bs.getBlock())
				.collect(Collectors.toList());
		
		if (bm.shouldStopBlock(e.getLocation().getBlock(), blocks)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent e) {
		Block piston = e.getBlock();
		List<Block> blocks = new ArrayList<>();
		blocks.add(piston.getRelative(e.getDirection()));
		for (Block b : e.getBlocks()) {
			blocks.add(b);
			blocks.add(b.getRelative(e.getDirection()));
		}
		
		if (bm.shouldStopBlock(piston, blocks)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonExtend(BlockPistonRetractEvent e) {
		Block piston = e.getBlock();
		List<Block> blocks = new ArrayList<>();
		blocks.add(piston.getRelative(e.getDirection()));
		for (Block b : e.getBlocks()) {
			blocks.add(b);
			blocks.add(b.getRelative(e.getDirection()));
		}
		
		if (bm.shouldStopBlock(piston, blocks)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player player = e.getPlayer();
		Block b = e.getBlockClicked();
		Set<Bastion> bastions = bm.getBastions(b.getLocation());
		
		if (bastions.isEmpty()) {
			return;
		}
		
		for (Bastion bastion : bastions) {
			Reinforcement rein = bastion.getReinforcement(SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(b.getWorld()));
			Group group = rein.getGroup();
			if (!group.isAuthorized(player, GroupPermissions.BASTION_PLACE)) {
				player.sendMessage(ChatColor.RED + "Bastion prevents bucket place. " + ChatColor.DARK_RED + "| " + ChatColor.RESET + CitadelBlockListener.getReinforcementHealth(player, rein));
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent e){
		if (!(e.getItem().getType() == Material.WATER_BUCKET || e.getItem().getType() == Material.LAVA_BUCKET || e.getItem().getType() == Material.FLINT_AND_STEEL)) return;
		
		List<Block> blocks = Arrays.asList(e.getBlock().getRelative( ((Dispenser) e.getBlock().getState().getData()).getFacing()));
		
		if (bm.shouldStopBlock(e.getBlock(), blocks)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void handleEnderPearlLanded(PlayerTeleportEvent e){
		if (e.getCause() != TeleportCause.ENDER_PEARL) return; // Only handle enderpearl cases
		
		Set<Bastion> bastions = bm.getBastions(e.getTo());
		
		if (bastions.isEmpty()) {
			return;
		}

		Player player = e.getPlayer();
		ReinforcementWorld reinWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(e.getTo().getWorld());
		for (Bastion bastion : bastions) {
			BastionBlueprint bb = bastion.getBlueprint();
			Reinforcement rein = bastion.getReinforcement(reinWorld);
			if (!bb.getPearlConfig().block()) {
				continue;
			}
			if (bb.getPearlConfig().requiresMaturity() && !rein.isMature()) {
				continue;
			}
			Group group = rein.getGroup();
			if (!group.isAuthorized(player, GroupPermissions.BASTION_PEARL)) {
				player.sendMessage(ChatColor.RED + "Bastion prevents pearling. " + ChatColor.DARK_RED + "| " + ChatColor.RESET + CitadelBlockListener.getReinforcementHealth(player, rein));
				e.setCancelled(true);
				
				if (!bb.getPearlConfig().consumeOnBlock()) {
					player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				}
				return;
			}
		}
	}
}
