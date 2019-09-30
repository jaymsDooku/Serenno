package io.jayms.serenno.listener.citadel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class CitadelEntityListener extends CitadelListener {
	
	public CitadelEntityListener(CitadelManager cm, ReinforcementManager rm, BastionManager bm) {
		super(cm, rm, bm);
	}
	
	// prevent zombies from breaking reinforced doors
	@EventHandler(ignoreCancelled = true)
	public void breakDoor(EntityBreakDoorEvent e) {
		Reinforcement rein = rm.getReinforcement(e.getBlock());
		e.setCancelled(rein != null);
	}

	@EventHandler(ignoreCancelled = true)
	public void changeBlock(EntityChangeBlockEvent ecbe) {
		Reinforcement rein = rm.getReinforcement(ecbe.getBlock());
		if (rein != null) {
			rein.damage(rein.getBlueprint().getDefaultDamage());
			if (!rein.isBroken()) {
				ecbe.setCancelled(true);
			}
		}
	}

	// apply explosion damage to reinforcements
	@EventHandler(ignoreCancelled = true)
	public void explode(EntityExplodeEvent eee) {
		Iterator<Block> iterator = eee.blockList().iterator();
		// we can edit the result by removing blocks from the list
		while (iterator.hasNext()) {
			Block block = iterator.next();
			Reinforcement rein = rm.getReinforcement(block);
			rein.damage(rein.getBlueprint().getDefaultDamage());
			if (!rein.isBroken()) {
				iterator.remove();
			}
		}
	}
	
	// prevent creating golems from reinforced blocks
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawn(CreatureSpawnEvent cse) {
		EntityType type = cse.getEntityType();
		if (type != EntityType.IRON_GOLEM && type != EntityType.SNOWMAN && type != EntityType.WITHER
				&& type != EntityType.SILVERFISH) {
			return;
		}
		for (Block block : getGolemBlocks(type, cse.getLocation().getBlock())) {
			Reinforcement reinforcement = rm.getReinforcement(block);
			if (reinforcement != null) {
				cse.setCancelled(true);
			}
		}
	}
	
	private List<Block> getGolemBlocks(EntityType type, Block base) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(base);
		base = base.getRelative(BlockFace.UP);
		blocks.add(base);
		if (type == EntityType.IRON_GOLEM) {
			for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
					BlockFace.WEST }) {
				Block arm = base.getRelative(face);
				if (arm.getType() == Material.IRON_BLOCK)
					blocks.add(arm);
			}
		}
		base = base.getRelative(BlockFace.UP);
		blocks.add(base);
		return blocks;
	}
}
