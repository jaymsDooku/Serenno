package io.jayms.serenno.command;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.vault.data.mongodb.MongoVaultMapBastionBlueprintDataSource;
import io.jayms.serenno.vault.data.mongodb.MongoVaultMapReinforcementBlueprintDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.example.MappedFaweQueue;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.util.EditSessionBuilder;
import com.google.common.collect.Sets;
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
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.ReinforcementMode;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.MaterialTools;
import io.jayms.serenno.util.MaterialTools.MaterialNumbers;
import io.jayms.serenno.util.PlayerTools;
import io.jayms.serenno.util.PlayerTools.Clipboard;
import io.jayms.serenno.util.worldedit.Bastionizer;
import io.jayms.serenno.util.worldedit.Reinforcer;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapDatabase;
import io.jayms.serenno.vault.VaultMapManager;
import io.jayms.serenno.vault.VaultMapPlayerListType;
import net.md_5.bungee.api.ChatColor;
import sun.jvm.hotspot.code.LocationValue;

@CommandAlias("vault")
public class VaultCommand extends BaseCommand {

	private VaultMapManager vm = SerennoCrimson.get().getVaultMapManager();
	
	@Subcommand("create")
	public void create(Player player, String vaultName, int radius) {
		if (vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "A vault map with that name already exists.");
			return;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				VaultMap vaultMap = vm.createVault(SerennoCrimson.get().getPlayerManager().get(player), vaultName, radius);
				if (vaultMap == null) {
					return;
				}
				player.sendMessage(ChatColor.YELLOW + "You have created a new vault map: " + vaultMap.getArena().getRegion().getDisplayName());
			}
			
		}.runTask(SerennoCrimson.get());
	}
	
	@Subcommand("info")
	public void info(Player player, String vaultName) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapDatabase database = vaultMap.getDatabase();
		player.sendMessage(ChatColor.GOLD + vaultMap.getArena().getRegion().getName() + ChatColor.YELLOW + "'s region information");
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------");
		player.sendMessage(ChatColor.GOLD + "Cores: " + ChatColor.YELLOW + database.getCoreSource().getAll());
		player.sendMessage(ChatColor.GOLD + "Group Colours: " + ChatColor.YELLOW + database.getGroupColours());
	}
	
	@Subcommand("blueprints reset")
	public void resetBlueprints(Player player, String vaultName) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapDatabase database = vaultMap.getDatabase();
		MongoVaultMapReinforcementBlueprintDataSource reinSource = database.getReinforcementBlueprintSource();
		MongoVaultMapBastionBlueprintDataSource bastionSource = database.getBastionBlueprintSource();
		reinSource.deleteAll();
		bastionSource.deleteAll();
		ReinforcementBlueprint stoneBlueprint = ReinforcementBlueprint.builder()
				.name("stone")
				.displayName(ChatColor.GRAY + "Stone")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 5)
				.damageCooldown(0)
				.maturationTime(1000 * 60 * 1)
				.maturationScale(2)
				.itemStack(new ItemStackBuilder(Material.STONE, 1)
						.meta(new ItemMetaBuilder()
								.name("Stone")).build())
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(50)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint ironBlueprint = ReinforcementBlueprint.builder()
				.name("iron")
				.displayName(ChatColor.DARK_GRAY + "Iron")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 9)
				.damageCooldown(0)
				.itemStack(new ItemStackBuilder(Material.IRON_INGOT, 1)
						.meta(new ItemMetaBuilder()
								.name("Iron Ingot")).build())
				.maturationTime(1000 * 60 * 3)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(300)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint diamondBlueprint = ReinforcementBlueprint.builder()
				.name("diamond")
				.displayName(ChatColor.AQUA + "Diamond")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 27)
				.damageCooldown(0)
				.itemStack(new ItemStackBuilder(Material.DIAMOND, 1)
						.meta(new ItemMetaBuilder()
								.name("Diamond")).build())
				.maturationTime(1000 * 60 * 9)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(2000)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint diamondBastionBlueprint = ReinforcementBlueprint.builder()
				.name("diamond-bastion")
				.displayName(ChatColor.AQUA + "Diamond Bastion")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 30)
				.damageCooldown(2500)
				.maturationTime(1000 * 60 * 30)
				.maturationScale(2)
				.itemStack(new ItemStackBuilder(Material.DIAMOND, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.AQUA + "Diamond Bastion")).build())
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(2000)
				.reinforceableMaterials(Arrays.asList(Material.SPONGE))
				.build();
		
		BastionBlueprint vaultBlueprint = BastionBlueprint.builder()
				.name("vault")
				.displayName(ChatColor.DARK_RED + "Vault Bastion")
				.itemStack(new ItemStackBuilder(Material.SPONGE, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.DARK_RED + "Vault Bastion"))
						.build())
				.pearlConfig(PearlConfig.builder()
						.consumeOnBlock(false)
						.block(true)
						.blockMidAir(false)
						.damage(2)
						.build())
				.requiresMaturity(false)
				.shape(BastionShape.SQUARE)
				.radius(10)
				.build();
		
		reinSource.create(stoneBlueprint);
		reinSource.create(ironBlueprint);
		reinSource.create(diamondBlueprint);
		reinSource.create(diamondBastionBlueprint);
		
		bastionSource.create(vaultBlueprint);
		player.sendMessage(ChatColor.GOLD + vaultMap.getName() + ChatColor.YELLOW + " vault blueprints reset.");
	}
	
	@Subcommand("goto")
	public void gotoVaultMap(Player player, String vaultName) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		if (!vaultMap.isReady()) {
			player.sendMessage(ChatColor.RED + "That vault map is not ready yet.");
			return;
		}

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
		List<UUID> playerList = vaultMap.getDatabase().getPlayerList();
		if (playerList.contains(toAddID)) {
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
		List<UUID> playerList = vaultMap.getDatabase().getPlayerList();
		if (playerList.contains(toRemoveID)) {
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
		
		VaultMapPlayerListType playerListType = VaultMapPlayerListType.valueOf(type.toUpperCase());
		if (playerListType == null) {
			player.sendMessage(ChatColor.RED + "That isn't a valid player list type.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.setPlayerListType(playerListType);
		player.sendMessage(ChatColor.YELLOW + "You have set vault map's player list type to: " + ChatColor.GOLD + playerListType);
	}
	
	@Subcommand("groupcolour")
	public void groupColour(Player player, String vaultName, String groupName, String teamColour) {
		if (!vm.isVaultMap(vaultName)) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}
		
		ChatColor colour = ChatColor.valueOf(teamColour.toUpperCase());
		if (colour == null) {
			player.sendMessage(ChatColor.RED + "That isn't a valid colour.");
			return;
		}
		
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		VaultMapDatabase database = vaultMap.getDatabase();
		Group group = database.getGroupSource().get(groupName.toLowerCase());
		if (group == null) {
			player.sendMessage(ChatColor.RED + "That isn't a valid group.");
			return;
		}
		database.setGroupColour(colour, groupName.toLowerCase());
		player.sendMessage(ChatColor.YELLOW + "You have set vault map's " + colour + group.getName() + ChatColor.YELLOW + " group colour to: " + colour + teamColour);
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
	
	@Subcommand("r")
	public void reinforce(Player player, String vaultName, String material) {
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
		
		CitadelPlayer cp = SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player);
		ReinforcementMode reinMode = cp.getReinforcementMode();
		if (reinMode == null) {
			player.sendMessage(ChatColor.RED + "You need to have a reinforcement selected.");
			return;
		}
		ReinforcementBlueprint blueprint = reinMode.getReinforcementBlueprint();
		Group group = reinMode.getGroupToReinforce();
		
		Location p1 = cb.getP1();
		Location p2 = cb.getP2();
		if ((p1 == null || !vaultMap.getArena().getRegion().isInside(p1))
				|| (p2 == null || !vaultMap.getArena().getRegion().isInside(p2))) {
			player.sendMessage(ChatColor.RED + "Your selection must be within the bounds of the vault map.");
			return;
		}
		
		MaterialNumbers materialNumbers;
		try {
			materialNumbers = MaterialTools.getMaterialNumbers(material);
		} catch (IllegalArgumentException e) {
			player.sendMessage(ChatColor.RED + "Invalid new material data.");
			return;
		}
		Set<BaseBlock> replaceFilter = Sets.newHashSet(FaweCache.getBlock(materialNumbers.getId(), materialNumbers.getData()));
		
		FawePlayer<Player> fawePlayer = FawePlayer.wrap(player);
		Region region = fawePlayer.getSelection();
		
		EditSession editSession = new EditSessionBuilder(new BukkitWorld(p1.getWorld()))
				.player(FawePlayer.wrap(player))
				.build();
		
		ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
		Reinforcer reinforcer = new Reinforcer(player, rm.getReinforcementWorld(player.getWorld()), blueprint, group);
		FuzzyBlockMask mask = new FuzzyBlockMask(editSession, replaceFilter);
		RegionMaskingFilter filter = new RegionMaskingFilter(mask, reinforcer);
		RegionVisitor visitor = new RegionVisitor(region, filter,
				editSession.getQueue() instanceof MappedFaweQueue ? (MappedFaweQueue) editSession.getQueue() : null);
		Operations.completeBlindly(visitor);
		player.sendMessage(ChatColor.YELLOW + "You have reinforced: " + ChatColor.GOLD + visitor.getAffected() + ChatColor.YELLOW + " blocks");
	}
	
	@Subcommand("b")
	public void bastionize(Player player, String vaultName, String material, String bastionBlueprint) {
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
		
		BastionBlueprint bb = vaultMap.getDatabase().getBastionBlueprintSource().get(bastionBlueprint);
		if (bb == null) {
			player.sendMessage(ChatColor.RED + "That bastion blueprint doesn't exist.");
			return;
		}
		
		CitadelPlayer cp = SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player);
		ReinforcementMode reinMode = cp.getReinforcementMode();
		if (reinMode == null) {
			player.sendMessage(ChatColor.RED + "You need to have a reinforcement selected.");
			return;
		}
		ReinforcementBlueprint blueprint = reinMode.getReinforcementBlueprint();
		Group group = reinMode.getGroupToReinforce();
		
		Location p1 = cb.getP1();
		Location p2 = cb.getP2();
		if ((p1 == null || !vaultMap.getArena().getRegion().isInside(p1))
				|| (p2 == null || !vaultMap.getArena().getRegion().isInside(p2))) {
			player.sendMessage(ChatColor.RED + "Your selection must be within the bounds of the vault map.");
			return;
		}
		
		MaterialNumbers materialNumbers;
		try {
			materialNumbers = MaterialTools.getMaterialNumbers(material);
		} catch (IllegalArgumentException e) {
			player.sendMessage(ChatColor.RED + "Invalid new material data.");
			return;
		}
		Set<BaseBlock> replaceFilter = Sets.newHashSet(FaweCache.getBlock(materialNumbers.getId(), materialNumbers.getData()));
		
		FawePlayer<Player> fawePlayer = FawePlayer.wrap(player);
		Region region = fawePlayer.getSelection();
		
		EditSession editSession = new EditSessionBuilder(new BukkitWorld(p1.getWorld()))
				.player(FawePlayer.wrap(player))
				.build();
		
		BastionManager bm = SerennoCobalt.get().getCitadelManager().getBastionManager();
		Bastionizer bastionizer = new Bastionizer(player, bm.getBastionWorld(player.getWorld()), blueprint, bb, group);
		FuzzyBlockMask mask = new FuzzyBlockMask(editSession, replaceFilter);
		RegionMaskingFilter filter = new RegionMaskingFilter(mask, bastionizer);
		RegionVisitor visitor = new RegionVisitor(region, filter,
				editSession.getQueue() instanceof MappedFaweQueue ? (MappedFaweQueue) editSession.getQueue() : null);
		Operations.completeBlindly(visitor);
		player.sendMessage(ChatColor.YELLOW + "You have bastionized: " + ChatColor.GOLD + visitor.getAffected() + ChatColor.YELLOW + " blocks");
	}
	
	@Subcommand("rr")
	public void replace(Player player, String vaultName, String oldMaterials, String newMaterial) {
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
		
		CitadelPlayer cp = SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player);
		ReinforcementMode reinMode = cp.getReinforcementMode();
		if (reinMode == null) {
			player.sendMessage(ChatColor.RED + "You need to have a reinforcement selected.");
			return;
		}
		ReinforcementBlueprint blueprint = reinMode.getReinforcementBlueprint();
		Group group = reinMode.getGroupToReinforce();
		
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
		Reinforcer reinforcer = new Reinforcer(player, rm.getReinforcementWorld(player.getWorld()), blueprint, group);
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

	@Subcommand("update")
	public void update(Player player, String vaultName) {
		VaultMap vaultMap = vm.getVaultMap(vaultName);
		if (vaultMap == null) {
			player.sendMessage(ChatColor.RED + "That vault map doesn't exist.");
			return;
		}

		Set<Reinforcement> allReinforcements = vaultMap.getReinforcementWorld().getAllReinforcements();
		new BukkitRunnable() {

			int total = allReinforcements.size();
			Queue<Reinforcement> reinforcements = new ConcurrentLinkedQueue<>(allReinforcements);
			int batchSize = 30000;
			int repaired = 0;

			@Override
			public void run() {
				if (reinforcements.isEmpty()) {
					player.sendMessage(ChatColor.YELLOW + "Repaired " + repaired + "/" + total + " reinforcements");
					cancel();
					return;
				}

				for (int i = 0; i < batchSize; i++) {
					Reinforcement reinforcement = reinforcements.poll();
					if (reinforcement == null) continue;
					if (reinforcement.repair()) {
						repaired++;
					}
				}
			}

		}.runTaskTimer(SerennoCrimson.get(), 0L, 1L);
	}
	
}
