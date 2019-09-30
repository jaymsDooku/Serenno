package io.jayms.serenno.arena;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.region.Region;
import net.md_5.bungee.api.ChatColor;

public interface Arena {
	
	String getName();
	
	void setDescription(String set);
	
	String getDescription();
	
	void setCreators(String set);
	
	String getCreators();
	
	void setDisplayItem(ItemStack set);
	
	ItemStack getDisplayItem();
	
	Region getRegion();
	
	void addSpawnPoint(ChatColor teamColor, Location spawn);
	
	void removeSpawnPoint(ChatColor teamColor);
	
	Map<ChatColor, Location> getSpawnPoints();
	
	boolean requiresWorldCloning();
	
	void addDuelType(DuelType type);
	
	void removeDuelType(DuelType type);
	
	Set<DuelType> getDuelTypes();
	
	void setDirty(boolean set);
	
	boolean isDirty();
	
}
