package io.jayms.serenno.command;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.ArenaManager;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.util.ItemUtil;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("arena")
public class ArenaCommand extends BaseCommand {

	private ArenaManager arenaManager = SerennoCrimson.get().getArenaManager();
	
	@Subcommand("create")
	public void create(Player player, String regionName) {
		Arena arena = arenaManager.createArena(player, regionName);
		player.sendMessage(ChatColor.YELLOW + "You have created a new arena: " + ChatColor.GOLD + arena.getName());
	}
	
	@Subcommand("delete")
	public void delete(Player player, String regionName) {
		arenaManager.deleteArena(arenaManager.getArena(regionName));
	}
	
	@Subcommand("info")
	public void info(CommandSender sender, String arenaName) {
		Arena arena = arenaManager.getArena(arenaName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		sender.sendMessage(ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + "'s arena information");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------");
		sender.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.YELLOW + arena.getDescription());
		sender.sendMessage(ChatColor.GOLD + "Creators: " + ChatColor.YELLOW + arena.getCreators());
		sender.sendMessage(ChatColor.GOLD + "Duel Types: " + ChatColor.GOLD + "[" + arena.getDuelTypes().stream().map(d -> d.getDisplayName()).collect(Collectors.joining(",")) + ChatColor.GOLD + "]");
		
		StringBuilder spawnsSB = new StringBuilder();
		spawnsSB.append(ChatColor.GOLD + "{");
		int i = 1;
		for (Entry<ChatColor, Location> spawnEn : arena.getSpawnPoints().entrySet()) {
			ChatColor teamColor = spawnEn.getKey();
			spawnsSB.append(teamColor + teamColor.getName());
			spawnsSB.append(ChatColor.GOLD + "=");
			spawnsSB.append(ChatColor.YELLOW + spawnEn.getValue().toString());
			if (i < arena.getSpawnPoints().size()) {
				spawnsSB.append(ChatColor.GOLD + ",");
			}
			i++;
		}
		spawnsSB.append(ChatColor.GOLD + "}");
		sender.sendMessage(ChatColor.GOLD + "Spawns: " + ChatColor.YELLOW + spawnsSB.toString());
	}
	
	@Subcommand("list")
	public void list(CommandSender sender) {
		List<Arena> arenas = arenaManager.listArenas();
		sender.sendMessage(ChatColor.YELLOW + "Arenas");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		arenas.stream().forEach(a -> {
			sender.sendMessage(ChatColor.YELLOW + a.getName());
		});
	}
	
	@Subcommand("save")
	public void save(CommandSender sender, String regionName) {
		Arena arena = arenaManager.getArena(regionName);
		arenaManager.saveArena(arena);
		sender.sendMessage(ChatColor.YELLOW + "You have saved arena: " + ChatColor.GOLD + arena.getName());
	}
	
	@Subcommand("creators")
	public void creators(CommandSender sender, String regionName, String creators) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		creators = ChatColor.translateAlternateColorCodes('&', creators);
		arena.setCreators(creators);
		sender.sendMessage(ChatColor.YELLOW + "You have set the arena creators of "
				+ ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + " to: " + ChatColor.WHITE + creators);
	}
	
	@Subcommand("description")
	public void description(CommandSender sender, String regionName, String desc) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		desc = ChatColor.translateAlternateColorCodes('&', desc);
		arena.setDescription(desc);
		sender.sendMessage(ChatColor.YELLOW + "You have set the arena description of "
				+ ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + " to: " + ChatColor.WHITE + desc);
	}
	
	@Subcommand("item")
	public void item(Player player, String regionName) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			player.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (mainHand == null) {
			player.sendMessage(ChatColor.RED + "Hold an item in your hand.");
			return;
		}
		
		arena.setDisplayItem(mainHand);
		player.sendMessage(ChatColor.YELLOW + "You have set the arena item stack of "
				+ ChatColor.GOLD + arena.getName() + ChatColor.YELLOW + " to: " + ChatColor.WHITE + ItemUtil.getName(mainHand));
	}
	
	@Subcommand("spawn")
	public void spawn(Player player, String regionName, String teamColorStr) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			player.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		ChatColor teamColor = ChatColor.valueOf(teamColorStr.toUpperCase());
		Location spawn = player.getLocation();
		arena.addSpawnPoint(teamColor, spawn);
		player.sendMessage(ChatColor.YELLOW + "You have set spawn of team "
				+ teamColor + teamColor.name() + ChatColor.YELLOW + " to " + ChatColor.GOLD + spawn.toVector().toString());
	}
	
	@Subcommand("spawn remove")
	public void spawnRemove(Player player, String regionName, String teamColorStr) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			player.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		
		ChatColor teamColor = ChatColor.valueOf(teamColorStr.toUpperCase());
		arena.removeSpawnPoint(teamColor);
		player.sendMessage(ChatColor.YELLOW + "You have removed spawn of team "
				+ teamColor + teamColor.name());
	}
	
	@Subcommand("dueltype add")
	public void addDuelType(CommandSender sender, String regionName, String duelTypeStr) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		DuelType duelType;
		try {
			duelType = DuelType.valueOf(duelTypeStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "That duel type doesn't exist.");
			return;
		}
		arena.addDuelType(duelType);
		sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.GOLD + duelType + ChatColor.YELLOW + " to " + ChatColor.GOLD + arena.getName());
	}
	
	@Subcommand("dueltype remove")
	public void removeDuelType(CommandSender sender, String regionName, String duelTypeStr) {
		Arena arena = arenaManager.getArena(regionName);
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "That arena doesn't exist.");
			return;
		}
		DuelType duelType;
		try {
			duelType = DuelType.valueOf(duelTypeStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "That duel type doesn't exist.");
			return;
		}
		arena.removeDuelType(duelType);
		sender.sendMessage(ChatColor.YELLOW + "You have removed " + ChatColor.GOLD + duelType + ChatColor.YELLOW + " from " + ChatColor.GOLD + arena.getName());
	}
	
}
