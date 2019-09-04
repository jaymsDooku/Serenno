package io.jayms.serenno.arena;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelRequest;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.region.Region;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

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
	
	Map<ChatColor, Location> getSpawnPoints();
	
	boolean requiresWorldCloning();
	
	List<DuelType> getDuelTypes();
	
	void setDirty(boolean set);
	
	boolean isDirty();
	
	public static List<IClickable> getArenaClickables(SerennoPlayer sender, SerennoPlayer playerOpp, DuelType duelType) {
		List<IClickable> mapClicks = new LinkedList<>();
		List<Arena> maps = SerennoCrimson.get().getArenaManager().listArenas(duelType);
		if (!maps.isEmpty()) {
			for (int j = 0; j < maps.size(); j++) {
				Arena map = maps.get(j);
				IClickable mapClick = new Clickable(map.getDisplayItem()) {
					
					@Override
					public void clicked(Player p) {
						ClickableInventory.forceCloseInventory(sender.getBukkitPlayer());
						DuelRequest duelRequest = new DuelRequest(sender, duelType, map);
						if (playerOpp instanceof SerennoBot) {
							SerennoCrimson.get().getGameManager().duel(playerOpp, duelRequest);
							return;
						}
						playerOpp.sendRequest(duelRequest);
					}
					
				};
				mapClicks.add(mapClick);
			}
		}
		return mapClicks;
	}
	
}
