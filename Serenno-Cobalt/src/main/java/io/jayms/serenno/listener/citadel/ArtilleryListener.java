package io.jayms.serenno.listener.citadel;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.github.maxopoly.finale.classes.engineer.event.WrenchUseEvent;

import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.manager.ArtilleryManager;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.util.PlayerTools;

public class ArtilleryListener implements Listener {

	private ArtilleryManager am;
	
	public ArtilleryListener(ArtilleryManager am) {
		this.am = am;
	}
	
	@EventHandler
	public void onWrenchUse(WrenchUseEvent e) {
		PlayerInteractEvent interact = e.getInteractEvent();
		if (interact.getHand() != EquipmentSlot.HAND) {
			return;
		}
		
		Block clicked = interact.getClickedBlock();
		if (clicked == null) {
			return;
		}
		
		EngineerPlayer engineer = e.getUser();
		ArtilleryCrate crate = am.getArtilleryCrate(clicked.getLocation());
		if (crate == null) {
			return;
		}
		
		PlayerInteractEvent interactEvent = e.getInteractEvent();
		if (PlayerTools.isRightClick(interactEvent.getAction())) {
			if (!crate.isAssembled()) {
				Map<String, Object> initData = new HashMap<>();
				initData.put("crate", crate);
				crate.getInterface().open(engineer.getBukkitPlayer(), initData);
				return;
			}
			
			Artillery artillery = crate.getArtillery();
			artillery.fire(e.getUser());
		} else if (PlayerTools.isLeftClick(interactEvent.getAction())) {
			if (!crate.isAssembled()) {
				return;
			}
			
			Artillery artillery = crate.getArtillery();
			Map<String, Object> initData = new HashMap<>();
			initData.put("artillery", artillery);
			artillery.getInterface().open(engineer.getBukkitPlayer(), initData);
		}
	}
	
	@EventHandler
	public void onReinforcementDestroy(ReinforcementDestroyEvent e) {
		ArtilleryCrate crate = am.getArtilleryCrate(e.getReinforcement().getLocation());
		if (crate == null) {
			return;
		}
		
		Artillery artillery = crate.getArtillery();
		if (artillery.isAssembled()) {
			artillery.disassemble();
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		Artillery artillery = am.getArtillery(block.getLocation());
		if (artillery == null) {
			return;
		}
		
		artillery.dealBlockDamage(player);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		Artillery artillery = am.getArtillery(block.getLocation());
		if (artillery == null) {
			return;
		}
		
		artillery.dealBlockDamage(player);
	}

}
