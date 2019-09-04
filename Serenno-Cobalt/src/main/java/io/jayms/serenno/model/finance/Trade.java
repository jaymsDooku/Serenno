package io.jayms.serenno.model.finance;

public interface Trade {

	FinancialEntity getEntityOne();
	
	FinancialEntity getEntityTwo();
	
	Transaction getOneToTwo();
	
	Transaction getTwoToOne();
	
}
