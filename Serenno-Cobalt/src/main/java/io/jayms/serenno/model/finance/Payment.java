package io.jayms.serenno.model.finance;

public interface Payment extends Transaction {

	Account getSender();
	
	Account getReceiver();
	
	Money getMoney();
	
}
