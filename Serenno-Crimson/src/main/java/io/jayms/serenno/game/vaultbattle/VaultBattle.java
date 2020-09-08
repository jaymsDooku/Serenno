package io.jayms.serenno.game.vaultbattle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.jayms.serenno.game.DeathCause;
import io.jayms.serenno.model.citadel.artillery.ArtilleryWorld;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.citadel.snitch.SnitchWorld;
import io.jayms.serenno.model.finance.company.Company;
import io.jayms.serenno.model.finance.company.ServerCompany;
import io.jayms.serenno.vault.Core;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.SimpleDuel;
import io.jayms.serenno.game.vaultbattle.pearling.SpectatorPearlManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapDatabase;
import net.md_5.bungee.api.ChatColor;

public class VaultBattle extends SimpleDuel {

	private SpectatorPearlManager pearlManager;
	private World activeWorld;

	private ReinforcementWorld reinforcementWorld;
	private BastionWorld bastionWorld;
	private SnitchWorld snitchWorld;
	private ArtilleryWorld artilleryWorld;

	private Map<String, ReinforcementBlueprint> reinforcementBlueprintMap;

	private Map<Reinforcement, Core> coreSource;
	private Map<String, Company> companySource;
	private Map<String, Group> groupSource;
	private BiMap<ChatColor, String> groupColours;

	private double scaling;
	
	public VaultBattle(int id, VaultMap map, DuelTeam team1, DuelTeam team2, double scaling) {
		super(id, map, DuelType.VAULTBATTLE, team1, team2);
		pearlManager = new SpectatorPearlManager(this);
		this.scaling = scaling;
	}
	
	public SpectatorPearlManager getPearlManager() {
		return pearlManager;
	}

	public Map<Reinforcement, Core> getCoreSource() {
		return coreSource;
	}

	public Map<String, Group> getGroupSource() {
		return groupSource;
	}

	@Override
	protected void initGame(Consumer<Void> callback) {
		VaultMap vaultMap = getVaultMap();
		VaultMapDatabase database = vaultMap.getDatabase();
		List<SerennoPlayer> playing = getPlaying();
		for (SerennoPlayer player : playing) {
			if (!database.isAllowed(player.getBukkitPlayer())) {
				broadcast(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " isn't allowed to play on this vault map - game cancelled.");
				return;
			}
		}

		companySource = new HashMap<>();
		groupSource = new HashMap<>();
		VaultMapDatabase.initializeGroups(companySource, groupSource);
		groupColours = database.getGroupColours();

		addToGroup(getTeam1());
		addToGroup(getTeam2());

		activeWorld = vaultMap.activateWorld();
		SerennoCrimson.get().getVaultMapManager().putVaultBattleWorld(activeWorld, this);

		new BukkitRunnable() {

			@Override
			public void run() {
				reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().cloneReinforcementWorld(vaultMap.getReinforcementWorld(), activeWorld, groupSource, scaling);
				bastionWorld = SerennoCobalt.get().getCitadelManager().getBastionManager().newBastionWorld(activeWorld, database.getBastionSource());
				snitchWorld = SerennoCobalt.get().getCitadelManager().getSnitchManager().newSnitchWorld(activeWorld, database.getSnitchSource());
				artilleryWorld = SerennoCobalt.get().getCitadelManager().getArtilleryManager().newArtilleryWorld(activeWorld);



				coreSource = new ConcurrentHashMap<>();
				for (Core core : database.getCoreSource().getAll()) {
					coreSource.put(core.getReinforcement(reinforcementWorld), core);
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						VaultBattle.super.initGame(callback);
					}

				}.runTask(SerennoCrimson.get());
			}

		}.runTaskAsynchronously(SerennoCrimson.get());
	}
	
	private void addToGroup(DuelTeam team) {
		ChatColor teamColour = team.getTeamColor();
		String groupName = groupColours.get(team.getTeamColor());
		Group group = groupSource.get(groupName.toLowerCase());
		for (SerennoPlayer player : team.getAlive()) {
			player.sendMessage(ChatColor.YELLOW + "You are fighting for the " + team.getTeamColor() + group.getName());
			CitadelPlayer citadelPlayer = SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player.getBukkitPlayer());
			citadelPlayer.setDefaultGroup(group);
			group.addMember(player.getBukkitPlayer());
		}
	}
	
	@Override
	public void broadcast(String message) {
		super.broadcast(ChatColor.BLACK + "[" + ChatColor.DARK_RED + "VaultBattle" + ChatColor.BLACK + "]: " + ChatColor.RESET + message);
	}
	
	@Override
	protected void disposeGame() {
		super.disposeGame();
		
		VaultMap vaultMap = getVaultMap();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				vaultMap.deactivateWorld(activeWorld);
				SerennoCrimson.get().getVaultMapManager().removeVaultBattleWorld(activeWorld);
			}
			
		}.runTaskLater(SerennoCrimson.get(), 20L);
	}
	
	public VaultMap getVaultMap() {
		return (VaultMap) getMap();
	}

	@Override
	public Location getSpawnPoint(ChatColor teamColor) {
		Location spawnPoint = super.getSpawnPoint(teamColor);
		spawnPoint.setWorld(activeWorld);
		return spawnPoint;
	}
	
	@Override
	public void die(SerennoPlayer deadPlayer, DeathCause cause) {
		cause = pearlManager.pearl(deadPlayer);
		super.die(deadPlayer, cause);
	}
	
}
