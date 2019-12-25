package io.jayms.serenno.ui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.jayms.serenno.SerennoCommon;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class UIManager {

	private static final UIManager uiManager = new UIManager();
	
	public static UIManager getUIManager() {
		return uiManager;
	}
	
	private String defaultTitle = "Info";
	
	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}
	
	public String getDefaultTitle() {
		return defaultTitle;
	}

    private UIManager() {
    	start();
    }

    /**
     * Contains every players scoreboard information
     */
    private Map<UUID, UI> scoreboards = new ConcurrentHashMap<>();

    public UI getScoreboard(Player player) {
    	return getScoreboard(player, defaultTitle);
    }
    
    public UI getScoreboard(Player player, String title) {
    	UI board = scoreboards.get(player.getUniqueId());
        
        if (board == null) {
        	board = new UI(title);
        	scoreboards.put(player.getUniqueId(), board);
        	SerennoCommon.get().getLogger().info("Instantiated new scoreboard UI for " + player.getName());
        }
        
        return board;
    }

    /**
     * Updates all UI information for the player
     * @param player The player
     */
    public void update(Player player) {
    	UI scoreboard = getScoreboard(player);
        
    	scoreboard.getScoreboard().update();
    	StringBuilder sb = scoreboard.update(player);
    	if (sb.length() == 0) {
    		sb.append(" ");
    	}
    	player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(sb.toString()));

        scoreboard.getScoreboard().send(player);
    }
    
    private BukkitTask updateTask = null;
    
    public void start() {
    	if (updateTask != null) return;
    	
    	updateTask = Bukkit.getScheduler().runTaskTimer(SerennoCommon.get(), new Runnable() {
    		
    		@Override
    		public void run() {
    			if (scoreboards.isEmpty()) return;
    			
    			Set<UUID> users = scoreboards.keySet();
    			Set<UUID> toRemove = null;
    				
    			for (UUID user : users) {
    				Player player = Bukkit.getPlayer(user);
    				if (player == null || !player.isOnline()) {
    					if (toRemove == null) {
    						toRemove = new HashSet<>();
    					}
    					toRemove.add(user);
    					continue;
    				}
    				
    				update(player);
    			}
    			
    			if (toRemove == null) return;
    			
    			for (UUID rm : toRemove) {
    				scoreboards.remove(rm);
    			}
    		}
    		
    	}, 0L, 1L);
    }
}
