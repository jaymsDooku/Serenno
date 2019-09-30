package io.jayms.serenno.manager;

import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.listener.citadel.ArtilleryListener;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.menu.TrebuchetCrateMenu;
import io.jayms.serenno.model.citadel.artillery.menu.TrebuchetMenu;
import io.jayms.serenno.util.Coords;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.locations.QTBox;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;

public class ArtilleryManager {

	private CitadelManager cm;
	
	private Map<Coords, ArtilleryCrate> crates = Maps.newConcurrentMap();
	private SparseQuadTree artilleries;
	
	private ArtilleryListener artilleryListener;
	
	private TrebuchetCrateMenu trebuchetCrateMenu;
	private MenuController trebuchetCrateMenuController;
	
	private TrebuchetMenu trebuchetMenu;
	private MenuController trebuchetMenuController;
	
	public ArtilleryManager(CitadelManager cm) {
		this.cm = cm;
		this.artilleries = new SparseQuadTree(900);
		this.artilleryListener = new ArtilleryListener(this);
		
		Bukkit.getPluginManager().registerEvents(artilleryListener, SerennoCobalt.get());
	}
	
	public void placeArtilleryCrate(Player player, ArtilleryCrate crate, Location loc) {
		crates.put(Coords.fromLocation(loc), crate);
		crate.setLocation(loc);
		player.sendMessage(ChatColor.YELLOW + "You have placed down a " + crate.getDisplayName());
	}
	
	public ArtilleryCrate getArtilleryCrate(Location loc) {
		return crates.get(Coords.fromLocation(loc));
	}
	
	public void assemble(Artillery artillery) {
		artilleries.add(artillery);
	}
	
	public void disassemble(Artillery artillery) {
		artilleries.remove(artillery);
	}
	
	public Artillery getArtillery(Location loc) {
		Set<? extends QTBox> boxes = artilleries.find(loc.getBlockX(), loc.getBlockZ());
		if (boxes.isEmpty()) {
			return null;
		}
		return (Artillery) boxes.iterator().next();
	}
	
	public TrebuchetCrateMenu getTrebuchetCrateMenu() {
		if (trebuchetCrateMenu == null) {
			trebuchetCrateMenu = new TrebuchetCrateMenu();
			trebuchetMenuController = new MenuController(trebuchetCrateMenu);
		}
		return trebuchetCrateMenu;
	}
	
	public TrebuchetMenu getTrebuchetMenu() {
		if (trebuchetMenu == null) {
			trebuchetMenu = new TrebuchetMenu();
			trebuchetMenuController = new MenuController(trebuchetMenu);
		}
		return trebuchetMenu;
	}

}
