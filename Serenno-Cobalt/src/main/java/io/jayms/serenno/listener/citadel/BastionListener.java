package io.jayms.serenno.listener.citadel;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.finance.FinancialEntity;

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
	
	@EventHandler
	public void onWaterFlow(BlockFromToEvent e) {
		Block toBlock = e.getToBlock();
		Block origin = e.getBlock();

		Set<FinancialEntity> owners = new HashSet<>();
		Set<Bastion> originBastions = bm.getBastions(origin.getLocation());
		Set<Bastion> toBastions = bm.getBastions(toBlock.getLocation());
		
		for (Bastion originBastion : originBastions) {
			
		}
	}

}
