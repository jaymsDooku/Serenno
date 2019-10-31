package io.jayms.serenno.model.citadel.artillery.menu;

import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.github.maxopoly.finale.classes.engineer.EngineerTools;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.trebuchet.Trebuchet;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetCrate;

public class TrebuchetCrateMenu extends SingleMenu {
	
	public TrebuchetCrateMenu() {
		super("");
	}
	
	@Override
	public boolean allowPlayerInventory() {
		return true;
	}

	@Override
	public boolean onOpen(Player player) {
		return true;
	}

	@Override
	public void onClose(Player player) {
	}
	
	private ItemStack directionDisplay;

	@Override
	public Inventory newInventory(Map<String, Object> initData) {
		TrebuchetCrate crate = (TrebuchetCrate) initData.get("crate");
		setName(crate.getDisplayName());
		
		int size = 45;
		setSize(size);
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		
		addButton(11, getDirectionButton(inventory, crate, BlockFace.NORTH));
		addButton(19, getDirectionButton(inventory, crate, BlockFace.WEST));
		addButton(21, getDirectionButton(inventory, crate, BlockFace.EAST));
		addButton(29, getDirectionButton(inventory, crate, BlockFace.SOUTH));
		
		addButton(20, getDirectionDisplayButton((Trebuchet) crate.getArtillery()));
		
		addButton(14, getStartItemTitle());
		addButton(23, getStarterItemButton());
		
		addButton(25, getBuildButton(inventory, crate));
		
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getDirectionDisplayButton(Trebuchet trebuchet) {
		short dirDura = 11;
		if (trebuchet.getDirection() == BlockFace.NORTH) {
			dirDura = 11;
		} else if (trebuchet.getDirection() == BlockFace.EAST) {
			dirDura = 4;
		} else if (trebuchet.getDirection() == BlockFace.SOUTH) {
			dirDura = 14;
		} else if (trebuchet.getDirection() == BlockFace.WEST) {
			dirDura = 1;
		}
		
		directionDisplay = new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
		.durability(dirDura)
		.meta(new ItemMetaBuilder()
				.name(ChatColor.RED + "Direction: " + ChatColor.WHITE + trebuchet.getDirection().name())).build();
		
		return new SimpleButton.Builder(this)
				.setItemStack(directionDisplay)
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getDirectionButton(Inventory inventory, TrebuchetCrate crate, BlockFace face) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.ARROW, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.YELLOW + WordUtils.capitalize(face.name()))).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						Artillery artillery = crate.getArtillery();
						artillery.setDirection(face);
						player.sendMessage(ChatColor.YELLOW + "You have set " + crate.getDisplayName() + " #" + artillery.getID() + ChatColor.YELLOW + " direction to " + ChatColor.GOLD + WordUtils.capitalize(face.name()));
						inventory.setItem(20, getDirectionDisplayButton((Trebuchet) artillery).getItemStack());
					}
					
				}).build();
	}
	
	private SimpleButton getStartItemTitle() {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
						.durability((short) 14)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.RED + "Place starter item below.")).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getStarterItemButton() {
		return new SimpleButton.Builder(this)
				.setItemStack(null)
				.setNormal(true)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
					}
					
				}).build();
	}
	
	private SimpleButton getBuildButton(Inventory inventory, TrebuchetCrate crate) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.EMERALD_BLOCK, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.GREEN + "Assemble")).build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						EngineerPlayer engineer = EngineerTools.getEngineer(player);
						
						ItemStack starterItem = inventory.getItem(23);
						if (starterItem == null || starterItem.getType() != crate.getStarterItemType() || starterItem.getAmount() != crate.getStarterItemAmount()) {
							player.sendMessage(ChatColor.RED + "Trebuchet requires a starter item in order to assemble.");
							return;
						}
						inventory.remove(starterItem);
						
						player.closeInventory();
						Artillery artillery = crate.getArtillery();
						artillery.assemble(engineer);
					}
					
				}).build();
	}
	
}
