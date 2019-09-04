package io.jayms.serenno.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;

public class PlayerTools {

	public static void clean(Player player) {
		player.getInventory().clear();
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setFireTicks(0);
		player.getActivePotionEffects().stream().forEach(e -> {
			player.removePotionEffect(e.getType());
		});
	}
	
	public static ItemStack getHead(UUID uuid, String name) {
		return getHead(uuid, name, new ArrayList<>());
	}
	
	public static ItemStack getHead(UUID uuid, String name, List<String> lore) {
		return new ItemStackBuilder(Material.SKULL_ITEM, 1)
				.meta(new ItemMetaBuilder().owner(getName(uuid)).name(name).lore(lore))
				.durability((short) 3)
				.build();
	}
	
	public static String getName(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			return offlinePlayer.getName();
		}
		return player.getName();
	}
	
	public static PotionEffect getActiveEffect(Player player, PotionEffectType type) {
		return player.getActivePotionEffects().stream()
			.filter(e -> e.getType() == type)
			.findFirst()
			.orElse(null);
	}
	
	public static Map<PotionEffectType, Double> getPotionDurations(Player bukkitPlayer) {
		Map<PotionEffectType, Double> durations = new HashMap<>();
		
		Collection<PotionEffect> effects = bukkitPlayer.getActivePotionEffects();
		effects.stream().forEach(pe -> {
			durations.put(pe.getType(), ((double)(1000.0D / pe.getDuration())));
		});
		
		return durations;
	}
	
	public static void clearEffects(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	public static boolean isRightClick(Action a) {
		return a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK;
	}
	
	public static boolean isLeftClick(Action a) {
		return a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK;
	}
	
}
