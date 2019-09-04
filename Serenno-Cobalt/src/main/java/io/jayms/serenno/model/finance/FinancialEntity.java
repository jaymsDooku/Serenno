package io.jayms.serenno.model.finance;

import java.util.Set;
import java.util.UUID;

public interface FinancialEntity {

	UUID getID();
	
	String getName();
	
	String getDisplayName();
	
	void sendMessage(String message);
	
	boolean isServer();
	
	Set<Account> getAccounts();
	
}
