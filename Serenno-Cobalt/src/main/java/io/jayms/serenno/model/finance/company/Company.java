package io.jayms.serenno.model.finance.company;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.finance.Account;
import io.jayms.serenno.model.finance.FinancialEntity;

public abstract class Company implements FinancialEntity {

	private UUID id;
	private String name;
	private String displayName;
	private UUID founder;
	private long foundingTime;
	
	private Set<Employee> employees = new HashSet<>();

	public Company(String name, UUID founder) {
		this(UUID.randomUUID(), name, founder, System.currentTimeMillis());
	}
	
	public Company(UUID id, String name, UUID founder, long foundingTime) {
		this.id = id;
		this.name = name;
		this.founder = founder;
		this.foundingTime = foundingTime;
	}
	
	@Override
	public UUID getID() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	public UUID getFounder() {
		return founder;
	}
	
	public long getFoundingTime() {
		return foundingTime;
	}
	
	public Set<Employee> getEmployees() {
		return employees;
	}
	
	@Override
	public Set<Account> getAccounts() {
		return SerennoCobalt.get().getFinanceManager().getAccounts(this);
	}
}
