package io.jayms.serenno;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import io.jayms.serenno.arena.ArenaManager;
import io.jayms.serenno.bot.Bot;
import io.jayms.serenno.bot.BotTrait;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.game.GameManager;
import io.jayms.serenno.game.TeamManager;
import io.jayms.serenno.lobby.Lobby;
import io.jayms.serenno.player.SerennoPlayerManager;
import io.jayms.serenno.region.RegionManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import vg.civcraft.mc.civmodcore.ACivMod;

public class SerennoCrimson extends ACivMod {

	private static SerennoCrimson instance;
	
	public static SerennoCrimson get() {
		return instance;
	}
	
	private SerennoCrimsonConfigManager configManager;
	
	public SerennoCrimsonConfigManager getConfigManager() {
		return configManager;
	}
	
	private RegionManager regionManager;
	
	public RegionManager getRegionManager() {
		return regionManager;
	}
	
	private ArenaManager arenaManager;
	
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	private SerennoPlayerManager playerManager;
	
	public SerennoPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	private Lobby lobby;
	
	public Lobby getLobby() {
		return lobby;
	}
	
	private GameManager gameManager;
	
	public GameManager getGameManager() {
		return gameManager;
	}
	
	private TeamManager teamManager;
	
	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	private File schematicsFolder;
	
	public File getSchematicsFolder() {
		return schematicsFolder;
	}
	
	@Override
	public String getPluginName() {
		return "Serenno-Crimson";
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BotTrait.class).withName("serenno-bot"));
		
		configManager = new SerennoCrimsonConfigManager(this);
		if (!configManager.parse()) {
			getLogger().severe("Errors in config file, shutting down");
			Bukkit.shutdown();
			return;
		}
		
		schematicsFolder = new File(this.getDataFolder(), "schematics");
		if (!schematicsFolder.exists()) {
			if (schematicsFolder.mkdir()) {
				getLogger().info("Created Serenno-Crimson schematics folder.");
			}
		}
		
		playerManager = new SerennoPlayerManager();
		regionManager = new RegionManager();
		arenaManager = new ArenaManager();
		teamManager = new TeamManager();
		gameManager = new GameManager();
		lobby = new Lobby();
		
		Bukkit.getPluginManager().registerEvents(new SerennoListener(), this);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		Bot.clearAndKillAllNPCs();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(online, null));
		}
		
		regionManager.saveAll();
		arenaManager.saveAll();
		
		MongoAPI.close();
	}

}
