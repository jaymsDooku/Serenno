package io.jayms.serenno.model.finance;

public class Money {

	private Currency currency;
	private double amount;
	
	public Money(Currency currency, double amount) {
		this.currency = currency;
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
}
