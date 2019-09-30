package io.jayms.serenno.game.menu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.statistics.Death;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.game.statistics.DuelStatistics.PlayerType;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.PlayerTools;

public class PlayerReportMenu extends SingleMenu {
	
	public static void open(Player viewer, SerennoPlayer player, SerennoPlayer toSwitch, Map<String, Object> initData) {
		PlayerReportMenu reportMenu = new PlayerReportMenu(player, toSwitch);
		reportMenu.open(viewer, initData);
		new MenuController(reportMenu);
	}

	private SerennoPlayer player;
	private SerennoPlayer toSwitch;
	
	public PlayerReportMenu(SerennoPlayer player, SerennoPlayer toSwitch) {
		super(ChatColor.WHITE + "Player Report: " + ChatColor.RED + player.getBukkitPlayer().getName());
		this.player = player;
		this.toSwitch = toSwitch;
		System.out.println("Instantiating PlayerReportMenu for " + player.getBukkitPlayer().getName());
	}
	
	@Override
	public boolean onOpen(Player player) {
		return true;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Map<String, Object> initData) {
		Duel duel = (Duel) initData.get("duel");
		DuelStatistics report = duel.getStatistics();
		
		int size = 54;
		setSize(size);
		
		Death deathData = report.getDeath(player);
		Kit finalInv = deathData.getFinalInventory();
		ItemStack[] contents = finalInv.contents();
		
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			addButton(i, new SimpleButton.Builder(this)
					.setPickUpAble(false)
					.setItemStack(item).build());
		}
		
		ItemStack[] armour = finalInv.armour();
		for (int i = 0; i < armour.length; i++) {
			ItemStack item = armour[i];
			addButton(36 + i, new SimpleButton.Builder(this)
					.setPickUpAble(false)
					.setItemStack(item).build());
		}
		
		String switchItemName = ChatColor.YELLOW + "Switch to " + ChatColor.RED + toSwitch.getName();
		addButton(45, new SimpleButton.Builder(this)
				.setPickUpAble(false)
				.setItemStack(new ItemStackBuilder(Material.SPECTRAL_ARROW, 1)
						.meta(new ItemMetaBuilder().name(switchItemName)).build())
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player p = (Player) e.getWhoClicked();
						p.closeInventory();
						new BukkitRunnable() {
							
							@Override
							public void run() {
								PlayerReportMenu.open(p, toSwitch, player, initData);
							}
							
						}.runTaskLater(SerennoCrimson.get(), 1L);
					}
				})
				.build());
		
		Map<PotionEffectType, Long> potDurations = deathData.getPotionDurations();
		
		List<String> potDurationsLore = new ArrayList<>();
		for (Entry<PotionEffectType, Long> potDurationEn : potDurations.entrySet()) {
			potDurationsLore.add(ItemUtil.getEffectName(potDurationEn.getKey()) + " - " + ChatColor.RED + ItemUtil.getPotionDurations(potDurationEn.getValue()));
		}
		if (potDurationsLore.isEmpty()) {
			potDurationsLore.add(ChatColor.YELLOW + "No Effects");
		}
		
		addButton(48, new SimpleButton.Builder(this)
				.setPickUpAble(false)
				.setItemStack(new ItemStackBuilder(Material.POTION, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.YELLOW + "Potion Durations")
								.colour(Color.MAROON)
								.lore(potDurationsLore)
								.flag(ItemFlag.HIDE_POTION_EFFECTS)).build())
				.build());
		
		int potsThrown = report.getPotionsThrown(player);
		int potsMissed = report.getPotionsMissed(player);
		
		double potAcc = report.getPotionAccuracy(player);
		double potAccPerc = potAcc * 100;
		String potAccStr = potsThrown > 0 ? dp1.format(potAccPerc) + "%" : "N/A";
		
		addButton(49, new SimpleButton.Builder(this)
					.setPickUpAble(false)
					.setItemStack(new ItemStackBuilder(Material.SPLASH_POTION, 1)
							.meta(new ItemMetaBuilder()
									.name(ChatColor.YELLOW + "Health Potion Stats")
									.colour(Color.RED)
									.lore(Arrays.asList(ChatColor.WHITE + "Missed: " + ChatColor.RED + potsMissed,
											ChatColor.WHITE + "Thrown: " + ChatColor.RED + potsThrown,
											ChatColor.WHITE + "Accuracy: " + ChatColor.RED + potAccStr))
									.flag(ItemFlag.HIDE_POTION_EFFECTS)).build())
					.build());
		
		addButton(50, getOffensiveStatsButton(report));
		addButton(51, getDefensiveStatsButton(report));
		
		double finalHealth = deathData.getFinalHealth() / 2;
		
		addButton(52, new SimpleButton.Builder(this)
					.setPickUpAble(false)
					.setItemStack(new ItemStackBuilder(Material.RED_ROSE, 1)
							.meta(new ItemMetaBuilder()
									.name(ChatColor.RED + "Health")
									.lore(Arrays.asList(ChatColor.RED + dp1.format(finalHealth) + ChatColor.WHITE + "/" + ChatColor.RED + "10"))).build())
					.build());
		
		int finalFoodLevel = deathData.getFinalHunger() / 2;
		
		addButton(53, new SimpleButton.Builder(this)
				.setPickUpAble(false)
				.setItemStack(new ItemStackBuilder(Material.COOKED_BEEF, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.YELLOW + "Food Level")
								.lore(Arrays.asList(ChatColor.YELLOW + Integer.toString(finalFoodLevel) + ChatColor.WHITE + "/" + ChatColor.YELLOW + "10"))).build())
				.build());
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private DecimalFormat dp1 = new DecimalFormat("#.#");
	
	private SimpleButton getDefensiveStatsButton(DuelStatistics report) {
		int hitsTaken = report.getHits(player, PlayerType.VICTIM);
		int critsTaken = report.getCrits(player, PlayerType.VICTIM);
		double damageTaken = report.getDamage(player, PlayerType.VICTIM);
		
		return new SimpleButton.Builder(this)
		.setPickUpAble(false)
		.setItemStack(new ItemStackBuilder(Material.SHIELD, 1)
				.meta(new ItemMetaBuilder()
						.name(ChatColor.YELLOW + "Defensive Stats")
						.lore(Arrays.asList(ChatColor.WHITE + "Hits Taken: " + ChatColor.RED + hitsTaken,
								ChatColor.WHITE + "Crits Taken: " + ChatColor.RED + critsTaken,
								ChatColor.WHITE + "Damage Taken: " + ChatColor.RED + dp1.format(damageTaken)))).build())
		.build();
	}
	
	private SimpleButton getOffensiveStatsButton(DuelStatistics report) {
		int hitsDealt = report.getHits(player, PlayerType.DAMAGER);
		int critsDealt = report.getCrits(player, PlayerType.DAMAGER);
		double damageDealt = report.getDamage(player, PlayerType.DAMAGER);
		
		return new SimpleButton.Builder(this)
				.setPickUpAble(false)
				.setItemStack(new ItemStackBuilder(Material.IRON_SWORD, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.YELLOW + "Offensive Stats")
								.lore(Arrays.asList(ChatColor.WHITE + "Hits Dealt: " + ChatColor.RED + hitsDealt,
										ChatColor.WHITE + "Crits Dealt: " + ChatColor.RED + critsDealt,
										ChatColor.WHITE + "Damage Dealt: " + ChatColor.RED + dp1.format(damageDealt)))).build())
				.build();
	}

}
