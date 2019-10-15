package io.jayms.serenno.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.model.finance.Account;
import io.jayms.serenno.model.finance.FinancialEntity;
import io.jayms.serenno.model.finance.FinancialPlayer;
import io.jayms.serenno.model.finance.company.Bank;
import io.jayms.serenno.model.finance.company.Company;
import io.jayms.serenno.model.finance.company.CompanyType;
import io.jayms.serenno.model.finance.company.ServerCompany;
import io.jayms.serenno.model.finance.company.SimpleCompany;

public class FinanceManager implements Listener {

	private UUID serverID = UUID.randomUUID();
	private Map<UUID, FinancialPlayer> players = Maps.newConcurrentMap();
	private Map<String, Company> companies = Maps.newConcurrentMap();
	
	public UUID getServerID() {
		return serverID;
	}
	
	public Company createCompany(Player creator, String name, CompanyType type) {
		if (companyExists(name)) {
			throw new IllegalArgumentException("Company with this name already exists.");
		}
		
		Company company = new SimpleCompany(name, creator);
		companies.put(name, company);
		return company;
	}
	
	public Company createServerCompany(String name) {
		if (companyExists(name)) {
			throw new IllegalArgumentException("Company with this name already exists.");
		}
		
		Company company = new ServerCompany(name, serverID);
		companies.put(name, company);
		return company;
	}
	
	public Company getCompany(String name) {
		return companies.get(name);
	}
	
	public boolean companyExists(String name) {
		return companies.containsKey(name.toLowerCase());
	}
	
	public FinancialPlayer getPlayer(Player player) {
		FinancialPlayer fp = players.get(player.getUniqueId());
		if (fp == null) {
			fp = load(player);
			players.put(player.getUniqueId(), fp);
		}
		return fp;
	}
	
	public FinancialPlayer load(Player player) {
		MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("finance_player");
        FindIterable<Document> query = collection.find(Filters.eq("uuid", player.getUniqueId().toString()));
        Document document = query.first();
        
        FinancialPlayer financialPlayer = new FinancialPlayer(player); 
        
        if (document != null) {
	        
        }
        
        return financialPlayer;
	}
	
	public Set<FinancialPlayer> getPlayers() {
		return Sets.newHashSet(players.values());
	}
	
	public Collection<Company> getCompanies() {
		return companies.values();
	}
	
	public Set<Bank> getBanks() {
		return getCompanies().stream().filter(c -> {
			return c instanceof Bank;
		}).map(c -> {
			return (Bank) c;
		}).collect(Collectors.toSet());
	}
	
	public Set<Account> getAccounts(FinancialEntity financeEntity) {
		Set<Account> accounts = new HashSet<>();
		for (Bank bank : getBanks()) {
			accounts.addAll(bank.getAccounts());
		}
		return accounts;
	}
	
}
