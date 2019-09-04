package io.jayms.serenno.command.region;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionManager;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.MultiPageView;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;

@CivCommand(id = "regionflags")
public class RegionFlagsCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return true;
		}
		
		Set<String> flags = region.getFlags();
		List<String> possibleFlags = region.getPossibleFlags();
		List<IClickable> clicks = new LinkedList<IClickable>();
		for (int i = 0; i < possibleFlags.size(); i++) {
			String flag = possibleFlags.get(i);
			ItemStack is = new ItemStackBuilder(Material.INK_SACK, 1).durability((short)1).build();
			if (flags.contains(flag)) {
				is.setDurability((short)10);
				ISUtils.setName(is, ChatColor.GREEN + flag);
			} else {
				ISUtils.setName(is, ChatColor.RED + flag);
			}
			final int g = i;
			IClickable click = new Clickable(is) {
				
				@Override
				public void clicked(Player p) {
					ClickableInventory ci = ClickableInventory.getOpenInventory(p);
					if (region.getFlags().contains(flag)) {
						region.getFlags().remove(flag);
						is.setDurability((short)1);
						ISUtils.setName(is, ChatColor.RED + flag);
					} else {
						region.getFlags().add(flag);
						is.setDurability((short)10);
						ISUtils.setName(is, ChatColor.GREEN + flag);
					}
					ci.setSlot(this, g);
					region.setDirty(true);
				}
				
				@Override
				public void addedToInventory(ClickableInventory inv, int slot) {
					super.addedToInventory(inv, slot);
					if (inv.getInventory() != null) {
						inv.getInventory().setItem(slot, getItemStack());
					}
				}
				
			};
			clicks.add(click);
		}
		MultiPageView pageView = new MultiPageView((Player) sender, clicks,
				ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " region flags", true);
		pageView.showScreen();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
