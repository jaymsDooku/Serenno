package io.jayms.serenno.model.finance.company;

import java.util.Set;

import org.bukkit.entity.Player;

public interface Employee {

	Company getCompany();
	
	Player getPlayer();
	
	EmployeePosition getPosition();
	
	boolean hasPermission(String perm);
	
	Set<String> getPermissions();
	
}
