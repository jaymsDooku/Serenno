package io.jayms.serenno.model.finance.company;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import io.jayms.serenno.model.finance.Account;
import io.jayms.serenno.model.finance.FinancialEntity;

public abstract class Bank extends Company {
	
	private Multimap<UUID, Account> bankAccounts;
	
	public Bank(String name, Player owner, long foundingTime) {
		this(UUID.randomUUID(), name, owner, foundingTime, HashMultimap.create());
	}
	
	public Bank(UUID id, String name, Player owner, long foundingTime, Multimap<UUID, Account> bankAccounts) {
		super(id, name, owner.getUniqueId(), foundingTime);
		this.bankAccounts = bankAccounts;
	}
	
	public Set<Account> getAccounts(FinancialEntity financeEntity) {
		return Sets.newHashSet(bankAccounts.get(financeEntity.getID()));
	}
	
	public Multimap<UUID, Account> getBankAccounts() {
		return bankAccounts;
	}

}
