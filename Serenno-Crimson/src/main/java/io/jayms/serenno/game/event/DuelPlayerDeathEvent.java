package io.jayms.serenno.game.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import io.jayms.serenno.game.Duel;
import io.jayms.serenno.player.SerennoPlayer;

public class DuelPlayerDeathEvent extends DuelEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private SerennoPlayer dead;
	private EntityDamageEvent event;
	
	public DuelPlayerDeathEvent(Duel duel, SerennoPlayer dead, EntityDamageEvent event) {
		super(duel);
		this.dead = dead;
		this.event = event;
	}
	
	public SerennoPlayer getDead() {
		return dead;
	}

	public EntityDamageEvent getEvent() {
		return event;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
