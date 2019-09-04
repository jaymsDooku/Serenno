package io.jayms.serenno.model.finance.company;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.finance.Currency;

public class Treasury {

	private Currency currency;
	private List<ItemStack> inventory;
	
	public Treasury() {
	
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public List<ItemStack> getInventory() {
		return inventory;
	}
	
}
