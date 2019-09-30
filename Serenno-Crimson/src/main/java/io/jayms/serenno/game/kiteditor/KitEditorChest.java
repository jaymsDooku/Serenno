package io.jayms.serenno.game.kiteditor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.kiteditor.KitEditor.KitEditorData;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.LocationTools;
import io.jayms.serenno.util.MaterialTools;
import net.md_5.bungee.api.ChatColor;

public abstract class KitEditorChest implements Listener {

	private KitEditor kitEditor;
	
	private Location kitEditorChest;
	private Inventory chestInv;
	private Set<Integer> chestSlots;
	private Set<UUID> currentlyOpened;
	private boolean filledChest;
	
	public KitEditorChest(KitEditor kitEditor, Location kitEditorChest) {
		this.kitEditor = kitEditor;
		this.kitEditorChest = kitEditorChest;
		this.chestSlots = new HashSet<>();
		this.currentlyOpened = new HashSet<>();
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public abstract Set<DuelType> getValidDuelTypes();
	
	public abstract void fill(Inventory inv);
	
	private boolean resolveChest(Block b, Location chestLoc) {
		if (!MaterialTools.isChest(b)) {
			return false;
		}
		
		if (b.getLocation().equals(chestLoc)) {
			return true;
		}
		
		for (BlockFace face : LocationTools.PLANAR_SIDES) {
			Block rel = b.getRelative(face);
			if (MaterialTools.isChest(rel) && rel.getLocation().equals(chestLoc)) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (currentlyOpened.contains(e.getPlayer().getUniqueId())) {
			currentlyOpened.remove(e.getPlayer().getUniqueId());
			//System.out.println("No longer viewing chest");
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		if (!currentlyOpened.contains(player.getUniqueId())) {
			//System.out.println("Not opened chest.");
			return;
		}
		
		if (e.getInventory() == e.getClickedInventory()) {
			e.setCancelled(true);
			
			if (!chestSlots.contains(e.getSlot())) {
				//System.out.println("Not a good slot.");
				return;
			}
			
			e.setCursor(e.getCurrentItem());
		}
	}
	
	public void setChestItem(int i, ItemStack it) {
		chestInv.setItem(i, it);
		chestSlots.add(i);
	}
	
	@EventHandler
	public void kitChests(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		if (!kitEditor.inKitEditor(sp)) {
			return;
		}
		
		Block clickedBlock = e.getClickedBlock();
		if (clickedBlock == null) {
			return;
		}
		
		if (resolveChest(clickedBlock, kitEditorChest)) {
			KitEditorData ked = kitEditor.getKitEditorData(sp);
			if (!getValidDuelTypes().contains(ked.duelType)) {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "I don't think you'll be needing what's in there... ;)");
				return;
			}
			
			if (!filledChest) {
				if (clickedBlock.getState() instanceof DoubleChest) {
					DoubleChest dc = (DoubleChest) clickedBlock.getState();
					chestInv = dc.getInventory();
					fill(chestInv);
					filledChest = true;
				} else if (clickedBlock.getState() instanceof Chest) {
					Chest chest = (Chest) clickedBlock.getState();
					chestInv = chest.getInventory();
					fill(chestInv);
					filledChest = true;
				}
			}
			currentlyOpened.add(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (currentlyOpened.contains(e.getPlayer().getUniqueId())) {
			currentlyOpened.remove(e.getPlayer().getUniqueId());
		}
	}
	
}
