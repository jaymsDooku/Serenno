package io.jayms.serenno.player;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.bot.Bot;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.manager.PlayerManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import vg.civcraft.mc.civmodcore.ui.ActionBarHandler;
import vg.civcraft.mc.civmodcore.ui.UI;
import vg.civcraft.mc.civmodcore.ui.UIHandler;
import vg.civcraft.mc.civmodcore.ui.UIManager;
import vg.civcraft.mc.civmodcore.ui.UIScoreboard;

public class SerennoPlayerManager extends PlayerManager<SerennoPlayer> {

	private Map<Integer, SerennoBot> bots = new HashMap<>();
	
	public SerennoPlayerManager() {
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public void killBot(SerennoBot bot) {
		NPC npc = bot.getNpc();
		//bots.remove(npc.getId());
		Bot.killAndRemoveNPC(npc);
	}
	
	public SerennoBot getBot(NPC npc) {
		SerennoBot bot = bots.get(npc.getId());
		if (bot == null) {
			bot = new SerennoBot(npc);
			bots.put(npc.getId(), bot);
		}
		return bot;
	}
	
	public Document document(SerennoPlayer player) {
		Document doc = new Document();
		doc.append("uuid", player.getBukkitPlayer().getUniqueId());
		
		Map<DuelType, Kit[]> kits = player.getKits();
		Document kitsDoc = new Document();
		for (Entry<DuelType, Kit[]> kitsEn : kits.entrySet()) {
			DuelType duelType = kitsEn.getKey();
			Kit[] kitsArr = kitsEn.getValue();
			List<String> kitsStr = Arrays.stream(kitsArr).filter(k -> k != null).map(k -> k.toBase64()).collect(Collectors.toList());
			kitsDoc.append(duelType.toString(), kitsStr);
		}
		doc.append("kits", kitsDoc);
		
		return doc;
	}
	
	public void savePlayer(Player player) {
		SerennoPlayer serennoPlayer = get(player);
		if (serennoPlayer == null) {
			return;
		}
		
		savePlayer(serennoPlayer, true);
	}
	
	public void savePlayer(SerennoPlayer player, boolean async) {
		if (player instanceof SerennoBot) {
			return;
		}
		if (!MongoAPI.isConnected()) {
			return;
		}
		if (!player.isDirty()) {
			return;
		}
		
		if (async) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				savePlayer(player);
			}
			
		}.runTaskAsynchronously(SerennoCrimson.get());
		} else {
			savePlayer(player);
		}
	}
	
	public void saveAll() {
		for (SerennoPlayer sp : getPlayers().values()) {
			savePlayer(sp, false);
		}
	}
		
	private void savePlayer(SerennoPlayer player) {
		MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("player");
		FindIterable<Document> query = collection.find(Filters.eq("uuid", player.getBukkitPlayer().getUniqueId()));
		Document document = query.first();
		
		Document newDoc = document(player);
		
		if (document != null) {
			collection.replaceOne(document, newDoc);
		} else {
			collection.insertOne(newDoc);
		}
		player.setDirty(false);
		SerennoCrimson.get().getLogger().info("Saved player: " + player.getBukkitPlayer().getName());
	}
	
	private org.bukkit.scoreboard.Team getTeam(Scoreboard mcBoard, String t, boolean friendly, String prefix) {
		org.bukkit.scoreboard.Team team;
		try {
            team = mcBoard.registerNewTeam(t);
            team.setCanSeeFriendlyInvisibles(friendly);
            team.setPrefix(prefix);
        } catch (IllegalArgumentException e) {
            team = mcBoard.getTeam(t);
        }
		return team;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		UI ui = UIManager.getUIManager().getScoreboard(p);
		ui.getScoreboard().setTitle(ChatColor.DARK_RED + "Serenno " + ChatColor.DARK_RED + "[" + ChatColor.RED + ChatColor.ITALIC + "Crimson" + ChatColor.RESET + ChatColor.DARK_RED + "]");
		ui.getUIHandlers().add(new UIHandler() {
			
			@Override
			public void handle(Player player, UIScoreboard board) {
				SerennoPlayer sp = get(player);
				Scoreboard sb = board.getScoreboard();
				org.bukkit.scoreboard.Team norm, ally, enemy, tagged;
				
				norm = getTeam(sb, "n", true, ChatColor.WHITE + "");
				ally = getTeam(sb, "t", true, ChatColor.GREEN + "");
				enemy = getTeam(sb, "e", true, ChatColor.RED + "");
				tagged = getTeam(sb, "tagged", true, ChatColor.GOLD + "");
				
				board.add(ChatColor.STRIKETHROUGH + "--------------" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "----", 1);
				board.add(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "-------------" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "-----", 15);
				
				if (SerennoCrimson.get().getLobby().inLobby(sp)) {
					board.add(ChatColor.RED + "Online: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(), 3);
				} else {
					board.remove(3, "");
				}
				
				Duel duel = sp.getDuel();
				if (duel != null) {
					DuelTeam team = duel.getTeam(sp);
					
					if (team != null && team.getTeam().size() > 1) {
						board.add(ChatColor.RED + "Team: " + ChatColor.WHITE + team.getTeam().getLeader().getName() + "'s Team", 2);
					} else {
						board.remove(2, "");
					}
					
					DuelTeam otherTeam = duel.getOtherTeam(team);
					board.add(ChatColor.RED + "Opponent: " + ChatColor.WHITE + otherTeam.getTeam().getLeader().getName(), 14);
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						SerennoPlayer sOnline = get(online);
						String name = online.getName();
						SerennoPlayer onlineSp = get(online);
						Duel onlineDuel = onlineSp.getDuel();
						if (duel != null) {
							if (norm.hasEntry(name)) {
								norm.removeEntry(name);
							}
							if (onlineDuel != null && onlineDuel.isPlaying(sOnline)) {
								if (team.getTeam().inTeam(sOnline)) {
									if (!ally.hasEntry(name)) {
										ally.addEntry(name);
									}
								} else {
									if (!enemy.hasEntry(name)) {
										enemy.addEntry(name);
									}
								}
							}
						} else {
							if (ally.hasEntry(name)) {
								ally.removeEntry(name);
							}
							if (enemy.hasEntry(name)) {
								enemy.removeEntry(name);
							}
							if (tagged.hasEntry(name)) {
								tagged.removeEntry(name);
							}
							if (!norm.hasEntry(name)) {
								norm.addEntry(name);
							}
						}
					}
				} else {
					board.remove(14, "");
					board.remove(2, "");
				}
			}
		});
		ui.getActionBarHandlers().add(new ActionBarHandler() {
			
			@Override
			public StringBuilder handle(Player player, StringBuilder sb) {
				return sb;
			}
		});
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		savePlayer(get(p));
	}

	@Override
	protected Function<Player, SerennoPlayer> getPlayerInstantiator() {
		return (player) -> {
			if (CitizensAPI.getNPCRegistry().isNPC(player)) {
				return getBot(CitizensAPI.getNPCRegistry().getNPC(player));
			}
			
			if (isPlayer(player)) {
				return get(player);
			}
			
			if (!MongoAPI.isConnected()) {
				return null;
			}
			
			MongoCollection<Document> collection = SerennoCommon.get().getDBManager().getCollection("player");
	        FindIterable<Document> query = collection.find(Filters.eq("uuid", player.getUniqueId().toString()));
	        Document document = query.first();
	        
	        SerennoPlayer serennoPlayer = new SerennoPlayer(player); 
	        
	        if (document != null) {
		        Map<DuelType, Kit[]> kits = serennoPlayer.getKits();
		        Document kitDoc = (Document) document.get("kits");
		        for (Entry<String, Object> kitEn : kitDoc.entrySet()) {
		        	Kit[] kitArr = new Kit[9];
		        	List<String> kitsStr = kitDoc.getList(kitEn.getKey(), String.class);
		        	for (int i = 0; i < kitsStr.size(); i++) {
		        		try {
							kitArr[i] = Kit.fromBase64(kitsStr.get(i));
						} catch (IOException e) {
							e.printStackTrace();
						}
		        	}
		        	kits.put(DuelType.valueOf(kitEn.getKey()), kitArr);
		        }
	        }
	        
	        SerennoCrimson.get().getLogger().info("Loaded player: " + player.getName());
	        return serennoPlayer;
		};
	}
	
}
