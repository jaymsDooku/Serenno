package io.jayms.serenno.command;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.MappedFaweQueue;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.mask.FuzzyBlockMask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.regions.Region;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.MaterialTools;
import io.jayms.serenno.util.MaterialTools.MaterialNumbers;
import io.jayms.serenno.util.PlayerTools;
import io.jayms.serenno.util.PlayerTools.Clipboard;
import io.jayms.serenno.util.worldedit.Reinforcer;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapDatabase;
import io.jayms.serenno.vault.VaultMapManager;
import io.jayms.serenno.vault.VaultMapPlayerList;
import io.jayms.serenno.vault.VaultMapPlayerListType;
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
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		vaultMap.gotoVaultMap(player);
	}
	
	@Subcommand("set goto")
	public void setGotoVaultMap(Player player, String vaultName) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		vaultMap.setGotoLocation(player.getLocation());
		player.sendMessage(ChatColor.YELLOW + "You have set go to location to: " + ChatColor.GOLD + player.getLocation());
	}
	
	@Subcommand("playerlist add")
	public void playerlistAdd(Player player, String vaultName, String toAdd) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		UUID toAddID = Bukkit.getPlayerUniqueId(toAdd);
		if (toAddID == null) {
			player.sendMessage(ChatColor.RED + "That player doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapPlayerList playerList = vaultMap.getDatabase().getPlayerList();
		if (playerList.inPlayerList(toAddID)) {
			player.sendMessage(ChatColor.RED + "That player is already in the player list.");
			return;
		}
		playerList.add(toAddID);
	}
	
	@Subcommand("playerlist remove")
	public void playerlistRemove(Player player, String vaultName, String toRemove) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		UUID toRemoveID = Bukkit.getPlayerUniqueId(toRemove);
		if (toRemoveID == null) {
			player.sendMessage(ChatColor.RED + "That player doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapPlayerList playerList = vaultMap.getDatabase().getPlayerList();
		if (playerList.inPlayerList(toRemoveID)) {
			player.sendMessage(ChatColor.RED + "That player is already in the player list.");
			return;
		}
		playerList.remove(toRemoveID);
	}
	
	@Subcommand("playerlist type")
	public void playerlistType(Player player, String vaultName, String type) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMapPlayerListType playerListType = VaultMapPlayerListType.valueOf(type);
		if (playerListType == null) {
			player.sendMessage(ChatColor.RED + "That isn't a valid player list type.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.setPlayerListType(playerListType);
		player.sendMessage(ChatColor.YELLOW + "You have set vault map's player list type to: " + ChatColor.GOLD + playerListType);
	}
	
	@Subcommand("list")
	public void list(Player player) {
		Collection<VaultMap> vaults = vm.listVaults();
		player.sendMessage(ChatColor.RED + "Vaults");
		player.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "-----------------");
		for (VaultMap vaultMap : vaults) {
			player.sendMessage(ChatColor.DARK_RED + "- " + ChatColor.RED + vaultMap.getArena().getRegion().getName());
		}
	}
	
	@Subcommand("save")
	public void save(Player player, String vaultName) {
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		vaultMap.save();
		player.sendMessage(ChatColor.YELLOW + "You have saved vault map: " + vaultMap.getArena().getRegion().getDisplayName());
	}
	
	@Subcommand("delete")
	public void delete(Player player, String vaultName) {
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		if (vaultMap == null) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		vm.deleteVaultMap(vaultMap);
		player.sendMessage(ChatColor.YELLOW + "You have deleted vault map: " + vaultMap.getArena().getRegion().getDisplayName());
	}
	
	@Subcommand("rr")
	public void replace(Player player, String vaultName, String reinBlueprint, String oldMaterials, String newMaterial) {
		Clipboard cb = PlayerTools.getClipboard(player);
		if (cb == null) {
			player.sendMessage(ChatColor.RED + "Select some points with world edit.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		if (vaultMap == null) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		Location p1 = cb.getP1();
		Location p2 = cb.getP2();
		if ((p1 == null || !vaultMap.getArena().getRegion().isInside(p1))
				|| (p2 == null || !vaultMap.getArena().getRegion().isInside(p2))) {
			player.sendMessage(ChatColor.RED + "Your selection must be within the bounds of the vault map.");
			return;
		}
		
		List<MaterialNumbers> oldMaterialNumbers = MaterialTools.getListOfMaterialNumbers(oldMaterials);
		Set<BaseBlock> replaceFilter = oldMaterialNumbers.stream()
				.map(m -> FaweCache.getBlock(m.getId(), m.getData()))
				.collect(Collectors.toSet());
		
		MaterialNumbers materialNumbers;
		try {
			materialNumbers = MaterialTools.getMaterialNumbers(newMaterial);
		} catch (IllegalArgumentException e) {
			player.sendMessage(ChatColor.RED + "Invalid new material data.");
			return;
		}
		
		FawePlayer<Player> fawePlayer = FawePlayer.wrap(player);
		Region region = fawePlayer.getSelection();
		
		EditSession editSession = new EditSessionBuilder(new BukkitWorld(p1.getWorld()))
				.player(FawePlayer.wrap(player))
				.build();
		
		ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
		Reinforcer reinforcer = new Reinforcer(player, rm.getReinforcementWorld(player.getWorld()),
				rm.getReinforcementBlueprint(reinBlueprint), SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player).getDefaultGroup());
		FuzzyBlockMask mask = new FuzzyBlockMask(editSession, replaceFilter);
		RegionMaskingFilter filter = new RegionMaskingFilter(mask, reinforcer);
		RegionVisitor visitor = new RegionVisitor(region, filter,
				editSession.getQueue() instanceof MappedFaweQueue ? (MappedFaweQueue) editSession.getQueue() : null);
		Operations.completeBlindly(visitor);
		player.sendMessage(ChatColor.YELLOW + "You have reinforced: " + ChatColor.GOLD + visitor.getAffected() + ChatColor.YELLOW + " blocks");
		
		BaseBlock replacement = FaweCache.getBlock(materialNumbers.getId(), materialNumbers.getData());
		
		editSession.replaceBlocks(region, replaceFilter, replacement);
		player.sendMessage(ChatColor.YELLOW + "You have replaced: [" + (replaceFilter.stream().map(b -> b.toString()).collect(Collectors.joining(", "))) + "] -> " + replacement.toString());
	}
	
}
