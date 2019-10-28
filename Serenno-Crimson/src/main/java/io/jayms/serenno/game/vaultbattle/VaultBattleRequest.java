package io.jayms.serenno.game.vaultbattle;

import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.Duelable;
import io.jayms.serenno.vault.VaultMap;
import net.md_5.bungee.api.ChatColor;

public class VaultBattleRequest extends DuelRequest {
	
	private ChatColor teamColor;

	public VaultBattleRequest(Duelable sender, DuelType duelType, VaultMap map, ChatColor teamColor) {
		super(sender, duelType, map);
		this.teamColor = teamColor;
	}
	
	public VaultMap getVaultMap() {
		return (VaultMap) this.getMap();
	}
	
	public ChatColor getTeamColor() {
		return teamColor;
	}

}
