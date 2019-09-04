package io.jayms.serenno.model.citadel;

public class RegenRate {

	private int amount;
	private long interval;
	
	public RegenRate(int amount, long interval) {
		this.amount = amount;
		this.interval = interval;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public long getInterval() {
		return interval;
	}
	
}
