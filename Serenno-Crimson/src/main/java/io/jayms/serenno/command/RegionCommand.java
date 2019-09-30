package io.jayms.serenno.command;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.world.World;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionManager;
import io.jayms.serenno.util.PlayerTools;
import io.jayms.serenno.util.PlayerTools.Clipboard;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.MultiPageView;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;

@CommandAlias("region")
public class RegionCommand extends BaseCommand {

	private RegionManager regionManager = SerennoCrimson.get().getRegionManager();
	
	@Subcommand("create")
	public void create(Player player, String regionName) {
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.createRegion(player, regionName);
		if (region == null) {
			return;
		}
		player.sendMessage(ChatColor.YELLOW + "You have created a new region: " + ChatColor.GOLD + region.getName());
	}
	
	@Subcommand("delete")
	public void delete(Player player, String regionName) {
		regionManager.deleteRegion(player, regionName);
	}
	
	@Subcommand("flags")
	public void flags(Player player, String regionName) {
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
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
				public boolean clicked(Player p) {
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
					return true;
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
		MultiPageView pageView = new MultiPageView(player, clicks,
				ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " region flags", true);
		pageView.showScreen();
	}
	
	@Subcommand("import")
	public void importRegion(Player player, String regionName, String schematicName) {
		File schematicsFolder = SerennoCrimson.get().getSchematicsFolder();
		File file = new File(schematicsFolder, schematicName);
		boolean allowUndo = true;
		boolean noAir = false;
		Location origin = player.getLocation();
		World world = new BukkitWorld(origin.getWorld());
		com.sk89q.worldedit.Vector position = new com.sk89q.worldedit.Vector(origin.getX(), origin.getY(), origin.getZ());
		Schematic schematic;
		try {
			schematic = ClipboardFormats.findByFile(file).load(file);
			schematic.paste(world, position, allowUndo, !noAir, (Transform) null);
			
			Location end = origin.clone().add(schematic.getClipboard().getRegion().getWidth(), schematic.getClipboard().getRegion().getHeight(), schematic.getClipboard().getRegion().getLength());
			
			RegionManager regionManager = SerennoCrimson.get().getRegionManager();
			Region region = regionManager.createRegion(player, regionName, origin, end);
			if (region == null) {
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Successfully imported " + ChatColor.DARK_GREEN + schematicName + ChatColor.GREEN + " as " + ChatColor.GOLD + region.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Subcommand("info")
	public void info(CommandSender sender, String regionName) {
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
		}
		
		sender.sendMessage(ChatColor.GOLD + region.getName() + ChatColor.YELLOW + "'s region information");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------");
		sender.sendMessage(ChatColor.GOLD + "Display Name: " + region.getDisplayName());
		sender.sendMessage(ChatColor.GOLD + "Parent World: " + ChatColor.YELLOW + region.getParentWorld().getName());
		sender.sendMessage(ChatColor.GOLD + "Point 1: " + ChatColor.YELLOW + region.getPoint1().toString());
		sender.sendMessage(ChatColor.GOLD + "Point 2: " + ChatColor.YELLOW + region.getPoint2().toString());
		
		StringBuilder flagSB = new StringBuilder();
		flagSB.append(ChatColor.GOLD + "{");
		int i = 1;
		for (String flag : region.getPossibleFlags()) {
			flagSB.append(ChatColor.YELLOW + flag);
			flagSB.append(ChatColor.GOLD + "=");
			flagSB.append(region.getFlags().contains(flag) ? ChatColor.GREEN  + "true": ChatColor.RED + "false");
			if (i < region.getPossibleFlags().size()) {
				flagSB.append(ChatColor.GOLD + ",");
			}
			i++;
		}
		flagSB.append(ChatColor.GOLD + "}");
		sender.sendMessage(ChatColor.GOLD + "Flags: " + ChatColor.YELLOW + flagSB.toString());
	}
	
	@Subcommand("list")
	public void list(CommandSender sender, String regionName) {
		List<Region> regions = regionManager.listRegions();
		sender.sendMessage(ChatColor.YELLOW + "Regions");
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "---------------");
		regions.stream().forEach(region -> {
			sender.sendMessage(ChatColor.YELLOW + region.getName());
		});
	}
	
	@Subcommand("save")
	public void save(Player player, String regionName) {
		regionManager.saveRegion(player, regionName);
	}
	
	@Subcommand("displayname")
	public void displayName(CommandSender sender, String regionName, String displayName) {
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
		}
		
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		region.setDisplayName(displayName);
		region.setDirty(true);
		sender.sendMessage(ChatColor.YELLOW + "You have set display name of region " 
		+ ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " to " + displayName);
	}
	
	@Subcommand("boundaries")
	public void setBoundaries(Player player, String regionName) {
		Region region = regionManager.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "That region doesn't exist.");
			return;
		}
		
		Clipboard cb = PlayerTools.getClipboard(player);
		Location p1 = cb.getP1();
		Location p2 = cb.getP2();
		
		region.setParentWorld(p1.getWorld());
		
		Vector v1 = p1.toVector();
		Vector v2 = p2.toVector();
		region.setPoint1(v1);
		region.setPoint2(v2);
		player.sendMessage(ChatColor.YELLOW + "You've set the parent world of region: " + ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " to " + ChatColor.GOLD + p1.getWorld().getName());
		player.sendMessage(ChatColor.YELLOW + "You've set point 1 of region: " + ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " to " + ChatColor.GOLD + v1);
		player.sendMessage(ChatColor.YELLOW + "You've set point 2 of region: " + ChatColor.GOLD + region.getName() + ChatColor.YELLOW + " to " + ChatColor.GOLD + v2);
	}
	
}
