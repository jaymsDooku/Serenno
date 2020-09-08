package io.jayms.serenno.listener.citadel;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.github.maxopoly.finale.classes.engineer.event.WrenchUseEvent;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.manager.ArtilleryManager;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetCrate;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetCrateItem;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
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
		
		Reinforcement reinforcement = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcement(clicked.getLocation().getBlock());
		if (reinforcement == null) {
			return;
		}
		EngineerPlayer engineer = e.getUser();
		ArtilleryCrate crate = am.getArtilleryCrate(reinforcement);
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
	public void onReinforcementCreate(PlayerReinforcementCreationEvent e) {
		ItemStack itemPlaced = e.getItemPlaced();
		CustomItem customItem = CustomItemManager.getCustomItemManager().getCustomItem(itemPlaced);
		if (!(customItem instanceof TrebuchetCrateItem)) {
			return;
		}
		TrebuchetCrateItem crateItem = (TrebuchetCrateItem) customItem;
		TrebuchetCrate crate = new TrebuchetCrate(crateItem);
		Reinforcement reinforcement = e.getReinforcement();
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().placeArtilleryCrate(e.getPlacer(), crate, reinforcement);
	}
	
	@EventHandler
	public void onReinforcementDestroy(ReinforcementDestroyEvent e) {
		ArtilleryCrate crate = am.getArtilleryCrate(e.getReinforcement());
		if (crate == null) {
			return;
		}
		
		PlayerReinforcementDestroyEvent playerE = (e instanceof PlayerReinforcementDestroyEvent) ? (PlayerReinforcementDestroyEvent) e : null;
		am.breakArtilleryCrate((playerE != null) ? playerE.getDestroyer() : null, crate);
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
		
		e.setCancelled(artillery.dealBlockDamage(player));
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		Artillery artillery = am.getArtillery(block.getLocation());
		if (artillery == null) {
			return;
		}

		e.setCancelled(artillery.dealBlockDamage(player));
	}

}
