package io.jayms.serenno.model.citadel.snitch;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

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
	
	private Reinforcement reinforcement;
	private String name;
	private int radius;
	
	public Snitch(Reinforcement reinforcement, String name, int radius) {
		this.reinforcement = reinforcement;
		this.name = name;
		this.radius = radius;
	}
	
	DecimalFormat df = new DecimalFormat("######");
	
	public void notifyEntrance(Player entering) {
		Group group = reinforcement.getGroup();
		if (group.isAuthorized(entering, GroupPermissions.SNITCH_NOTIFICATION_BYPASS)) {
			return;
		}
		
		SnitchEnterEvent event = new SnitchEnterEvent(this, entering);
		Bukkit.getPluginManager().callEvent(event);
		
		for (GroupMember member : group.getMembers()) {
			Player player = member.getPlayer();
			player.sendMessage(ChatColor.BLACK + "[" + ChatColor.AQUA + "Snitch" + ChatColor.BLACK + "]: " + ChatColor.DARK_AQUA + entering.getName() + ChatColor.AQUA
				+ " has entered a snitch " + ChatColor.DARK_AQUA + name + ChatColor.AQUA + " at " 
				+ ChatColor.DARK_AQUA + "[" + getLocation().getBlockX() + ChatColor.DARK_AQUA  + ", " + getLocation().getBlockY() + ChatColor.DARK_AQUA + ", " + getLocation().getBlockZ() + ChatColor.DARK_AQUA + "]"
				+ ChatColor.GOLD + " (" + ChatColor.YELLOW + df.format(player.getLocation().distance(getLocation())) + "m" + ChatColor.GOLD + ")");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public Location getLocation() {
		return reinforcement.getLocation();
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void damage(double dmg) {
		reinforcement.damage(dmg);
	}
	
	public void destroy() {
		reinforcement.destroy();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Snitch)) {
			return false;
		}
		
		Snitch snitch = (Snitch) obj;
		return snitch.name.equals(name) && snitch.reinforcement.equals(reinforcement);
	}
	
	@Override
	public int compareTo(Snitch other) {
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
	public int qtXMin() {
		return qtXMid() - getRadius();
	}

	@Override
	public int qtXMid() {
		return getLocation().getBlockX();
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
		return getLocation().getBlockZ();
	}

	@Override
	public int qtZMax() {
		return qtZMid() + getRadius();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(qtXMid(), getLocation().getBlockY(), qtZMid());
	}
	
}

