package io.jayms.serenno.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.boydti.fawe.object.FawePlayer;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;

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
		Collection<PotionEffect> effects = player.getActivePotionEffects();
		System.out.println("effects: " + effects);
		return effects.stream()
			.filter(e -> e.getType() == type)
			.findFirst()
			.orElse(null);
	}
	
	public static Map<PotionEffectType, Long> getPotionDurations(Player bukkitPlayer) {
		Map<PotionEffectType, Long> durations = new HashMap<>();
		
		Collection<PotionEffect> effects = bukkitPlayer.getActivePotionEffects();
		effects.stream().forEach(pe -> {
			int durTicks = pe.getDuration();
			long dur = 50 * durTicks;
			durations.put(pe.getType(), dur);
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
	
	public static Clipboard getClipboard(Player player){
		FawePlayer<Player> fawePlayer = FawePlayer.wrap(player);
		com.sk89q.worldedit.regions.Region selection = fawePlayer.getSelection();
		if (selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
			player.sendMessage(ChatColor.RED + "Select 2 points with world edit wand.");
			return null;
		}
		
		Vector p1 = new Vector(selection.getMinimumPoint().getX(), selection.getMinimumPoint().getY(), selection.getMinimumPoint().getZ());
		Vector p2 = new Vector(selection.getMaximumPoint().getX(), selection.getMaximumPoint().getY(), selection.getMaximumPoint().getZ());
		
		World w = Bukkit.getWorld(selection.getWorld().getName());
		
		if (w == null) {
			player.sendMessage(ChatColor.RED + "Clipboard selection error.");
			return null;
		}
		
		Location l1 = new Location(w, p1.getX(), p1.getY(), p1.getZ());
		Location l2 = new Location(w, p2.getX(), p2.getY(), p2.getZ());
		Clipboard cb = new Clipboard();
		cb.p1 = l1;
		cb.p2 = l2;
		return cb;
	}
	
	public static class Clipboard {
		
		Location p1;
		Location p2;
		
		Clipboard() {
		}
		
		public Location getP1() {
			return p1;
		}
		
		public Location getP2() {
			return p2;
		}
		
	}
	
	public static Entity getTargetedEntity(final Player player, final double range, final List<Entity> avoid) {
		double longestr = range + 1;
		Entity target = null;
		final Location origin = player.getEyeLocation();
		final Vector direction = player.getEyeLocation().getDirection().normalize();
		for (final Entity entity : origin.getWorld().getEntities()) {
			if (entity instanceof Player) {
				if (((Player) entity).isDead() || ((Player) entity).getGameMode().equals(GameMode.SPECTATOR)) {
					continue;
				}
			}
			if (avoid.contains(entity)) {
				continue;
			}
			if (entity.getWorld().equals(origin.getWorld())) {
				if (entity.getLocation().distanceSquared(origin) < longestr * longestr && getDistanceFromLine(direction, origin, entity.getLocation()) < 2 && (entity instanceof LivingEntity) && entity.getEntityId() != player.getEntityId() && entity.getLocation().distanceSquared(origin.clone().add(direction)) < entity.getLocation().distanceSquared(origin.clone().add(direction.clone().multiply(-1)))) {
					target = entity;
					longestr = entity.getLocation().distance(origin);
				}
			}
		}
		if (target != null) {
			if (isObstructed(origin, target.getLocation())) {
				target = null;
			}
		}
		return target;
	}

	public static Entity getTargetedEntity(final Player player, final double range) {
		return getTargetedEntity(player, range, new ArrayList<Entity>());
	}
	
	public static final List<Material> NON_OPAQUE = Arrays.asList(Material.AIR, Material.SAPLING, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.WEB, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.TORCH, Material.FIRE, Material.REDSTONE_WIRE, Material.CROPS, Material.LADDER, Material.RAILS, Material.SIGN_POST, Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW, Material.SUGAR_CANE_BLOCK, Material.PORTAL, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.VINE, Material.WATER_LILY, Material.NETHER_STALK, Material.ENDER_PORTAL, Material.COCOA, Material.TRIPWIRE_HOOK, Material.TRIPWIRE, Material.FLOWER_POT, Material.CARROT, Material.POTATO, Material.WOOD_BUTTON, Material.GOLD_PLATE, Material.IRON_PLATE, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.DAYLIGHT_DETECTOR, Material.CARPET, Material.DOUBLE_PLANT, Material.STANDING_BANNER, Material.WALL_BANNER, Material.DAYLIGHT_DETECTOR_INVERTED, Material.END_ROD, Material.CHORUS_PLANT, Material.CHORUS_FLOWER, Material.BEETROOT_BLOCK, Material.END_GATEWAY);
	
	public static boolean isObstructed(final Location location1, final Location location2) {
		final Vector loc1 = location1.toVector();
		final Vector loc2 = location2.toVector();

		final Vector direction = loc2.subtract(loc1);
		direction.normalize();

		Location loc;

		double max = 0;
		if (location1.getWorld().equals(location2.getWorld())) {
			max = location1.distance(location2);
		}

		for (double i = 0; i <= max; i++) {
			loc = location1.clone().add(direction.clone().multiply(i));
			final Material type = loc.getBlock().getType();
			if (type != Material.AIR && !(NON_OPAQUE.contains(type) || isWater(loc.getBlock()))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isWater(final Block block) {
		return block != null ? isWater(block.getType()) : null;
	}

	public static boolean isWater(final Material material) {
		return material == Material.WATER || material == Material.STATIONARY_WATER;
	}
	
	public static double getDistanceFromLine(final Vector line, final Location pointonline, final Location point) {
		final Vector AP = new Vector();
		double Ax, Ay, Az;
		Ax = pointonline.getX();
		Ay = pointonline.getY();
		Az = pointonline.getZ();

		double Px, Py, Pz;
		Px = point.getX();
		Py = point.getY();
		Pz = point.getZ();

		AP.setX(Px - Ax);
		AP.setY(Py - Ay);
		AP.setZ(Pz - Az);

		return (AP.crossProduct(line).length()) / (line.length());
	}
	
}
