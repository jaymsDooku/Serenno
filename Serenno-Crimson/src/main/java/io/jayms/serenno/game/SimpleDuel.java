package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.event.DuelPlayerStartEvent;
import io.jayms.serenno.game.menu.MatchReportMenu;
import io.jayms.serenno.game.statistics.DuelStatistics;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.Kit;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.TeamManager;
import io.jayms.serenno.util.PlayerTools;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public abstract class SimpleDuel extends AbstractGame implements Duel {

	private DuelTeam team1;
	private DuelTeam team2;
	private DuelType duelType;
	private DuelStatistics statistics = new DuelStatistics();
	
	public SimpleDuel(int id, Arena map, DuelType duelType, DuelTeam team1, DuelTeam team2) {
		super(id, map);
		this.duelType = duelType;
		this.team1 = team1;
		this.team2 = team2;
	}
	
	@Override
	public DuelTeam getTeam(SerennoPlayer player) {
		if (team1.getTeam().inTeam(player)) {
			return team1;
		}
		if (team2.getTeam().inTeam(player)) {
			return team2;
		}
		return null;
	}
	
	@Override
	public DuelTeam getOtherTeam(DuelTeam team) {
		if (team1.getTeam().equals(team.getTeam())) {
			return team2;
		}
		if (team2.getTeam().equals(team.getTeam())) {
			return team1;
		}
		return null;
	}
	
	@Override
	public DuelStatistics getStatistics() {
		return statistics;
	}
	
	@Override
	public Location getSpawnPoint(ChatColor teamColor) {
		return getMap().getSpawnPoints().get(team1.getTeamColor());
	}
	
	@Override
	protected void initGame() {
		Location spawn1 = getSpawnPoint(team1.getTeamColor());
		Location spawn2 = getSpawnPoint(team2.getTeamColor());
		team1.getTeam().clean();
		team2.getTeam().clean();
		
		team1.getTeam().teleport(spawn1);
		team2.getTeam().teleport(spawn2);
		
		List<SerennoPlayer> playing = getPlaying();
		for (SerennoPlayer sp : playing) {
			Player p = sp.getBukkitPlayer();
			p.getInventory().clear();
			
			sp.setCurrentGame(this);
			Bukkit.getPluginManager().callEvent(new DuelPlayerStartEvent(this, sp));
			
			Kit[] kits = sp.getDuelingKits(getDuelType());
			for (int i = 0; i < kits.length; i++) {
				Kit kit = kits[i];
				
				if (kit != null) {
					ItemStack kitBook = new ItemStackBuilder(Material.BOOK, 1)
							.meta(new ItemMetaBuilder()
									.name(duelType.getDisplayName() + ChatColor.WHITE + "#" + (i+1))
									.enchant(Enchantment.DURABILITY, 1, true)
									.flag(ItemFlag.HIDE_ENCHANTS)).build();
					ItemMeta meta = kitBook.getItemMeta();
					NBTItem nbtKitBook = new NBTItem(kitBook);
					nbtKitBook.setInteger("index", i);
					kitBook = nbtKitBook.getItem();
					kitBook.setItemMeta(meta);
					p.getInventory().setItem(i, kitBook);
				}
			}
		}
	}
	
	@Override
	protected void disposeGame() {
		TeamManager tm = SerennoCrimson.get().getTeamManager();
		if (team1.isTemporary()) {
			tm.disbandTeam(team1.getTeam());
		}
		if (team2.isTemporary()) {
			tm.disbandTeam(team2.getTeam());
		}
		
		List<SerennoPlayer> playing = getPlaying();
		for (SerennoPlayer player : playing) {
			if (player instanceof SerennoBot) {
				SerennoCrimson.get().getPlayerManager().killBot((SerennoBot) player);
			}
			player.setCurrentGame(null);
			SerennoCrimson.get().getLobby().sendToLobby(player);
		}
	}

	@Override
	public List<SerennoPlayer> getPlaying() {
		List<SerennoPlayer> playing = new ArrayList<>();
		playing.addAll(team1.getTeam().getAll());
		playing.addAll(team2.getTeam().getAll());
		return playing;
	}

	@Override
	public boolean isPlaying(SerennoPlayer player) {
		return getPlaying().stream().filter(p -> p.equals(player)).findFirst().isPresent();
	}

	@Override
	public int getCountdown() {
		return 5;
	}

	@Override
	public DuelTeam getTeam1() {
		return team1;
	}

	@Override
	public DuelTeam getTeam2() {
		return team2;
	}
	
	private DuelTeam winner;
	private DuelTeam loser;
	
	@Override
	public DuelTeam getWinner() {
		return winner;
	}
	
	@Override
	public DuelTeam getLoser() {
		return loser;
	}

	@Override
	public DuelType getDuelType() {
		return duelType;
	}
	
	@Override
	public void broadcast(String message, SerennoPlayer player) {
		DuelTeam duelTeam = getTeam(player);
		getPlaying().forEach(p -> {
			String playerMsg = (duelTeam.getTeam().inTeam(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
			p.sendMessage(message.replace("%p%", playerMsg));
		});
	}
	
	@Override
	public void die(SerennoPlayer deadPlayer) {
		DuelTeam duelTeam = getTeam(deadPlayer);
		if (duelTeam == null) {
			return;
		}
		
		DuelStatistics duelStats = getStatistics();
		
		duelStats.death(deadPlayer.getBukkitPlayer());
		duelTeam.die(deadPlayer);
		
		if (deadPlayer instanceof SerennoBot) {
			SerennoCrimson.get().getPlayerManager().killBot((SerennoBot) deadPlayer);
		} else {
			PlayerTools.clean(deadPlayer.getBukkitPlayer());
			startSpectating(deadPlayer, deadPlayer.getLocation());
		}
		
		int alive = duelTeam.alive();
		if (alive <= 0) { // Lost
			finish(getOtherTeam(duelTeam), duelTeam);
		}
	}
	
	@Override
	public void finish(DuelTeam winners, DuelTeam losers) {
		this.winner = winners;
		this.loser = losers;
		
		winners.getAlive().forEach(a -> {
			getStatistics().death(a.getBukkitPlayer());
		});
		
		String winnerMsg = winners.getTeam().getLeader().getName() +  (winners.getTeam().size() > 1 ? "'s team" : "") + ChatColor.YELLOW + " has won the game.";  
		winners.getTeam().sendMessage(ChatColor.DARK_GREEN + winnerMsg);
		losers.getTeam().sendMessage(ChatColor.DARK_RED + winnerMsg);
		
		FancyMessage matchReport = new FancyMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Click here for the Match Report")
				.command("/game stats " + this.getID());
		winners.getTeam().sendMessage(matchReport);
		losers.getTeam().sendMessage(matchReport);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				stop(null);
			}
			
		}.runTaskLater(SerennoCrimson.get(), 6 * 20L);
	}
	
	private MatchReportMenu reportMenu;
	private MenuController reportMenuController;
	
	@Override
	public MatchReportMenu getMatchReportMenu() {
		if (reportMenu == null) {
			this.reportMenu = new MatchReportMenu(this);
			this.reportMenuController = new MenuController(reportMenu);
		}
		return reportMenu;
	}

	@Override
	public String toString() {
		return "SimpleDuel [team1=" + team1 + ", team2=" + team2 + ", duelType=" + duelType + ", statistics="
				+ statistics + ", winner=" + winner + ", loser=" + loser + "]";
	}
	
}
