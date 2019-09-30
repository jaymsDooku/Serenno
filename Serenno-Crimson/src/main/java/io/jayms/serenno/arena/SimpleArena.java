package io.jayms.serenno.arena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.region.Region;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;

public class SimpleArena implements Arena {

	private Region region;
	private String description;
	private String creators;
	private ItemStack displayItem;
	private Map<ChatColor, Location> spawnPoints;
	private Set<DuelType> duelTypes;
	private boolean dirty = true;
	
	public SimpleArena(Region region) {
		this(region, "", "", new ItemStack(Material.STONE, 1), new HashMap<>(), new HashSet<>());
	}
	
	public SimpleArena(Region region, String description, String creators, ItemStack displayItem, Map<ChatColor, Location> spawnPoints, Set<DuelType> duelTypes) {
		this.region = region;
		this.description = description;
		this.creators = creators;
		this.displayItem = displayItem;
		this.duelTypes = duelTypes;
		this.spawnPoints = spawnPoints;
	}
	
	@Override
	public void setDirty(boolean set) {
		dirty = set;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public String getName() {
		return region.getDisplayName();
	}
	
	@Override
	public void setDescription(String set) {
		description = set;
		setDirty(true);
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public void setCreators(String set) {
		creators = set;
		setDirty(true);
	}

	@Override
	public String getCreators() {
		return creators;
	}
	
	@Override
	public ItemStack getDisplayItem() {
		ISUtils.setName(displayItem, getName());
		ISUtils.setLore(displayItem, description);
		return displayItem;
	}
	
	@Override
	public void setDisplayItem(ItemStack set) {
		displayItem = set;
		setDirty(true);
	}

	@Override
	public Region getRegion() {
		return region;
	}
	
	@Override
	public void addSpawnPoint(ChatColor teamColor, Location spawn) {
		spawnPoints.put(teamColor, spawn);
		setDirty(true);
	}
	
	@Override
	public void removeSpawnPoint(ChatColor teamColor) {
		spawnPoints.remove(teamColor);
		setDirty(true);
	}

	@Override
	public Map<ChatColor, Location> getSpawnPoints() {
		return spawnPoints;
	}

	@Override
	public boolean requiresWorldCloning() {
		return getDuelTypes().contains(DuelType.VAULTBATTLE);
	}
	
	@Override
	public void addDuelType(DuelType type) {
		duelTypes.add(type);
		setDirty(true);
	}
	
	@Override
	public void removeDuelType(DuelType type) {
		duelTypes.remove(type);
		setDirty(true);
	}
	
	@Override
	public Set<DuelType> getDuelTypes() {
		return duelTypes;
	}


}
