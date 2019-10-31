package io.jayms.serenno.listener.citadel;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Comparator;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.ReinforcementMode;
import io.jayms.serenno.model.citadel.ReinforcementMode.ReinforceMethod;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupPermissions;
import io.jayms.serenno.util.Cooldown;
import io.jayms.serenno.util.LocationTools;
import io.jayms.serenno.util.MathTools;
import io.jayms.serenno.util.NumberUtils;
import io.jayms.serenno.util.PlayerTools;
import net.md_5.bungee.api.ChatColor;

public class CitadelBlockListener extends CitadelListener {
	
	public CitadelBlockListener(CitadelManager cm, ReinforcementManager rm, BastionManager bm) {
		super(cm, rm, bm);
	}
	
	public static String getReinforcementHealth(Player player, Reinforcement rein) {
		ReinforcementBlueprint rb = rein.getBlueprint();
		ChatColor healthPC = NumberUtils.getPrimaryColor(rein.getHealthAsPercentage());
		ChatColor healthSC = NumberUtils.getSecondaryColor(rein.getHealthAsPercentage());
		double health = rein.getHealth();
		double maxHealth = rb.getMaxHealth();
		String healthStr = df.format(rein.getHealthAsPercentage() * 100) + "%";
		return healthPC + healthStr + healthSC + " (" + healthPC + df.format(health) + healthSC + "/" + healthPC + maxHealth + healthSC + ")";
	}
	
	private String getReinforcementMaturity(Player player, Reinforcement rein) {
		ChatColor progressPC = NumberUtils.getPrimaryColor(rein.getProgress());
		ChatColor progressSC = NumberUtils.getSecondaryColor(rein.getProgress());
		double damage = rein.getDamage();
		String progressStr = df.format(rein.getProgress() * 100) + "%";
		return progressPC + progressStr + " Maturity " + progressSC + "(" + progressPC + df.format(damage) + progressSC + ")";
	}
	
	private String getAcidMaturity(Player player, Reinforcement rein) {
		ChatColor progressPC = NumberUtils.getPrimaryColor(rein.getAcidProgress());
		ChatColor progressSC = NumberUtils.getSecondaryColor(rein.getAcidProgress());
		long timeRemaining = rein.getAcidTimeRemaining();
		String progressStr = df.format(rein.getAcidProgress() * 100) + "%";
		return progressPC + progressStr + " Acid " + progressSC + (timeRemaining > 1000 ? "(" + progressPC + MathTools.formatDuration(timeRemaining, TimeUnit.MILLISECONDS) + progressSC + ")" : "");
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player); 
		Block b = e.getBlock();
		
		Set<Bastion> bastions = bm.getBastions(b.getLocation());
		if (!bastions.isEmpty()) {
			for (Bastion bastion : bastions) {
				Reinforcement rein = bastion.getReinforcement();
				Group group = rein.getGroup();
				if (!group.isAuthorized(player, GroupPermissions.BASTION_PLACE)) {
					player.sendMessage(ChatColor.RED + "Bastion prevents block place. " + ChatColor.DARK_RED + "| " + ChatColor.RESET + getReinforcementHealth(player, rein));
					e.setCancelled(true);
					rein.damage(player);
					return;
				}
			}
		}
		
		if (rm.placeBlock(cp, b, e.getItemInHand())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block b = e.getBlock();
		e.setCancelled(rm.breakBlock(cp, b));
	}
	
	private Cooldown<Player> lastReinforceInfo = new Cooldown<>();
	private Cooldown<Player> lastBastionInfo = new Cooldown<>();
	
	@EventHandler
	public void reinforceInfo(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = e.getPlayer();
		
		if (lastReinforceInfo.isOnCooldown(player)) {
			return;
		}
		
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}
		
		if (!cp.isReinforcementInfo()) {
			return;
		}
		
		Reinforcement reinforcement = rm.getReinforcement(clickedBlock);
		if (reinforcement == null) {
			return;
		}
		
		ReinforcementBlueprint rb = reinforcement.getBlueprint();
		Group group = reinforcement.getGroup();
		boolean apartOf = group.isMember(player);
		ChatColor grpColor = apartOf ? ChatColor.GREEN : ChatColor.RED;
		String grpName = apartOf ? group.getName() : "Unknown";
		String message = rb.getDisplayName() + ChatColor.YELLOW + " | "
				+ getReinforcementHealth(player, reinforcement) + ChatColor.YELLOW + " | "
				+ getReinforcementMaturity(player, reinforcement) + ChatColor.YELLOW + " | ";
		ReinforcementDataSource dataSource = rm.getReinforcementWorld(player.getWorld()).getDataSource(); 
		if (dataSource != null && dataSource.isAcidBlock(clickedBlock.getType())) {
			message += getAcidMaturity(player, reinforcement) + ChatColor.YELLOW + " | ";
		} else if (clickedBlock.getType() == Material.GOLD_BLOCK) {
			message += getAcidMaturity(player, reinforcement) + ChatColor.YELLOW + " | ";
		}
		message += ChatColor.GOLD + "Group: " + grpColor + grpName;
		
		player.sendMessage(message);
		lastReinforceInfo.putOnCooldown(player, 1000L);
	}
	
	@EventHandler
	public void reinforceBlock(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (!PlayerTools.isLeftClick(e.getAction())) {
			return;
		}
		
		Player player = e.getPlayer();
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}
		
		ReinforcementMode reinMode = cp.getReinforcementMode();
		if (reinMode == null) {
			return;
		}
		
		if (reinMode.getMethod() != ReinforceMethod.REINFORCE) {
			return;
		}
		
		rm.reinforceBlock(cp, clickedBlock, player.getInventory().getItemInMainHand());
	}
	
	@EventHandler
	public void bastionInfo(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (!PlayerTools.isRightClick(e.getAction())) {
			return;
		}
		
		Player player = e.getPlayer();
		
		if (lastBastionInfo.isOnCooldown(player)) {
			return;
		}
		
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}
		
		if (!cp.isBastionInfo()) {
			return;
		}
		
		Set<Bastion> bastions = bm.getBastions(clickedBlock.getRelative(e.getBlockFace()).getLocation());
		if (bastions.isEmpty()) {
			return;
		}
		
		Bastion bastion = bastions.iterator().next();
		Reinforcement rein = bastion.getReinforcement();
		Group group = rein.getGroup();
		if (group.isMember(player)) {
			player.sendMessage(ChatColor.GREEN + "Friendly bastion" + ChatColor.YELLOW + " | "
					+ getReinforcementMaturity(player, rein) + ChatColor.YELLOW + " | "
					+ getReinforcementHealth(player, rein) + ChatColor.YELLOW + " | "
					+ ChatColor.GOLD + "Group: " + ChatColor.GREEN + group.getName());
		} else {
			player.sendMessage(ChatColor.RED + "Unfriendly bastion.");
		}
		lastBastionInfo.putOnCooldown(player, 1000L);
	}
	
	@EventHandler
	public void blockBurn(BlockBurnEvent e) {
		Reinforcement rein = rm.getReinforcement(e.getBlock());
		if (rein == null) {
			return;
		}
		
		e.setCancelled(true);
		Block block = e.getBlock();
		// Basic essential fire protection
		if (block.getRelative(0, 1, 0).getType() == Material.FIRE) {
			block.getRelative(0, 1, 0).setType(Material.AIR);
		} // Essential
			// Extended fire protection (recommend)
		if (block.getRelative(1, 0, 0).getType() == Material.FIRE) {
			block.getRelative(1, 0, 0).setType(Material.AIR);
		}
		if (block.getRelative(-1, 0, 0).getType() == Material.FIRE) {
			block.getRelative(-1, 0, 0).setType(Material.AIR);
		}
		if (block.getRelative(0, -1, 0).getType() == Material.FIRE) {
			block.getRelative(0, -1, 0).setType(Material.AIR);
		}
		if (block.getRelative(0, 0, 1).getType() == Material.FIRE) {
			block.getRelative(0, 0, 1).setType(Material.AIR);
		}
		if (block.getRelative(0, 0, -1).getType() == Material.FIRE) {
			block.getRelative(0, 0, -1).setType(Material.AIR);
		}
	}
	
	@EventHandler
	public void blockPhysEvent(BlockPhysicsEvent e) {
		if (!e.getBlock().getType().hasGravity()) {
			return;
		}
		
		Reinforcement rein = rm.getReinforcement(e.getBlock());
		e.setCancelled(rein != null);
	}
	
	@EventHandler
	public void comparatorPlaceCheck(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() != Material.REDSTONE_COMPARATOR) {
			return;
		}
		Comparator comparator = (Comparator) event.getBlockPlaced().getState().getData();
		Block block = event.getBlockPlaced().getRelative(comparator.getFacing().getOppositeFace());
		// Check if the comparator is placed against something with an inventory
		if (ReinforcementManager.isPreventingBlockAccess(event.getPlayer(), block)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can not place this because it'd allow bypassing a nearby reinforcement");
			return;
		}
		// Comparators can also read through a single opaque block
		if (block.getType().isOccluding()) {
			if (ReinforcementManager.isPreventingBlockAccess(event.getPlayer(),
					block.getRelative(comparator.getFacing().getOppositeFace()))) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can not place this because it'd allow bypassing a nearby reinforcement");
				return;
			}
		}
	}
	
	@EventHandler
	public void liquidDumpEvent(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		if (block.getType().equals(Material.AIR) || block.getType().isSolid()) {
			return;
		}
		Reinforcement rein = rm.getReinforcement(block);
		event.setCancelled(rein != null);
	}
	
	@EventHandler
	public void onBlockFromToEvent(BlockFromToEvent event) {
		// prevent water/lava from spilling reinforced blocks away
		Reinforcement rein = rm.getReinforcement(event.getToBlock());
		event.setCancelled(rein != null);
	}
	
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		for (BlockState block_state : event.getBlocks()) {
			if (rm.getReinforcement(block_state.getBlock()) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void openContainer(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (PlayerTools.isLeftClick(e.getAction())) {
			return;
		}
		if (!e.hasBlock()) {
			return;
		}
		Reinforcement rein = rm.getReinforcement(e.getClickedBlock());
		if (rein == null) {
			return;
		}
		if (e.getClickedBlock().getState() instanceof Container) {
			if (!rein.hasPermission(e.getPlayer(), GroupPermissions.REINFORCEMENT_CONTAINER_BYPASS)) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.GOLD + e.getClickedBlock().getType().name() + ChatColor.RED +
						" is locked with " + rein.getBlueprint().getDisplayName());
			}
			return;
		}
		if (e.getClickedBlock().getState().getData() instanceof Openable) {
			if (!rein.hasPermission(e.getPlayer(), GroupPermissions.REINFORCEMENT_OPENABLE_BYPASS)) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.GOLD + e.getClickedBlock().getType().name() + ChatColor.RED +
						" is locked with " + rein.getBlueprint().getDisplayName());
			}
		}
	}
	
	@EventHandler
	public void preventBypassChestAccess(BlockPlaceEvent e) {
		Material mat = e.getBlock().getType();
		if (mat != Material.CHEST && mat != Material.TRAPPED_CHEST) {
			return;
		}
		for (BlockFace face : LocationTools.PLANAR_SIDES) {
			Block rel = e.getBlock().getRelative(face);
			if (rel != null && rel.getType() == mat) {
				if (ReinforcementManager.isPreventingBlockAccess(e.getPlayer(), rel)) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You can not place this because it'd allow bypassing a nearby reinforcement");
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onRedstone(BlockRedstoneEvent e) {
		if (e.getNewCurrent() <= 0 || e.getOldCurrent() > 0) return;
		
		Block b = e.getBlock();
		MaterialData md = b.getState().getData();
		
		if (md instanceof Openable) {
			Openable openable = (Openable) md;
			if (openable.isOpen()) {
				return;
			}
			
			Reinforcement rein = rm.getReinforcement(b);
			if (rein != null) {
				Collection<LivingEntity> livingEntities = LocationTools.getNearbyLivingEntities(b.getLocation(), 3);
				boolean allow = false;
				for (LivingEntity le : livingEntities) {
					if (!(le instanceof Player)) continue;
					Player player = (Player) le;
					if (rein.getGroup().isAuthorized(player, GroupPermissions.REINFORCEMENT_OPENABLE_BYPASS)) {
						allow = true;
						break;
					}
				}
				if (!allow) {
					e.setNewCurrent(e.getOldCurrent());
				}
			}
		}
	}
	
}
