package io.jayms.serenno.model.finance;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;

public class FinancialPlayer implements FinancialEntity {

	private final Player bukkitPlayer;
	
	public FinancialPlayer(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
	}
	
	@Override
	public UUID getID() {
		return bukkitPlayer.getUniqueId();
	}
	
	@Override
	public String getName() {
		return bukkitPlayer.getName();
	}
	
	@Override
	public String getDisplayName() {
		return bukkitPlayer.getDisplayName();
	}
	
	@Override
	public void sendMessage(String message) {
		bukkitPlayer.sendMessage(message);
	}
	
	@Override
	public boolean isServer() {
		return false;
	}
	
	public Player getBukkitPlayer() {
		return bukkitPlayer;
	}
	
	@Override
	public Set<Account> getAccounts() {
		return SerennoCobalt.get().getFinanceManager().getAccounts(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FinancialPlayer)) {
			return false;
		}
		
		FinancialPlayer fp = (FinancialPlayer) obj;
		return getID().equals(fp.getID());
	}
	
	@Override
	public int hashCode() {
		return getID().hashCode();
	}
	
}
