package io.jayms.serenno.command;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapManager;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("vault")
public class VaultCommand extends BaseCommand {

	private VaultMapManager vm = SerennoCrimson.get().getVaultMapManager();
	
	@Subcommand("create")
	public void create(Player player, String vaultName, int radius) {
		if (vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "A vault map with that name already exists.");
			return;
		}
		
		VaultMap vaultMap = vm.createVault(SerennoCrimson.get().getPlayerManager().get(player), vaultName, radius);
		player.sendMessage(ChatColor.YELLOW + "You have created a new vault map: " + vaultMap.getArena().getRegion().getDisplayName());
	}
	
	@Subcommand("goto")
	public void gotoVaultMap(Player player, String vaultName) {
		
	}
	
	@Subcommand("set goto")
	public void setGotoVaultMap(Player player, String vaultName) {
		
	}
}
