package io.jayms.serenno.manager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.listener.citadel.ArtilleryListener;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.model.citadel.artillery.Artillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileRunner;
import io.jayms.serenno.model.citadel.artillery.ArtilleryRunnable;
import io.jayms.serenno.model.citadel.artillery.ArtilleryWorld;
import io.jayms.serenno.model.citadel.artillery.menu.TrebuchetCrateMenu;
import io.jayms.serenno.model.citadel.artillery.menu.TrebuchetMenu;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetMissileRunner;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.util.Coords;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.locations.QTBox;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;

public class ArtilleryManager {

	private CitadelManager cm;
	
	private Map<Coords, ArtilleryCrate> crates = Maps.newConcurrentMap();
	private Map<String, ArtilleryWorld> artilleryWorlds = Maps.newConcurrentMap();
	
	private ArtilleryRunnable artilleryRunnable;
	private Map<Class<? extends Artillery>, ArtilleryMissileRunner> missileRunners = Maps.newConcurrentMap();
	
	private ArtilleryListener artilleryListener;
	
	private TrebuchetCrateMenu trebuchetCrateMenu;
	private MenuController trebuchetCrateMenuController;
	
	private TrebuchetMenu trebuchetMenu;
	private MenuController trebuchetMenuController;
	
	public ArtilleryManager(CitadelManager cm) {
		this.cm = cm;
		this.artilleryListener = new ArtilleryListener(this);
		
		registerMissileRunner(new TrebuchetMissileRunner());
		
		Bukkit.getPluginManager().registerEvents(artilleryListener, SerennoCobalt.get());
		
		Bukkit.getScheduler().runTaskTimer(SerennoCobalt.get(), artilleryRunnable = new ArtilleryRunnable(), 0L, 1L);
	}
	
	public void registerMissileRunner(ArtilleryMissileRunner runner) {
		missileRunners.put(runner.getArtilleryType(), runner);
	}
	
	public Collection<ArtilleryMissileRunner> getMissileRunners() {
		return missileRunners.values();
	}
	
	public ArtilleryMissileRunner getMissileRunner(Class<? extends Artillery> clazz) {
		return missileRunners.get(clazz);
	}
	
	public void placeArtilleryCrate(Player player, ArtilleryCrate crate, Location loc) {
		crates.put(Coords.fromLocation(loc), crate);
		crate.setLocation(loc);
		player.sendMessage(ChatColor.YELLOW + "You have placed down a " + crate.getDisplayName());
	}
	
	public ArtilleryCrate getArtilleryCrate(Location loc) {
		return crates.get(Coords.fromLocation(loc));
	}
	
	public ArtilleryWorld getArtilleryWorld(World world) {
		ArtilleryWorld artilleryWorld = artilleryWorlds.get(world.getName());
		if (artilleryWorld == null) {
			artilleryWorld = new ArtilleryWorld(world);
			artilleryWorlds.put(world.getName(), artilleryWorld);
		}
		return artilleryWorld;
	}
	
	public void assemble(Artillery artillery) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(artillery.getLocation().getWorld());
		artilleryWorld.addArtillery(artillery);
	}
	
	public void disassemble(Artillery artillery) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(artillery.getLocation().getWorld());
		artilleryWorld.deleteArtillery(artillery);
	}
	
	public Artillery getArtillery(Location loc) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(loc.getWorld());
		Set<Artillery> artilleries = artilleryWorld.getArtilleries(loc);
		if (artilleries.isEmpty()) return null;
		return artilleries.iterator().next();
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
