package io.jayms.serenno.model.finance.company;

import java.util.Set;

public interface EmployeePosition {

	String getName();
	
	String getDisplayName();
	
	int getOrder();
	
	Set<String> getPermissions();
	
}
