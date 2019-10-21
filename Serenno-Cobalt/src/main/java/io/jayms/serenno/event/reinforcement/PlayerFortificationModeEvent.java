package io.jayms.serenno.event.reinforcement;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;

public class PlayerFortificationModeEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	private Player player;
	private Group group;
	private String groupName;
	private ItemStack blueprintItem;
	private ReinforcementBlueprint blueprint;
	
	public PlayerFortificationModeEvent(Player player, Group group, String groupName, ItemStack blueprintItem, ReinforcementBlueprint blueprint) {
		this.player = player;
		this.group = group;
		this.groupName = groupName;
		this.blueprintItem = blueprintItem;
		this.blueprint = blueprint;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
	public void setBlueprint(ReinforcementBlueprint blueprint) {
		this.blueprint = blueprint;
	}
	
	public ItemStack getBlueprintItem() {
		return blueprintItem;
	}
	
	public ReinforcementBlueprint getBlueprint() {
		return blueprint;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public String getGroupName() {
		return groupName;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
