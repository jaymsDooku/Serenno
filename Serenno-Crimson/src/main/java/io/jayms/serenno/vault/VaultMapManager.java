package io.jayms.serenno.vault;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.game.vaultbattle.VaultBattle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.arena.event.ArenaLoadEvent;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.region.Region;
import io.jayms.serenno.region.RegionFlags;
import net.md_5.bungee.api.ChatColor;

public class VaultMapManager implements Listener {
	
	private Map<String, VaultMap> vaultMaps = Maps.newConcurrentMap();
	private Map<UUID, VaultBattle> vaultBattleWorlds = Maps.newConcurrentMap();
	
	private File vaultMapsFolder;
	private File vaultMapsFolderTemp;
	
	private VaultMapListener vaultMapListener;
	
	public VaultMapManager() {
		vaultMapsFolder = new File(SerennoCrimson.get().getDataFolder(), "vaultMapsFolder");
		vaultMapsFolderTemp = new File(vaultMapsFolder, "temp");
		if (!vaultMapsFolder.exists()) {
			vaultMapsFolder.mkdirs();
		}
		if (!vaultMapsFolderTemp.exists()) {
			vaultMapsFolderTemp.mkdirs();
		}
		
		vaultMapListener = new VaultMapListener(this);
		
		Bukkit.getPluginManager().registerEvents(vaultMapListener, SerennoCrimson.get());
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public File getVaultMapsFolderTemp() {
		return vaultMapsFolderTemp;
	}
	
	public File getVaultMapsFolder() {
		return vaultMapsFolder;
	}
	
	@EventHandler
	public void onArenaLoad(ArenaLoadEvent e) {
		Arena arena = e.getArena();
		String name = arena.getRegion().getName();

		if (!MongoAPI.databaseExists(name)) {
			return;
		}
		
		VaultMap vaultMap = new SimpleVaultMap(name, arena);
		vaultMap.getReinforcementWorldAsync((vm) -> {
			vaultMap.setReady(true);
			Bukkit.broadcastMessage(vaultMap.getRegion().getDisplayName() + ChatColor.YELLOW + " is ready.");
		});
		vaultMaps.put(name, vaultMap);
		e.setArena(vaultMap);
		SerennoCrimson.get().getLogger().info("Loaded vault map: " + name);
	}
	
	public VaultMap createVault(SerennoPlayer sp, String name, int radius) {
		if (isVaultMap(name)) {
			return null;
		}
		
		World world = Bukkit.getWorld(name);
		if (world == null) {
			WorldCreator creator = new WorldCreator(name);
			creator.generator(new ChunkGenerator() {
				@Override
				public byte[] generate(World world, Random random, int x, int z) {
					return new byte[32768]; //Empty byte array
				}
			});
			creator.environment(Environment.NORMAL);
			creator.generateStructures(false);
			creator.type(WorldType.FLAT);
			world = creator.createWorld();
			world.getBlockAt(0, 69, 0).setType(Material.BEDROCK);
		}
		
		Location p1 = new Location(world, -radius, 0, -radius);
		Location p2 = new Location(world, radius, 256, radius);
		Region worldRegion = SerennoCrimson.get().getRegionManager().createRegion(sp.getBukkitPlayer(), name, p1, p2);
		
		if (worldRegion == null) {
			sp.sendMessage(ChatColor.RED + "Failed to create region.");
			return null;
		}
		
		worldRegion.getFlags().add(RegionFlags.BLOCK_BREAK);
		worldRegion.getFlags().add(RegionFlags.BLOCK_PLACE);
		worldRegion.getFlags().add(RegionFlags.DAMAGE_LOSS);
		worldRegion.getFlags().add(RegionFlags.HUNGER_LOSS);
		worldRegion.getFlags().add(RegionFlags.PVP);
		worldRegion.getFlags().add(RegionFlags.PVE);
		
		Arena worldArena = SerennoCrimson.get().getArenaManager().createArena(sp.getBukkitPlayer(), worldRegion.getName());
		
		if (worldArena == null) {
			sp.sendMessage(ChatColor.RED + "Failed to create arena.");
			return null;
		}

		VaultMap vaultMap = new SimpleVaultMap(world.getName(), worldArena);
		SerennoCrimson.get().getArenaManager().replaceArena(vaultMap);
		
		VaultMapDatabase database = vaultMap.getDatabase();

		ReinforcementBlueprint stoneBlueprint = ReinforcementBlueprint.builder()
				.name("stone")
				.displayName(ChatColor.GRAY + "Stone")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 5)
				.damageCooldown(0)
				.maturationTime(1000 * 60 * 1)
				.maturationScale(2)
				.itemStack(new ItemStackBuilder(Material.STONE, 1)
						.meta(new ItemMetaBuilder()
								.name("Stone")).build())
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(50)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint ironBlueprint = ReinforcementBlueprint.builder()
				.name("iron")
				.displayName(ChatColor.DARK_GRAY + "Iron")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 9)
				.damageCooldown(0)
				.itemStack(new ItemStackBuilder(Material.IRON_INGOT, 1)
						.meta(new ItemMetaBuilder()
								.name("Iron Ingot")).build())
				.maturationTime(1000 * 60 * 3)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(200)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint diamondBlueprint = ReinforcementBlueprint.builder()
				.name("diamond")
				.displayName(ChatColor.AQUA + "Diamond")
				.defaultDamage(1)
				.acidTime(1000 * 60 * 27)
				.damageCooldown(0)
				.itemStack(new ItemStackBuilder(Material.DIAMOND, 1)
						.meta(new ItemMetaBuilder()
								.name("Diamond")).build())
				.maturationTime(1000 * 60 * 9)
				.maturationScale(2)
				.regenRate(new RegenRate(1, 60000))
				.maxHealth(400)
				.unreinforceableMaterials(Arrays.asList(Material.WEB))
				.build();
		ReinforcementBlueprint diamondBastionBlueprint = ReinforcementBlueprint.builder()
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
				.build();
		
		BastionBlueprint bastionBlueprint = BastionBlueprint.builder()
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
				.build();
		database.getBastionBlueprintSource().create(bastionBlueprint);
		
		vaultMaps.put(worldRegion.getName(), vaultMap);
		return vaultMap;
	}
	
	public void deleteVaultMap(VaultMap vaultMap) {
		vaultMaps.remove(vaultMap.getArena().getName());
		vaultMap.delete();
	}
	
	public boolean isVaultMap(String name) {
		return vaultMaps.containsKey(name);
	}
	
	public VaultMap getVaultMap(String name) {
		VaultMap vm = vaultMaps.get(name);
		return vm;
	}

	public VaultMap getVaultMapFromOriginalWorld(World originalWorld) {
		return vaultMaps.values().stream().filter(m -> m.getOriginalWorldName().equals(originalWorld.getName())).findFirst().orElse(null);
	}
	
	public VaultMap getVaultMap(World world) {
		return vaultMaps.values().stream().filter(m -> m.isActiveWorld(world)).findFirst().orElse(null);
	}

	public void putVaultBattleWorld(World world, VaultBattle vaultBattle) {
		vaultBattleWorlds.put(world.getUID(), vaultBattle);
	}

	public void removeVaultBattleWorld(World world) {
		vaultBattleWorlds.remove(world.getUID());
	}

	public VaultBattle getVaultBattleFromWorld(World world) {
		return vaultBattleWorlds.get(world.getUID());
	}
	
	public Map<UUID, VaultBattle> getVaultBattleWorlds() {
		return vaultBattleWorlds;
	}
	
	public Collection<VaultMap> listVaults() {
		return vaultMaps.values();
	}

	public void dispose() {
		for (VaultMap vm : vaultMaps.values()) {
			vm.dispose();
		}
	}

}
