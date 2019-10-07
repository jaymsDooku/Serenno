package io.jayms.serenno.model.citadel.artillery;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.boydti.fawe.object.schematic.Schematic;
import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.World;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCobaltConfigManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public abstract class AbstractArtillery implements Artillery, Comparable<Artillery> {

	private static int ID = 0;
	
	private static int newID() {
		return ID++;
	}
	
	private int id;
	private ArtilleryCrate crate;
	private BlockFace direction = BlockFace.NORTH;
	private boolean assembled;
	
	protected SerennoCobaltConfigManager config = SerennoCobalt.get().getConfigManager();
	
	protected AbstractArtillery(ArtilleryCrate crate) {
		this.id = newID();
		this.crate = crate;
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public ArtilleryCrate getCrate() {
		return crate;
	}
	
	@Override
	public void setDirection(BlockFace dir) {
		this.direction = dir;
	}
	
	@Override
	public BlockFace getDirection() {
		return direction;
	}
	
	@Override
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	@Override
	public Location getLocation() {
		Location crateLoc = crate.getLocation(); 
		Location loc = new Location(crateLoc.getWorld(), this.qtXMid(), crateLoc.getBlockY(), this.qtZMid());
		loc = loc.add(0, 1, 0);
		return loc;
	}
	
	@Override
	public void assemble(EngineerPlayer player) {
		Location loc = getLocation();
		
		int minX = qtXMin();
		int minZ = qtXMin();
		int maxX = qtXMax();
		int maxZ = qtZMax();
		int minY = crate.getLocation().getBlockY();
		int maxY = minY + config.getTrebuchetHeight();
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = loc.getWorld().getBlockAt(x, y, z);
					if (b.getType() != Material.AIR && !b.getLocation().equals(crate.getLocation())) {
						player.sendMessage(ChatColor.RED + "There isn't enough room for assembly.");
						return;
					}
				}
			}
		}
		
		Schematic schematic = getSchematic();
		boolean allowUndo = true;
		boolean noAir = false;
		World world = new BukkitWorld(loc.getWorld());
		com.sk89q.worldedit.Vector position = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
		AffineTransform transform = new AffineTransform();
		switch (getDirection()) {
			case EAST:
				//position = position.add(-config.getTrebuchetRightWidth(), 0, -config.getTrebuchetBackwardLength());
				transform = transform.rotateY(90);
				break;
			case NORTH:
				position = position.add(config.getTrebuchetRightWidth(), 0, config.getTrebuchetBackwardLength());
				transform = transform.rotateY(180);
				break;
			case WEST:
				//position = position.add(-config.getTrebuchetRightWidth(), 0, config.getTrebuchetBackwardLength());
				transform = transform.rotateY(270);
				break;
			case SOUTH:
				position = position.add(-config.getTrebuchetRightWidth(), 0, -config.getTrebuchetBackwardLength());
			default:
				break;
		}
		schematic.paste(world, position, allowUndo, !noAir, transform);
		assembled = true;
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().assemble(this);
		player.sendMessage(ChatColor.YELLOW + "You have assembled a " + getDisplayName());
	}
	
	@Override
	public void disassemble() {
		Location loc = getLocation();
		
		int minX = qtXMin();
		int minZ = qtZMin();
		int maxX = qtXMax();
		int maxZ = qtZMax();
		int minY = crate.getLocation().getBlockY();
		int maxY = minY + config.getTrebuchetHeight();
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = loc.getWorld().getBlockAt(x, y, z);
					b.setType(Material.AIR);
				}
			}
		}
		SerennoCobalt.get().getCitadelManager().getArtilleryManager().disassemble(this);
	}

	@Override
	public boolean isAssembled() {
		return assembled;
	}
	
	private Reinforcement reinforcement;
	
	@Override
	public void dealBlockDamage(Player player) {
		if (reinforcement == null) {
			this.reinforcement = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getDirectReinforcement(crate.getLocation().getBlock());
		}
		reinforcement.damage(1);
	}
	
	@Override
	public int compareTo(Artillery other) {
		Location location = getLocation();
		UUID thisWorld = location.getWorld().getUID();
		int thisX = location.getBlockX();
		int thisY = location.getBlockY();
		int thisZ = location.getBlockZ();

		Location otherLocation = other.getLocation();
		UUID otherWorld = otherLocation.getWorld().getUID();
		int otherX = otherLocation.getBlockX();
		int otherY = otherLocation.getBlockY();
		int otherZ = otherLocation.getBlockZ();

		int worldCompare = thisWorld.compareTo(otherWorld);
		if (worldCompare != 0) {
			return worldCompare;
		}

		if (thisX < otherX) {
			return -1;
		}
		if (thisX > otherX) {
			return 1;
		}

		if (thisY < otherY) {
			return -1;
		}
		if (thisY > otherY) {
			return 1;
		}
		
		if (thisZ < otherZ) {
			return -1;
		}
		if (thisZ > otherZ) {
			return 1;
		}

		return 0;
	}
	
}
