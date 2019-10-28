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
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import net.md_5.bungee.api.ChatColor;

public class ArtilleryManager {

	private CitadelManager cm;
	private ReinforcementManager rm;
	
	private Map<Reinforcement, ArtilleryCrate> crates = Maps.newConcurrentMap();
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
		this.rm = cm.getReinforcementManager();
		this.artilleryListener = new ArtilleryListener(this);
		
		World world = Bukkit.getWorld(SerennoCobalt.get().getConfigManager().getDefaultReinforcementWorld());
		newArtilleryWorld(world);
		
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
	
	public void placeArtilleryCrate(Player player, ArtilleryCrate crate, Reinforcement reinforcement) {
		crates.put(reinforcement, crate);
		crate.setReinforcement(reinforcement);
		player.sendMessage(ChatColor.YELLOW + "You have placed down a " + crate.getDisplayName());
	}
	
	public void breakArtilleryCrate(Player player, ArtilleryCrate crate) {
		crates.remove(crate.getReinforcement());
		if (player != null) {
			player.sendMessage(ChatColor.YELLOW + "You have broken " + crate.getDisplayName());
		}
	}
	
	public ArtilleryCrate getArtilleryCrate(Reinforcement reinforcement) {
		return crates.get(reinforcement);
	}
	
	public ArtilleryWorld newArtilleryWorld(World world) {
		ArtilleryWorld artilleryWorld = new ArtilleryWorld(world);
		artilleryWorlds.put(world.getName(), artilleryWorld);
		return artilleryWorld;
	}
	
	public ArtilleryWorld getArtilleryWorld(World world) {
		return artilleryWorlds.get(world.getName());
	}
	
	public void deleteArtilleryWorld(World world) {
		artilleryWorlds.remove(world.getName());
	}
	
	public void assemble(Artillery artillery) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(artillery.getLocation().getWorld());
		if (artilleryWorld == null) {
			return;
		}
		artilleryWorld.addArtillery(artillery);
	}
	
	public void disassemble(Artillery artillery) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(artillery.getLocation().getWorld());
		if (artilleryWorld == null) {
			return;
		}
		artilleryWorld.deleteArtillery(artillery);
	}
	
	public Artillery getArtillery(Location loc) {
		ArtilleryWorld artilleryWorld = getArtilleryWorld(loc.getWorld());
		if (artilleryWorld == null) {
			return null;
		}
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
