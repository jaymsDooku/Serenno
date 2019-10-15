package io.jayms.serenno.model.finance.company;

import java.util.UUID;

public class ServerCompany extends Company {

	public ServerCompany(String name, UUID serverID) {
		super(name, serverID);
	}
	
	@Override
	public void sendMessage(String message) {
	}
	
	@Override
	public boolean isServer() {
		return true;
	}

}
