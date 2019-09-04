package io.jayms.serenno.game.event;

import org.bukkit.event.HandlerList;

import io.jayms.serenno.game.Duel;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.player.SerennoPlayer;

public class DuelPlayerKitLoadEvent extends DuelEvent {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Kit kit;
	
	public DuelPlayerKitLoadEvent(Duel duel, SerennoPlayer player, Kit kit) {
		super(duel);
		this.kit = kit;
	}
	
	public void setKit(Kit kit) {
		this.kit = kit;
	}
	
	public Kit getKit() {
		return kit;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
