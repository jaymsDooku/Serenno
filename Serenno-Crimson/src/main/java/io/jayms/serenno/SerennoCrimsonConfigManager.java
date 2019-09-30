package io.jayms.serenno;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class SerennoCrimsonConfigManager extends CoreConfigManager {
	
	public SerennoCrimsonConfigManager(ACivMod plugin) {
		super(plugin);
	}
	
	@Override
	protected boolean parseInternal(ConfigurationSection config) {
		damagersConfigSection = config.getConfigurationSection("damagers");
		
		kitEditorArcherChest = Location.deserialize(config.getConfigurationSection("kitEditor.archerChest").getValues(false));
		kitEditorDiamondChest = Location.deserialize(config.getConfigurationSection("kitEditor.diamondChest").getValues(false));
		kitEditorSaveSignLocation = Location.deserialize(config.getConfigurationSection("kitEditor.saveSign").getValues(false));
		kitEditorLocation = Location.deserialize(config.getConfigurationSection("kitEditor.location").getValues(false));
		lobbySpawn = Location.deserialize(config.getConfigurationSection("lobby.spawn").getValues(false));
		return true;
	}
	
	private ConfigurationSection damagersConfigSection;
	
	public ConfigurationSection getDamagersConfigSection() {
		return damagersConfigSection;
	}
	
	private Location kitEditorArcherChest;
	private Location kitEditorDiamondChest;
	private Location kitEditorSaveSignLocation;
	private Location kitEditorLocation;
	
	public Location getKitEditorArcherChest() {
		return kitEditorArcherChest;
	}
	
	public Location getKitEditorDiamondChest() {
		return kitEditorDiamondChest;
	}
	
	public Location getKitEditorLocation() {
		return kitEditorLocation;
	}
	
	public Location getKitEditorSaveSignLocation() {
		return kitEditorSaveSignLocation;
	}
	
	private Location lobbySpawn;
	
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
}
