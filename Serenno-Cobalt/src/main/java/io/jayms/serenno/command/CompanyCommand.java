package io.jayms.serenno.command;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.FinanceManager;
import io.jayms.serenno.model.finance.company.Company;
import io.jayms.serenno.model.finance.company.CompanyType;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("company")
public class CompanyCommand extends BaseCommand {

	private FinanceManager fm = SerennoCobalt.get().getFinanceManager();
	
	@Subcommand("create")
	public void create(Player player, String companyName, String companyType) {
		Company company = fm.createCompany(player, companyName, CompanyType.valueOf(companyType.toUpperCase()));
		player.sendMessage(ChatColor.YELLOW + "You have created a new company: " + ChatColor.GOLD + company.getName());
	}
	
}
