package io.jayms.serenno.model.finance;

import org.bukkit.inventory.ItemStack;

public class Currency {

	private String name;
	private String symbol;
	private Peg peg;
	
	public Currency(String name, String symbol, Peg peg) {
		this.name = name;
		this.symbol = symbol;
		this.peg = peg;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public Peg getPeg() {
		return peg;
	}
	
	public static class Peg {
		
		private ItemStack item;
		private double amount;
		
		public Peg(ItemStack item, double amount) {
			this.item = item;
			this.amount = amount;
		}
		
		public ItemStack getItem() {
			return item;
		}
		
		public double getAmount() {
			return amount;
		}
	}
	
}
