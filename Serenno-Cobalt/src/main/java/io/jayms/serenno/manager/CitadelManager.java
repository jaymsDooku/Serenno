package io.jayms.serenno.manager;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.listener.citadel.CitadelBlockListener;
import io.jayms.serenno.listener.citadel.CitadelChunkListener;
import io.jayms.serenno.listener.citadel.CitadelEntityListener;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import net.md_5.bungee.api.ChatColor;

public class CitadelManager {

	private Map<UUID, CitadelPlayer> citadelPlayers = Maps.newConcurrentMap();
	private BastionManager bastionManager;
	private ReinforcementManager reinforcementManager;
	private ArtilleryManager artilleryManager;
	private SnitchManager snitchManager;
	
	private CitadelBlockListener blockListener;
	private CitadelEntityListener entityListener;
	private CitadelChunkListener chunkListener;
	
	public CitadelManager() {
		reinforcementManager = new ReinforcementManager(this, null);
		bastionManager = new BastionManager(reinforcementManager, null);
		snitchManager = new SnitchManager(reinforcementManager, null);
		artilleryManager = new ArtilleryManager(this);
		
		reinforcementManager.registerReinforcementBlueprint(ReinforcementBlueprint.builder()
				.name("stone")
				.displayName(ChatColor.GRAY + "Stone")
				.defaultDamage(1)
				.acidTime(20000)
				.damageCooldown(0)
				.itemStack(new ItemStack(Material.STONE, 1))
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(50)
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
		chunkListener = new CitadelChunkListener(reinforcementManager);
		Bukkit.getPluginManager().registerEvents(blockListener, SerennoCobalt.get());
		Bukkit.getPluginManager().registerEvents(entityListener, SerennoCobalt.get());
		Bukkit.getPluginManager().registerEvents(chunkListener, SerennoCobalt.get());
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
	
}
