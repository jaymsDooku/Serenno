package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jayms.serenno.player.ui.EnemyMarkedTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
import io.jayms.serenno.player.ui.AllyTeam;
import io.jayms.serenno.player.ui.EnemyTeam;
import io.jayms.serenno.team.TeamManager;
import io.jayms.serenno.ui.UI;
import io.jayms.serenno.ui.UIManager;
import io.jayms.serenno.ui.UIScoreboard;
import io.jayms.serenno.ui.UITeam;
import io.jayms.serenno.util.PlayerTools;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

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
	public DuelTeam getTeam(ChatColor teamColor) {
		if (team1.getTeamColor() == teamColor) {
			return team1;
		}
		if (team2.getTeamColor() == teamColor) {
			return team2;
		}
		return null;
	}
	
	@Override
	public DuelStatistics getStatistics() {
		return statistics;
	}
	
	@Override
	public Location getSpawnPoint(ChatColor teamColor) {
		return getMap().getSpawnPoints().get(teamColor);
	}
	
	@Override
	public void onSpectating(SerennoPlayer spectator, SerennoPlayer toSpectate) {
		UI ui = UIManager.getUIManager().getScoreboard(spectator.getBukkitPlayer());
		UIScoreboard scoreboard = ui.getScoreboard();
		DuelTeam team = getTeam(toSpectate);
		UITeam uiTeam = new DuelUITeam(team.getTeamColor());
		scoreboard.setTeam(toSpectate.getBukkitPlayer(), uiTeam);
	}
	
	@Override
	public void spawn(SerennoPlayer player) {
		if (SerennoCrimson.get().getLobby().inLobby(player)) {
			SerennoCrimson.get().getLobby().depart(player);
		}
		if (SerennoCrimson.get().getKitEditor().inKitEditor(player)) {
			SerennoCrimson.get().getKitEditor().saveKitAndDepart(player);
		}

		DuelTeam team = getTeam(player);
		DuelTeam otherTeam = getOtherTeam(team);
		Location spawn = getSpawnPoint(team.getTeamColor());
		PlayerTools.clean(player.getBukkitPlayer());
		player.teleport(spawn);
		
		UI ui = UIManager.getUIManager().getScoreboard(player.getBukkitPlayer());
		UIScoreboard scoreboard = ui.getScoreboard();

		team.getTeam().name(team.getAlive(), AllyTeam.TEAM);
		team.getTeam().name(otherTeam.getAlive(), EnemyTeam.TEAM);
		
		Kit[] kits = player.getDuelingKits(getDuelType());
		for (int i = 0; i < kits.length; i++) {
			Kit kit = kits[i];
			
			if (kit != null) {
				ItemStack kitBook = new ItemStackBuilder(Material.BOOK, 1)
						.meta(new ItemMetaBuilder()
								.name(duelType.getDisplayName() + ChatColor.WHITE + "#" + (i+1))
								.enchant(Enchantment.DURABILITY, 1, true)
								.flag(ItemFlag.HIDE_ENCHANTS)).build();
				net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(kitBook);
				NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				compound.setInt("index", i);
				nmsStack.setTag(compound);
				player.getBukkitPlayer().getInventory().setItem(i, CraftItemStack.asBukkitCopy(nmsStack));
			}
		}
	}
	
	@Override
	protected void initGame(Consumer<Void> callback) {
		List<SerennoPlayer> playing = getPlaying();
		for (SerennoPlayer sp : playing) {
			sp.setCurrentGame(this);
			spawn(sp);
		}

		callback.accept(null);
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
	public void die(SerennoPlayer deadPlayer, DeathCause cause) {
		if (duelType == DuelType.VAULTBATTLE && cause == DeathCause.ENVIRONMENT) {
			return;
		}

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
		
		if (!losers.getAlive().isEmpty()) {
			losers.getAlive().forEach(a -> {
				getStatistics().death(a.getBukkitPlayer());
			});
		}
			
		if (!winners.getAlive().isEmpty()) {
			winners.getAlive().forEach(a -> {
				getStatistics().death(a.getBukkitPlayer());
			});
		}
		
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
			
		}.runTaskLater(SerennoCrimson.get(), 5 * 20L);
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
