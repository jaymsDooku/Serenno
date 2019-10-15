package io.jayms.serenno.model.finance.company;

import org.bukkit.entity.Player;

public class SimpleCompany extends Company {

	public SimpleCompany(String name, Player founder) {
		super(name, founder.getUniqueId());
	}

	@Override
	public void sendMessage(String message) {
		getEmployees().stream().forEach(e -> {
			
		});
	}

	@Override
	public boolean isServer() {
		return false;
	}

}
