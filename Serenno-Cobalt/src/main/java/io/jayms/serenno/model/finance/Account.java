package io.jayms.serenno.model.finance;

import io.jayms.serenno.model.finance.company.Bank;
import net.md_5.bungee.api.ChatColor;

public class Account {

	private String id;
	private FinancialEntity owner;
	private Bank bank;
	private Currency currency;
	private double amount = 0;
	private boolean frozen = false;
	
	public Account(String id, FinancialEntity owner, Bank bank, Currency currency) {
		this.id = id;
		this.owner = owner;
		this.bank = bank;
		this.currency = currency;
	}
	
	public Bank getBank() {
		return bank;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public void add(double a) {
		this.amount += a;
	}
	
	public void subtract(double a) {
		this.amount -= a;
	}
	
	public void deposit(FinancialEntity entity, Account account, double a) {
		if (account.isFrozen()) {
			entity.sendMessage(ChatColor.RED + "Failed to withdraw from frozen account: " + ChatColor.WHITE + id);
			return;
		}
		
		if (isFrozen()) {
			owner.sendMessage(entity.getName() + ChatColor.RED + " tried to deposit to " + ChatColor.WHITE + id + ChatColor.RED + " but it's frozen.");
			entity.sendMessage(ChatColor.RED + "Failed to deposit to frozen account: " + ChatColor.WHITE + id);
			return;
		}
	}
	
	public void withdraw(FinancialEntity entity, Account account, double a) {
		if (account.isFrozen()) {
			entity.sendMessage(ChatColor.RED + "Failed to withdraw from frozen account: " + ChatColor.WHITE + id);
			return;
		}
		
		if (isFrozen()) {
			owner.sendMessage(entity.getName() + ChatColor.RED + " tried to deposit to " + ChatColor.WHITE + id + ChatColor.RED + " but it's frozen.");
			entity.sendMessage(ChatColor.RED + "Failed to deposit to frozen account: " + ChatColor.WHITE + id);
			return;
		}
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	
	public boolean isFrozen() {
		return frozen;
	}
}
