package io.jayms.serenno.model.citadel.snitch;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.jayms.serenno.event.snitch.SnitchEnterEvent;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupMember;
import io.jayms.serenno.model.group.GroupPermissions;
import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.civmodcore.locations.QTBox;

public class Snitch implements QTBox, Comparable<Snitch> {

	private UUID reinforcementID;
	private String name;
	private int blockX;
	private int blockY;
	private int blockZ;
	private int radius;

	private SnitchWorld snitchWorld;

	public Snitch(Reinforcement reinforcement, String name, int radius) {
		this(reinforcement.getID(), name, radius, reinforcement.getLocation().getBlockX(), reinforcement.getLocation().getBlockY(), reinforcement.getLocation().getBlockZ());
	}

	public Snitch(UUID reinforcementID, String name, int radius, int blockX, int blockY, int blockZ) {
		this.reinforcementID = reinforcementID;
		this.name = name;
		this.radius = radius;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
	}

	public void setSnitchWorld(SnitchWorld snitchWorld) {
		this.snitchWorld = snitchWorld;
	}

	public UUID getReinforcementID() {
		return reinforcementID;
	}

	DecimalFormat df = new DecimalFormat("######");
	
	public void notifyEntrance(Player entering) {
		if (snitchWorld == null) {
			System.out.println("snitchWorld: " + snitchWorld);
		}
		ReinforcementWorld reinWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(snitchWorld.getWorld());
		Reinforcement reinforcement = getReinforcement(reinWorld);
		Group group = reinforcement.getGroup();
		if (group.isAuthorized(entering, GroupPermissions.SNITCH_NOTIFICATION_BYPASS)) {
			return;
		}
		
		SnitchEnterEvent event = new SnitchEnterEvent(this, entering);
		Bukkit.getPluginManager().callEvent(event);

		Location loc = reinforcement.getLocation();
		for (GroupMember member : group.getMembers()) {
			Player player = member.getPlayer();
			player.sendMessage(ChatColor.BLACK + "[" + ChatColor.AQUA + "Snitch" + ChatColor.BLACK + "]: " + ChatColor.DARK_AQUA + entering.getName() + ChatColor.AQUA
				+ " has entered a snitch " + ChatColor.DARK_AQUA + name + ChatColor.AQUA + " at " 
				+ ChatColor.DARK_AQUA + "[" + loc.getBlockX() + ChatColor.DARK_AQUA  + ", " + loc.getBlockY() + ChatColor.DARK_AQUA + ", " + loc.getBlockZ() + ChatColor.DARK_AQUA + "]"
				+ ChatColor.GOLD + " (" + ChatColor.YELLOW + df.format(player.getLocation().distance(loc)) + "m" + ChatColor.GOLD + ")");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Reinforcement getReinforcement(ReinforcementWorld world) {
		return world.getReinforcement(reinforcementID);
	}
	
	public int getRadius() {
		return radius;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Snitch)) {
			return false;
		}
		
		Snitch snitch = (Snitch) obj;
		return snitch.name.equals(name) && snitch.reinforcementID.equals(reinforcementID);
	}
	
	@Override
	public int compareTo(Snitch other) {
		int otherX = other.blockX;
		int otherY = other.blockY;
		int otherZ = other.blockZ;

		if (blockX < otherX) {
			return -1;
		}
		if (blockX > otherX) {
			return 1;
		}

		if (blockY < otherY) {
			return -1;
		}
		if (blockY > otherY) {
			return 1;
		}
		
		if (blockZ < otherZ) {
			return -1;
		}
		if (blockZ > otherZ) {
			return 1;
		}

		return 0;
	}

	@Override
	public int qtXMin() {
		return qtXMid() - getRadius();
	}

	@Override
	public int qtXMid() {
		return blockX;
	}

	@Override
	public int qtXMax() {
		return qtXMid() + getRadius();
	}

	@Override
	public int qtZMin() {
		return qtZMid() - getRadius();
	}

	@Override
	public int qtZMid() {
		return blockZ;
	}

	@Override
	public int qtZMax() {
		return qtZMid() + getRadius();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(qtXMid(), blockY, qtZMid());
	}
	
}

