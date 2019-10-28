package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;

public class PlayerReinforcementCreationEvent extends ReinforcementCreationEvent {
	
	private Player placer;
	private ItemStack itemPlaced;
	
	public PlayerReinforcementCreationEvent(Player placer, ItemStack itemPlaced, Reinforcement reinforcement, ReinforcementDataSource dataSource) {
		super(reinforcement, dataSource);
		this.placer = placer;
		this.itemPlaced = itemPlaced;
	}
	
	public Player getPlacer() {
		return placer;
	}
	
	public ItemStack getItemPlaced() {
		return itemPlaced;
	}

}