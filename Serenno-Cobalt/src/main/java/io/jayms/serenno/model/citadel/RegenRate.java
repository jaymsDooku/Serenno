package io.jayms.serenno.model.citadel;

public class RegenRate {

	private double amount;
	private long interval;
	
	public RegenRate(double amount, long interval) {
		this.amount = amount;
		this.interval = interval;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public long getInterval() {
		return interval;
	}
	
	@Override
	public String toString() {
		return "RegenRate[Amount=" + amount + ",Interval=" + interval + "]";
	}
	
}
