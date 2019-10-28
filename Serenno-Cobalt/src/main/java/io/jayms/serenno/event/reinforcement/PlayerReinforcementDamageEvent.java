package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class PlayerReinforcementDamageEvent extends ReinforcementDamageEvent { 
	
	private Player damager;
	
	public PlayerReinforcementDamageEvent(Player damager, Reinforcement reinforcement, double damage) {
		super(reinforcement, damage);
		this.damager = damager;
	}
	
	public Player getDamager() {
		return damager;
	}
}
