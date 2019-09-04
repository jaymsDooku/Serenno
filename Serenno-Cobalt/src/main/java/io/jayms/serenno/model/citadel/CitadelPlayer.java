package io.jayms.serenno.model.citadel;

import org.bukkit.entity.Player;

import io.jayms.serenno.model.group.Group;

public class CitadelPlayer {

	private final Player bukkitPlayer;
	private Group defaultGroup;
	private ReinforcementMode reinforcementMode;
	private Group reinforcementBypass;
	private boolean	reinforcementInfo;
	private boolean bastionInfo;
	
	public CitadelPlayer(Player player) {
		this.bukkitPlayer = player;
	}
	
	public void setReinforcementInfo(boolean reinforcementInfo) {
		this.reinforcementInfo = reinforcementInfo;
	}
	
	public boolean isReinforcementInfo() {
		return reinforcementInfo;
	}
	
	public void setBastionInfo(boolean bastionInfo) {
		this.bastionInfo = bastionInfo;
	}
	
	public boolean isBastionInfo() {
		return bastionInfo;
	}
	
	public void setReinforcementMode(ReinforcementMode reinforcementMode) {
		this.reinforcementMode = reinforcementMode;
	}
	
	public ReinforcementMode getReinforcementMode() {
		return reinforcementMode;
	}
	
	public boolean isReinforcementFortification() {
		return reinforcementMode != null;
	}
	
	public void setReinforcementBypass(Group reinforcementBypass) {
		this.reinforcementBypass = reinforcementBypass;
	}
	
	public Group getReinforcementBypass() {
		return reinforcementBypass;
	}
	
	public boolean isReinforcementBypass() {
		return reinforcementBypass != null;
	}
	
	public void setDefaultGroup(Group defaultGroup) {
		this.defaultGroup = defaultGroup;
	}
	
	public Group getDefaultGroup() {
		return defaultGroup;
	}
	
	public Player getBukkitPlayer() {
		return bukkitPlayer;
	}
	
}
