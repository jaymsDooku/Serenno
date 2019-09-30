package io.jayms.serenno.game;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.github.maxopoly.finale.classes.ability.item.LinkerItem;
import com.github.maxopoly.finale.classes.ability.item.SugarRushItem;

import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.util.MaterialTools;
import net.md_5.bungee.api.ChatColor;

public final class DefaultKits {
	
	public static ItemStack health() {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
		.meta(new ItemMetaBuilder()
		.potionData(new PotionData(PotionType.INSTANT_HEAL, false, true))).build();
	}
	
	public static ItemStack fres(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
						.potionData(new PotionData(PotionType.FIRE_RESISTANCE, true, false))).build();
	}
	
	public static ItemStack strength(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
						.name(ChatColor.DARK_PURPLE + "Strength Potion")
						.colour(Color.fromRGB(98, 24, 23))
						.effects(Arrays.asList(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, 1)))).build();
	}
	
	public static ItemStack speed(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
						.name(ChatColor.AQUA + "Speed Potion")
						.colour(Color.fromRGB(122, 172, 195))
						.effects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, duration * 20, 1)))).build();
	}
	
	public static ItemStack regen(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
						.name(ChatColor.LIGHT_PURPLE + "Regeneration Potion")
						.colour(Color.fromRGB(158, 71, 132))
						.effects(Arrays.asList(new PotionEffect(PotionEffectType.REGENERATION, duration * 20, 0)))).build();
	}
	
	public static ItemStack poisonExtended(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
				.colour(Color.fromRGB(43, 135, 63))
				.effects(Arrays.asList(new PotionEffect(PotionEffectType.POISON, duration * 20, 0)))).build();
	}
	
	public static ItemStack slow(int duration) {
		return new ItemStackBuilder(Material.SPLASH_POTION, 1)
				.meta(new ItemMetaBuilder()
				.colour(Color.fromRGB(73, 76, 74))
				.effects(Arrays.asList(new PotionEffect(PotionEffectType.SLOW, duration * 20, 0)))).build();
	}
	
	public static ItemStack dhelmet() {
		return helmet(Material.DIAMOND_HELMET);
	}
	
	public static ItemStack dchest() {
		return chest(Material.DIAMOND_CHESTPLATE);
	}
	
	public static ItemStack dlegs() {
		return legs(Material.DIAMOND_LEGGINGS);
	}
	
	public static ItemStack dboots() {
		return boots(Material.DIAMOND_BOOTS);
	}
	
	//start bard items
	
	public static ItemStack ghelmet() {
		return helmet(Material.GOLD_HELMET);
	}
	
	public static ItemStack gchest() {
		return chest(Material.GOLD_CHESTPLATE);
	}
	
	public static ItemStack glegs() {
		return legs(Material.GOLD_LEGGINGS);
	}
	
	public static ItemStack gboots() {
		return boots(Material.GOLD_BOOTS);
	}
	
	public static ItemStack sugar() {
		ItemStack result = CustomItemManager.getCustomItemManager().getCustomItem(SugarRushItem.class).getItemStack();
		result.setAmount(64);
		return result;
	}
	
	public static ItemStack feather() {
		return new ItemStack(Material.FEATHER, 64);
	}
	
	public static ItemStack blazePowder() {
		return new ItemStack(Material.BLAZE_POWDER, 64);
	}
	
	public static ItemStack ironIngot() {
		return new ItemStack(Material.IRON_INGOT, 64);
	}
	
	public static ItemStack ghastTear() {
		return new ItemStack(Material.GHAST_TEAR, 64);
	}
	
	public static ItemStack spiderEye() {
		return new ItemStack(Material.SPIDER_EYE, 64);
	}
	
	//end bard items
	
	//start archer items
	
	public static ItemStack lhelmet() {
		return helmet(Material.LEATHER_HELMET);
	}
	
	public static ItemStack lchest() {
		return chest(Material.LEATHER_CHESTPLATE);
	}
	
	public static ItemStack llegs() {
		return legs(Material.LEATHER_LEGGINGS);
	}
	
	public static ItemStack lboots() {
		return boots(Material.LEATHER_BOOTS);
	}
	
	//end archer items
	
	public static ItemStack helmet(Material type) {
		ItemStackBuilder helmIt = new ItemStackBuilder(type, 1);
		if (MaterialTools.isHelmet(type)) {
			helmIt.meta(new ItemMetaBuilder()
					.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false)
					.enchant(Enchantment.DURABILITY, 3, false)
					.enchant(Enchantment.OXYGEN, 3, false)
					.enchant(Enchantment.WATER_WORKER, 1, false));
		}
		return helmIt.build();
	}
	
	public static ItemStack boots(Material type) {
		ItemStackBuilder bootsIt = new ItemStackBuilder(type, 1);
		if (MaterialTools.isBoots(type)) {
			bootsIt.meta(new ItemMetaBuilder()
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false)
						.enchant(Enchantment.DURABILITY, 3, false)
						.enchant(Enchantment.PROTECTION_FALL, 4, false)
						.enchant(Enchantment.DEPTH_STRIDER, 3, false));
		}
		return bootsIt.build();
	}
	
	public static ItemStack chest(Material type) {
		ItemStackBuilder chestIt = new ItemStackBuilder(type, 1);
		if (MaterialTools.isChestplate(type)) {
			chestIt.meta(new ItemMetaBuilder()
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false)
						.enchant(Enchantment.DURABILITY, 3, false));
		}
		return chestIt.build();
	}
	
	public static ItemStack legs(Material type) {
		ItemStackBuilder legsIt = new ItemStackBuilder(type, 1);
		if (MaterialTools.isLeggings(type)) {
			legsIt.meta(new ItemMetaBuilder()
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false)
						.enchant(Enchantment.DURABILITY, 3, false));
		}
		return legsIt.build();
	}
	
	public static ItemStack swordkb2() {
		ItemStack swordIt = new ItemStackBuilder(Material.DIAMOND_SWORD, 1)
		.meta(new ItemMetaBuilder()
				.enchant(Enchantment.DAMAGE_ALL, 3, false)
				.enchant(Enchantment.DURABILITY, 3, false)
				.enchant(Enchantment.FIRE_ASPECT, 2, false)
				.enchant(Enchantment.KNOCKBACK, 2, false))
		.build();
		
		return swordIt;
	}
	
	public static ItemStack swordkb1() {
		ItemStack swordIt = new ItemStackBuilder(Material.DIAMOND_SWORD, 1)
		.meta(new ItemMetaBuilder()
				.enchant(Enchantment.DAMAGE_ALL, 3, false)
				.enchant(Enchantment.DURABILITY, 3, false)
				.enchant(Enchantment.FIRE_ASPECT, 2, false)
				.enchant(Enchantment.KNOCKBACK, 1, false))
		.build();
		
		return swordIt;
	}
	
	public static ItemStack sword() {
		return sword(3);
	}
	
	public static ItemStack sword(int sharpness) {
		ItemStack swordIt = new ItemStackBuilder(Material.DIAMOND_SWORD, 1)
		.meta(new ItemMetaBuilder()
				.enchant(Enchantment.DAMAGE_ALL, sharpness, false)
				.enchant(Enchantment.DURABILITY, 3, false)
				.enchant(Enchantment.FIRE_ASPECT, 2, false))
		.build();
		
		return swordIt;
	}
	
	public static ItemStack bow() {
		ItemStack bowIt = new ItemStackBuilder(Material.BOW, 1)
		.meta(new ItemMetaBuilder()
				.enchant(Enchantment.ARROW_DAMAGE, 5, false)
				.enchant(Enchantment.ARROW_INFINITE, 1, false)
				.enchant(Enchantment.ARROW_FIRE, 1, false)
				.enchant(Enchantment.DURABILITY, 3, false)
				)		
		.build();
		return bowIt;
	}
	
	public static ItemStack bowPunch() {
		ItemStack bowIt = new ItemStackBuilder(Material.BOW, 1)
		.meta(new ItemMetaBuilder()
				.enchant(Enchantment.ARROW_DAMAGE, 5, false)
				.enchant(Enchantment.ARROW_INFINITE, 1, false)
				.enchant(Enchantment.ARROW_FIRE, 1, false)
				.enchant(Enchantment.ARROW_KNOCKBACK, 2, false)
				.enchant(Enchantment.DURABILITY, 3, false)
				)		
		.build();
		return bowIt;
	}
	
	public static ItemStack arrows() {
		return new ItemStack(Material.ARROW, 1);
	}
	
	public static ItemStack pickaxe() {
		return new ItemStackBuilder(Material.DIAMOND_PICKAXE, 1)
				.meta(new ItemMetaBuilder()
						.enchant(Enchantment.DIG_SPEED, 5, false)
						.enchant(Enchantment.DURABILITY, 3, false)).build();
	}
	
	public static ItemStack axe() {
		return new ItemStackBuilder(Material.DIAMOND_AXE, 1)
				.meta(new ItemMetaBuilder()
						.enchant(Enchantment.DIG_SPEED, 5, false)
						.enchant(Enchantment.DURABILITY, 3, false)).build();
	}
	
	public static ItemStack spade() {
		return new ItemStackBuilder(Material.DIAMOND_SPADE, 1)
				.meta(new ItemMetaBuilder()
						.enchant(Enchantment.DIG_SPEED, 5, false)
						.enchant(Enchantment.DURABILITY, 3, false)).build();
	}
	
	public static ItemStack waterBucket() {
		return new ItemStack(Material.WATER_BUCKET, 1);
	}
	
	public static ItemStack obby() {
		return new ItemStack(Material.OBSIDIAN, 64);
	}
	
	public static ItemStack stone() {
		return new ItemStack(Material.STONE, 64);
	}
	
	public static ItemStack webs() {
		return new ItemStack(Material.WEB, 64);
	}
	
	public static ItemStack pearls() {
		return new ItemStack(Material.ENDER_PEARL, 16);
	}
	
	public static ItemStack carrots() {
		return new ItemStack(Material.GOLDEN_CARROT, 64);
	}
	
	public static ItemStack steak() {
		return new ItemStack(Material.COOKED_BEEF, 64);
	}
	
	public static ItemStack godApple() {
		return new ItemStackBuilder(Material.GOLDEN_APPLE, 64)
				.durability((short) 1)
				.build();
	}
	
	public static ItemStack linker() {
		ItemStack result = CustomItemManager.getCustomItemManager().getCustomItem(LinkerItem.class).getItemStack();
		result.setAmount(64);
		return result;
	}
	
	public static Kit prot4Set() {
		return new Kit().helmet(dhelmet())
				.chestplate(dchest())
				.leggings(dlegs())
				.boots(dboots())
				.set(0, sword())
				.set(1, pearls())
				.offhand(carrots());
	}
	
	public static Kit prot4LeatherSet() {
		return new Kit().helmet(lhelmet())
				.chestplate(lchest())
				.leggings(llegs())
				.boots(lboots())
				.set(0, sword())
				.set(1, pearls())
				.offhand(carrots());
	}
	
	public static Kit combo() {
		Kit result = prot4Set()
				.set(2, godApple());
		return result;
	}
	
	public static Kit noDebuff() {
		Kit result = prot4Set()
				.range(2, 36, health())
				.set(5, fres(8 * 60))
				.set(6, regen(150))
				.set(7, strength(150))
				.set(8, speed(150))
				
				.set(24, regen(150))
				.set(25, strength(150))
				.set(26, speed(150))
				
				.set(33, regen(150))
				.set(34, strength(150))
				.set(35, speed(150));
		/*for (int j = 6; j < 36; j+=(24-15)) {
			int t = 0;
			for (int i = j; i < (j+3); i++) {
				result.set(i, buff(t++, 150));
			}
		}*/
		
		return result;
	}
	
	public static Kit debuff() {
		return noDebuff()
				.set(9, slow(67))
				.set(10, poisonExtended(33))
				.set(18, slow(67))
				.set(19, poisonExtended(33));
	}
	
	public static Kit archer() {
		Kit result = prot4LeatherSet()
				.range(4, 36, health())
				.set(2, bow())
				.set(3, linker())
				.set(5, fres(8 * 60))
				.set(6, regen(150))
				.set(7, strength(150))
				.set(8, sugar())
				
				.set(25, regen(150))
				.set(26, strength(150))
				
				.set(34, regen(150))
				.set(35, strength(150));
		return result;
	}
	
	public static Kit vaultBattle() {
		Kit result = noDebuff();
		result.set(9, pickaxe());
		result.set(10, spade());
		result.set(11, axe());
		return result;
	}
	
	public static Kit koth() {
		Kit result = noDebuff()
				.set(15, regen(150))
				.set(16, strength(150))
				.set(17, speed(150));
		return result;
	}
	
	public static ItemStack buff(int t, int duration) {
		switch(t) {
			case 0:
				return regen(duration);
			case 1:
				return strength(duration);
			case 2:
				return speed(duration);
			default:
				return strength(duration);
		}
	}
	
	
}
