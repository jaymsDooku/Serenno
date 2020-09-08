package io.jayms.serenno.manager;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import io.jayms.serenno.listener.MechanicsListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.listener.citadel.CitadelBlockListener;
import io.jayms.serenno.listener.citadel.CitadelEntityListener;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import net.md_5.bungee.api.ChatColor;

public class CitadelManager implements Listener {

	private Map<UUID, CitadelPlayer> citadelPlayers = Maps.newConcurrentMap();
	private BastionManager bastionManager;
	private ReinforcementManager reinforcementManager;
	private ArtilleryManager artilleryManager;
	private SnitchManager snitchManager;
	
	private CitadelBlockListener blockListener;
	private CitadelEntityListener entityListener;
	private MechanicsListener mechanicsListener;
	
	public CitadelManager() {
		reinforcementManager = new ReinforcementManager(this, null);
		bastionManager = new BastionManager(this, reinforcementManager, null);
		snitchManager = new SnitchManager(reinforcementManager, null);
		artilleryManager = new ArtilleryManager(this);
		
		reinforcementManager.registerReinforcementBlueprint(ReinforcementBlueprint.builder()
				.name("stone")
				.displayName(ChatColor.GRAY + "Stone")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 5)
				.damageCooldown(0)
				.maturationTime(1000 * 60 * 1)
				.maturationScale(2)
				.itemStack(new ItemStack(Material.STONE, 1))
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(50)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build());
		reinforcementManager.registerReinforcementBlueprint(ReinforcementBlueprint.builder()
				.name("iron")
				.displayName(ChatColor.DARK_GRAY + "Iron")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 9)
				.damageCooldown(0)
				.itemStack(new ItemStack(Material.IRON_INGOT, 1))
				.maturationTime(1000 * 60 * 3)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(200)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build());
		reinforcementManager.registerReinforcementBlueprint(ReinforcementBlueprint.builder()
				.name("diamond")
				.displayName(ChatColor.AQUA + "Diamond")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 27)
				.damageCooldown(0)
				.itemStack(new ItemStack(Material.DIAMOND, 1))
				.maturationTime(1000 * 60 * 9)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(400)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build());
		reinforcementManager.registerReinforcementBlueprint(ReinforcementBlueprint.builder()
				.name("diamond-bastion")
				.displayName(ChatColor.AQUA + "Diamond Bastion")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 30)
				.damageCooldown(2500)
				.maturationTime(1000 * 60 * 30)
				.maturationScale(2)
				.itemStack(new ItemStackBuilder(Material.DIAMOND, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.AQUA + "Diamond Bastion")).build())
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(400)
				.reinforceableMaterials(Arrays.asList(Material.SPONGE))
				.build());
		
		bastionManager.registerBastionBlueprint(BastionBlueprint.builder()
				.name("vault")
				.displayName(ChatColor.DARK_RED + "Vault Bastion")
				.itemStack(new ItemStackBuilder(Material.SPONGE, 1)
						.meta(new ItemMetaBuilder()
								.name(ChatColor.DARK_RED + "Vault Bastion"))
						.build())
				.pearlConfig(PearlConfig.builder()
						.consumeOnBlock(false)
						.block(true)
						.blockMidAir(false)
						.damage(2)
						.build())
				.requiresMaturity(false)
				.shape(BastionShape.SQUARE)
				.radius(10)
				.build());
		
		blockListener = new CitadelBlockListener(this, reinforcementManager, bastionManager);
		entityListener = new CitadelEntityListener(this, reinforcementManager, bastionManager);
		mechanicsListener = new MechanicsListener();
		Bukkit.getPluginManager().registerEvents(blockListener, SerennoCobalt.get());
		Bukkit.getPluginManager().registerEvents(entityListener, SerennoCobalt.get());
		Bukkit.getPluginManager().registerEvents(mechanicsListener, SerennoCobalt.get());
		Bukkit.getPluginManager().registerEvents(this, SerennoCobalt.get());
	}
	
	public ArtilleryManager getArtilleryManager() {
		return artilleryManager;
	}
	
	public BastionManager getBastionManager() {
		return bastionManager;
	}
	
	public ReinforcementManager getReinforcementManager() {
		return reinforcementManager;
	}
	
	public SnitchManager getSnitchManager() {
		return snitchManager;
	}
	
	public CitadelPlayer getCitadelPlayer(Player player) {
		CitadelPlayer citadelPlayer = citadelPlayers.get(player.getUniqueId());
		if (citadelPlayer == null) {
			citadelPlayer = new CitadelPlayer(player);
			citadelPlayers.put(player.getUniqueId(), citadelPlayer);
		}
		return citadelPlayer;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		citadelPlayers.remove(player.getUniqueId());
	}
	
}
