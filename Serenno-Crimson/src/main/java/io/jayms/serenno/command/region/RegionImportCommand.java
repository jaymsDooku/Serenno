package io.jayms.serenno.command.region;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.world.World;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionManager;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "regionimport")
public class RegionImportCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String regionName = args[0];
		String schematicName = args[1];
		Player player = (Player) sender;
		File schematicsFolder = SerennoCrimson.get().getSchematicsFolder();
		File file = new File(schematicsFolder, schematicName);
		boolean allowUndo = true;
		boolean noAir = false;
		Location origin = player.getLocation();
		World world = new BukkitWorld(origin.getWorld());
		Vector position = new Vector(origin.getX(), origin.getY(), origin.getZ());
		Schematic schematic;
		try {
			schematic = ClipboardFormats.findByFile(file).load(file);
			schematic.paste(world, position, allowUndo, !noAir, (Transform) null);
			
			Location end = origin.clone().add(schematic.getClipboard().getRegion().getWidth(), schematic.getClipboard().getRegion().getHeight(), schematic.getClipboard().getRegion().getLength());
			
			RegionManager regionManager = SerennoCrimson.get().getRegionManager();
			Region region = regionManager.createRegion(player, regionName, origin, end);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}
