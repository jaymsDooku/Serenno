package io.jayms.serenno.model.citadel.artillery;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.util.ParticleEffect;
import io.jayms.serenno.util.ParticleTools;
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
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.group.GroupPermissions;

public abstract class AbstractArtillery implements Artillery, Comparable<Artillery> {

	private static int ID = 0;
	
	private static int newID() {
		return ID++;
	}
	
	private int id;
	private ArtilleryCrate crate;
	private BlockFace direction = BlockFace.NORTH;
	private boolean assembled;
	private boolean firing;
	private long lastFired;
	
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
	public void setFiring(boolean set) {
		firing = set;
	}
	
	@Override
	public boolean isFiring() {
		return firing;
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
		return crate.getReinforcement();
	}
	
	@Override
	public Location getLocation() {
		Location crateLoc = crate.getLocation(); 
		Location loc = new Location(crateLoc.getWorld(), this.qtXMid(), crateLoc.getBlockY(), this.qtZMid());
		return loc;
	}
	
	@Override
	public long getLastFiringTime() {
		return lastFired;
	}
	
	@Override
	public boolean onCooldown() {
		return System.currentTimeMillis() < (lastFired + config.getTrebuchetCooldown());
	}
	
	private DecimalFormat df = new DecimalFormat("#.#");
	
	@Override
	public boolean fire(EngineerPlayer player) {
		if (onCooldown()) {
			long timeLeft = (lastFired + getCooldown()) - System.currentTimeMillis();
			double secLeft = timeLeft / 1000L;
			player.sendMessage(ChatColor.RED + "Trebuchet is still cooling down. Time left: " + df.format(secLeft) + "s");
			return false;
		}
		return true;
	}

	private static final ParticleTools.ParticlePlay play = new ParticleTools.ParticlePlay() {
		@Override
		public void play(Location loc) {
			ParticleEffect.FLAME.display(loc, 0, 0, 0, 0, 1);
		}
	};

	@Override
	public void projectHitBox() {
		Location loc = getLocation();

		int minX = qtXMin();
		int minZ = qtZMin();
		int maxX = qtXMax();
		int maxZ = qtZMax();
		int minY = getLowerY();
		int maxY = getUpperY();

		Location corner1 = new Location(loc.getWorld(), minX, minY, minZ);
		Location corner2 = new Location(loc.getWorld(), minX, minY, maxZ);
		Location corner3 = new Location(loc.getWorld(), maxX, minY, minZ);
		Location corner4 = new Location(loc.getWorld(), maxX, minY, maxZ);

		Location corner5 = new Location(loc.getWorld(), minX, maxY, minZ);
		Location corner6 = new Location(loc.getWorld(), minX, maxY, maxZ);
		Location corner7 = new Location(loc.getWorld(), maxX, maxY, minZ);
		Location corner8 = new Location(loc.getWorld(), maxX, maxY, maxZ);

		ParticleTools.drawLine(corner1, corner2, 20, play);
		ParticleTools.drawLine(corner1, corner3, 20, play);
		ParticleTools.drawLine(corner1, corner5, 20, play);

		ParticleTools.drawLine(corner2, corner4, 20, play);
		ParticleTools.drawLine(corner2, corner6, 20, play);

		ParticleTools.drawLine(corner3, corner4, 20, play);
		ParticleTools.drawLine(corner3, corner7, 20, play);

		ParticleTools.drawLine(corner5, corner6, 20, play);
		ParticleTools.drawLine(corner5, corner7, 20, play);

		ParticleTools.drawLine(corner8, corner4, 20, play);
		ParticleTools.drawLine(corner8, corner6, 20, play);
		ParticleTools.drawLine(corner8, corner7, 20, play);
	}

	@Override
	public void assemble(EngineerPlayer player) {
		Location loc = getLocation();
		
		int minX = qtXMin();
		int minZ = qtZMin();
		int maxX = qtXMax();
		int maxZ = qtZMax();
		int minY = getLowerY();
		int maxY = getUpperY();
		
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
		
		Location plusPlus = new Location(loc.getWorld(), maxX, minY, maxZ);
		Location minusMinus = new Location(loc.getWorld(), maxX, minY, maxZ);
		Location plusMinus = new Location(loc.getWorld(), maxX, minY, minZ);
		Location minusPlus = new Location(loc.getWorld(), minX, minY, maxZ);
		Set<Bastion> bastions = new HashSet<>();
		bastions.addAll(SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(plusPlus));
		bastions.addAll(SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(minusMinus));
		bastions.addAll(SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(plusMinus));
		bastions.addAll(SerennoCobalt.get().getCitadelManager().getBastionManager().getBastions(minusPlus));
		ReinforcementWorld reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(loc.getWorld());
		for (Bastion bastion : bastions) {
			if (!bastion.getReinforcement(reinforcementWorld).getGroup().isAuthorized(player.getBukkitPlayer(), GroupPermissions.REINFORCEMENT_FORTIFICATION)) {
				player.sendMessage(ChatColor.RED + "You aren't allowed to assemble artillery in foreign bastions.");
				return;
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
				position = position.add(-config.getTrebuchetRightWidth(), 0, config.getTrebuchetBackwardLength());
				transform = transform.rotateY(90);
				break;
			case NORTH:
				position = position.add(config.getTrebuchetRightWidth(), 0, config.getTrebuchetBackwardLength());
				transform = transform.rotateY(180);
				break;
			case WEST:
				position = position.add(config.getTrebuchetRightWidth(), 0, -config.getTrebuchetBackwardLength());
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
		player.sendMessage(ChatColor.YELLOW + "Left click with your wrench to open the operator menu.");
		player.sendMessage(ChatColor.YELLOW + "Right click with your wrench to fire!");
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
					if (b.getType() == Material.WOOD_BUTTON) {
						b.setType(Material.AIR);
					}
				}
			}
		}
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
	public boolean dealBlockDamage(Player player) {
		if (reinforcement == null) {
			this.reinforcement = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getDirectReinforcement(crate.getLocation().getBlock());
		}
		return reinforcement.damage(getBastionDamage());
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Artillery)) {
			return false;
		}
		
		Artillery art = (Artillery) obj;
		return this.id == art.getID();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
}
