package io.jayms.serenno.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.ReinforcementMode;
import io.jayms.serenno.model.citadel.ReinforcementMode.ReinforceMethod;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupPermissions;
import io.jayms.serenno.util.ChunkCache;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;
import io.jayms.serenno.util.LocationTools;

public class ReinforcementManager {
	
	private ReinforcementDataSource dataSource;
	private Map<String, ReinforcementWorld> reinforcementWorlds = Maps.newConcurrentMap();
	private Map<ItemStackKey, ReinforcementBlueprint> reinforcementBlueprints = Maps.newConcurrentMap();
	
	private CitadelManager cm;
	private GroupManager gm = SerennoCobalt.get().getGroupManager();
	
	public ReinforcementManager(CitadelManager cm, ReinforcementDataSource dataSource) {
		this.cm = cm;
		this.dataSource = dataSource;
		
		World world = Bukkit.getWorld(SerennoCobalt.get().getConfigManager().getDefaultReinforcementWorld());
		newReinforcementWorld(world, dataSource);
	}
	
	public void registerReinforcementBlueprint(ReinforcementBlueprint blueprint) {
		reinforcementBlueprints.put(new ItemStackKey(blueprint.getItemStack()), blueprint);
	}
	
	public ReinforcementBlueprint getReinforcementBlueprint(String name) {
		return reinforcementBlueprints.values().stream()
				.filter(r -> r.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
	
	public ReinforcementBlueprint getReinforcementBlueprint(ItemStack it) {
		return reinforcementBlueprints.get(new ItemStackKey(it));
	}
	
	public Collection<ReinforcementBlueprint> getReinforcementBlueprints() {
		return reinforcementBlueprints.values();
	}
	
	public ReinforcementWorld newReinforcementWorld(World world, ReinforcementDataSource dataSource) {
		ReinforcementWorld reinWorld = new ReinforcementWorld(world, dataSource);
		reinforcementWorlds.put(world.getName(), reinWorld);
		
		for (Chunk chunk : world.getLoadedChunks()) {
			reinWorld.loadChunkData(chunk);
		}
		return reinWorld;
	}
	
	public void deleteReinforcementWorld(World world) {
		ReinforcementWorld reinWorld = reinforcementWorlds.remove(world.getName());
		reinWorld.unloadAll();
	}
	
	public void removeReinforcementWorld(World world) {
		reinforcementWorlds.remove(world.getName());
	}
	
	public ReinforcementWorld getReinforcementWorld(World world) {
		return reinforcementWorlds.get(world.getName());
	}
	
	public Reinforcement getDirectReinforcement(Block block) {
		World world = block.getWorld();
		ReinforcementWorld reinWorld = getReinforcementWorld(world);
		if (reinWorld == null) return null;
		ChunkCache<Reinforcement> reinChunkCache = reinWorld.getChunkCache(ChunkCoord.fromBlock(block));
		return reinChunkCache.get(block);
	}
	
	public Reinforcement getReinforcement(Block block) {
		Reinforcement directReinforcement = getDirectReinforcement(block);
		if (directReinforcement != null) {
			return directReinforcement;
		}
		Block responsible = getResponsibleBlock(block);
		if (responsible == null) {
			return null;
		}
		return getDirectReinforcement(responsible);
	}
	
	public Set<Reinforcement> getReinforcementsInArea(Location l1, Location l2) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return new HashSet<>();
		}
		
		World world = l1.getWorld();
		ReinforcementWorld reinWorld = getReinforcementWorld(world);
		Set<Reinforcement> result = new HashSet<>();
		
		int minChunkX = l1.getChunk().getX();
		int minChunkZ = l1.getChunk().getZ();
		
		int maxChunkX = l2.getChunk().getX();
		int maxChunkZ = l2.getChunk().getZ();
		
		System.out.println("minChunkX: " + minChunkX);
		System.out.println("minChunkZ: " + minChunkZ);
		System.out.println("maxChunkX: " + maxChunkX);
		System.out.println("maxChunkZ: " + maxChunkZ);
		
		for (int x = minChunkX; x <= maxChunkX + 1; x++) {
			for (int z = minChunkZ; z <= maxChunkZ + 1; z++) {
				ChunkCache<Reinforcement> reinforcements = reinWorld.getChunkCache(new ChunkCoord(x, z));
				for (Reinforcement rein : reinforcements.getAll()) {
					System.out.println("rein: " + rein);
					if (LocationTools.isBetween(l1, l2, rein.getLocation())) {
						System.out.println("IN BETWEEN");
						result.add(rein);
					}
				}
			}
		}
		return result;
	}
	
	public boolean placeBlock(CitadelPlayer cp, Block block, ItemStack item) {
		ReinforcementMode reinMode = cp.getReinforcementMode();
		if (reinMode == null || reinMode.getMethod() != ReinforceMethod.FORTIFY) {
			return false;
		}
		
		return reinforceBlock(cp, block, item);
	}
	
	public boolean reinforceBlock(CitadelPlayer cp, Block block, ItemStack item) {
		ReinforcementMode reinMode = cp.getReinforcementMode();
		
		ReinforcementBlueprint blueprint = reinMode.getReinforcementBlueprint();
		Group group = reinMode.getGroupToReinforce();
		if (!group.isAuthorized(cp.getBukkitPlayer(), GroupPermissions.REINFORCEMENT_FORTIFICATION)) {
			return true;
		}
		
		reinforceBlock(cp.getBukkitPlayer(), block, item, blueprint, group);
		return false;
	}
	
	public void reinforceBlock(Player placer, Block block, ItemStack item, ReinforcementBlueprint blueprint, Group group) {
		Reinforcement reinforcement = Reinforcement.builder()
				.id(UUID.randomUUID())
				.health(blueprint.getMaxHealth())
				.blueprint(blueprint)
				.creationTime(System.currentTimeMillis())
				.placer(placer)
				.loc(block.getLocation())
				.inMemory(true)
				.group(group)
				.build();
		
		ReinforcementCreationEvent event = placer != null ? new PlayerReinforcementCreationEvent(placer, item, reinforcement, dataSource) 
				: new ReinforcementCreationEvent(reinforcement, dataSource);
		Bukkit.getPluginManager().callEvent(event);
		
		ReinforcementWorld reinWorld = getReinforcementWorld(block.getWorld());
		ChunkCache<Reinforcement> reinChunkCache = reinWorld.getChunkCache(ChunkCoord.fromBlock(block));
		reinChunkCache.put(block.getX(), block.getY(), block.getZ(), reinforcement);
		
		block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 1);
	}
	
	// false = allow break
	// true = dont allow break
	public boolean breakBlock(CitadelPlayer cp, Block block) {
		Reinforcement reinforcement = getReinforcement(block);
		
		if (reinforcement == null) {
			return false;
		}
		
		Group group = reinforcement.getGroup();
		Group bypassGroup = cp.getReinforcementBypass();
		System.out.println("group: " + group);
		System.out.println("bypassGroup: " + bypassGroup);
		if (group.equals(bypassGroup) && group.isAuthorized(cp.getBukkitPlayer(), GroupPermissions.REINFORCEMENT_BYPASS)) {
			reinforcement.destroy();
			return false;
		}
		
		ReinforcementBlueprint rb = reinforcement.getBlueprint();
		reinforcement.damage(cp.getBukkitPlayer(), rb.getDefaultDamage());
		return true;
	}

	public void destroyReinforcement(Player player, Reinforcement reinforcement) {
		Location loc = reinforcement.getLocation();
		ReinforcementWorld reinWorld = getReinforcementWorld(loc.getWorld());
		ChunkCache<Reinforcement> reinChunkCache = reinWorld.getChunkCache(loc);
		reinChunkCache.delete(new Coords(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		ReinforcementDestroyEvent reinDestroyEvent = player != null ? new PlayerReinforcementDestroyEvent(player, reinforcement, dataSource) 
				: new ReinforcementDestroyEvent(reinforcement, dataSource);
		Bukkit.getPluginManager().callEvent(reinDestroyEvent);
	}
	
	public static Block getResponsibleBlock(Block block) {
		switch (block.getType()) {
		case YELLOW_FLOWER:
		case RED_ROSE:
		case SAPLING:
		case WHEAT:
		case CARROT:
		case POTATO:
		case BEETROOT:
		case MELON_STEM:
		case PUMPKIN_STEM:
		case NETHER_WART_BLOCK:
			return block.getRelative(BlockFace.DOWN);
		case SUGAR_CANE:
		case CACTUS:
			// scan downwards for first different block
			Block below = block.getRelative(BlockFace.DOWN);
			while (below.getType() == block.getType()) {
				below = below.getRelative(BlockFace.DOWN);
			}
			return below;
		case ACACIA_DOOR:
		case BIRCH_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR:
		case SPRUCE_DOOR:
		case JUNGLE_DOOR:
		case WOOD_DOOR:
			if (block.getRelative(BlockFace.UP).getType() != block.getType()) {
				// block is upper half of a door
				return block.getRelative(BlockFace.DOWN);
			}
		case BED:
			Bed bed = (Bed) block.getState();
			if (bed.isHeadOfBed()) {
				return block.getRelative(((Bed) block.getState()).getFacing().getOppositeFace());
			}
		default:
			return null;
		}
	}
	
	public static boolean isPreventingBlockAccess(Player player, Block block) {
		if (block == null) {
			return false;
		}
		if (block.getState() instanceof InventoryHolder) {
			Reinforcement rein = resolveDoubleChestReinforcement(block);
			if (rein == null) {
				return false;
			}
			return !rein.hasPermission(player, GroupPermissions.REINFORCEMENT_CONTAINER_BYPASS);
		}
		return false;
	}

	public static Reinforcement resolveDoubleChestReinforcement(Block b) {
		Material mat = b.getType();
		ReinforcementManager reinMan = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
		Reinforcement rein = reinMan.getReinforcement(b);
		if (rein != null || (mat != Material.CHEST && mat != Material.TRAPPED_CHEST)) {
			return rein;
		}
		for (BlockFace face : LocationTools.PLANAR_SIDES) {
			Block rel = b.getRelative(face);
			if (rel.getType() != mat) {
				continue;
			}
			rein = reinMan.getReinforcement(rel);
			if (rein != null) {
				return rein;
			}
		}
		return null;
	}
	
}
