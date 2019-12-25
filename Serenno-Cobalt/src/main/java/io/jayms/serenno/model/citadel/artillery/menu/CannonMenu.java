package io.jayms.serenno.model.citadel.artillery.menu;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.Button;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.model.citadel.artillery.trebuchet.Trebuchet;
import io.jayms.serenno.util.ItemUtil;

public class CannonMenu extends SingleMenu {
	
	private boolean isOpen = false;
	
	public CannonMenu() {
		super("");
	}
	
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	@Override
	public boolean allowPlayerInventory() {
		return true;
	}

	@Override
	public boolean onOpen(Player player) {
		if (isOpen()) {
			player.sendMessage(ChatColor.RED + "Someone is already looking into the Trebuchet.");
			return false;
		}
		setOpen(true);
		return true;
	}
	
	@Override
	public void onClose(Player player, Inventory inventory, Map<String, Object> data) {
		Trebuchet trebuchet = (Trebuchet) data.get("artillery");
		
		int ammo = 0;
		int k = 14;
		for (int i = 0; i < 3; i++) {
			for (int j = k; j < k + 3; j++) {
				Button button = getButton(j);
				if (button == null) continue;
				ItemStack it = inventory.getItem(j);
				if (it == null) continue;
				if (it.getType() != trebuchet.getFiringAmmoMaterial()) {
					throw new IllegalStateException("A non-ammo material found inside trebuchet.");
				}
				ammo += it.getAmount();
			}
			k += 9;
		}
		trebuchet.setFiringAmmoAmount(ammo);
		setOpen(false);
	}

	@Override
	public void onClose(Player player) {
	}
	
	private ItemStack firingAngleDisplay;
	private ItemStack firingPowerDisplay;

	@Override
	public Inventory newInventory(Map<String, Object> initData) {
		Trebuchet trebuchet = (Trebuchet) initData.get("artillery");
		setName(trebuchet.getDisplayName());
		
		int size = 45;
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		
		addButton(1, getFiringAngleAdjustButton(inventory, trebuchet, Adjustment.ADD, 1));
		addButton(10, getFiringAngleAdjustButton(inventory, trebuchet, Adjustment.ADD, 0.1));
		addButton(28, getFiringAngleAdjustButton(inventory, trebuchet, Adjustment.SUB, 0.1));
		addButton(37, getFiringAngleAdjustButton(inventory, trebuchet, Adjustment.SUB, 1));
		
		addButton(19, getFiringAngleDisplayButton(trebuchet));
		
		addButton(3, getFiringPowerAdjustButton(inventory, trebuchet, Adjustment.ADD, 1));
		addButton(12, getFiringPowerAdjustButton(inventory, trebuchet, Adjustment.ADD, 0.1));
		addButton(30, getFiringPowerAdjustButton(inventory, trebuchet, Adjustment.SUB, 0.1));
		addButton(39, getFiringPowerAdjustButton(inventory, trebuchet, Adjustment.SUB, 1));
		
		addButton(21, getFiringPowerDisplayButton(trebuchet));
		
		addButton(6, getAmmoTitleButton());
		
		int ammoAmount = trebuchet.getFiringAmmoAmount();
		int k = 14;
		for (int i = 0; i < 3; i++) {
			for (int j = k; j < k + 3; j++) {
				addButton(j, getAmmoItemButton(trebuchet, ammoAmount));
				if (ammoAmount >= 64) {
					ammoAmount -= 64;
				} else {
					ammoAmount -= ammoAmount;
				}
				if (ammoAmount <= 0) {
					ammoAmount = 0;
				}
			}
			k += 9;
		}
		
		refresh(inventory);
		return inventory;
	}
	
	private enum Adjustment {
		ADD, SUB;
	}
	
	private SimpleButton getFiringAngleAdjustButton(Inventory inventory, Trebuchet trebuchet, Adjustment adjustment, double step) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.ARROW, 1)
						.meta(new ItemMetaBuilder()
								.name(adjustment == Adjustment.ADD ? ChatColor.GREEN + "+" + step : ChatColor.RED + "-" + step)).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						double newAngle = trebuchet.getFiringAngleThreshold();
						if (adjustment == Adjustment.ADD) {
							newAngle += step;
						} else {
							newAngle -= step;
						}
						if (newAngle <= 0) {
							newAngle = 1.0;
						}
						trebuchet.setFiringAngleThreshold(newAngle);
						ItemUtil.setName(firingAngleDisplay, ChatColor.RED + "Firing Angle: " + ChatColor.WHITE + trebuchet.getFiringAngleThreshold());
						inventory.setItem(19, firingAngleDisplay);
					}
					
				}).build();
	}
	
	private SimpleButton getFiringAngleDisplayButton(Trebuchet trebuchet) {
		firingAngleDisplay = new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
		.durability((short) 14)
		.meta(new ItemMetaBuilder()
				.name(ChatColor.RED + "Firing Angle: " + ChatColor.WHITE + trebuchet.getFiringAngleThreshold())).build();
		
		return new SimpleButton.Builder(this)
				.setItemStack(firingAngleDisplay)
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getFiringPowerAdjustButton(Inventory inventory, Trebuchet trebuchet, Adjustment adjustment, double step) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.ARROW, 1)
						.meta(new ItemMetaBuilder()
								.name(adjustment == Adjustment.ADD ? ChatColor.GREEN + "+" + step : ChatColor.RED + "-" + step)).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						double power = trebuchet.getFiringPower();
						if (adjustment == Adjustment.ADD) {
							power += step;
						} else {
							power -= step;
						}
						trebuchet.setFiringPower(power);
						ItemUtil.setName(firingPowerDisplay, ChatColor.RED + "Firing Power: " + ChatColor.WHITE + trebuchet.getFiringPower());
						inventory.setItem(21, firingPowerDisplay);
					}
					
				}).build();
	}
	
	private SimpleButton getFiringPowerDisplayButton(Trebuchet trebuchet) {
		firingPowerDisplay = new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
		.durability((short) 14)
		.meta(new ItemMetaBuilder()
				.name(ChatColor.RED + "Firing Power: " + ChatColor.WHITE + trebuchet.getFiringPower())).build();
		
		return new SimpleButton.Builder(this)
				.setItemStack(firingPowerDisplay)
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getAmmoTitleButton() {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
						.durability((short) 4)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.YELLOW + "Place ammo below.")).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getAmmoItemButton(Trebuchet trebuchet, int amount) {
		return new SimpleButton.Builder(this)
				.setItemStack(amount == 0 ? null : new ItemStack(trebuchet.getFiringAmmoMaterial(), amount))
				.setNormal(true)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						if (e.getCursor() == null) {
							return;
						}
						
						if (e.getCursor().getType() != trebuchet.getFiringAmmoMaterial()) {
							e.setCancelled(true);
							player.sendMessage(ChatColor.RED + "You can only put ammo here.");
						}
					}
					
				}).build();
	}
	
}

