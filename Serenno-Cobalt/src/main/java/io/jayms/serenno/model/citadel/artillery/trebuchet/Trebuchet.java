package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Location;
import org.bukkit.Material;

import com.boydti.fawe.object.schematic.Schematic;
import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.ArtilleryManager;
import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.artillery.AbstractArtillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileRunner;
import net.md_5.bungee.api.ChatColor;

public class Trebuchet extends AbstractArtillery {
	
	public static final String NAME = "trebuchet";
	public static final String DISPLAY_NAME = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Trebuchet";
	
	private double firingAngleThreshold = 110;
	private double firingPower;
	private int firingAmmoAmount = 0;
	private Material firingAmmoMaterial;
	
	public Trebuchet(ArtilleryCrate crate) {
		super(crate);
		System.out.println("qtXMin: " + qtXMin());
		System.out.println("qtZMin: " + qtZMin());
		System.out.println("qtXMid: " + qtXMid());
		System.out.println("qtZMid: " + qtZMid());
		System.out.println("qtXMax: " + qtXMax());
		System.out.println("qtZMax: " + qtZMax());
		System.out.println("loc: " + getLocation());
	}
	
	private Location rotPoint;
	
	public Location getRotationPoint() {
		if (rotPoint == null) {
			Location origin = getLocation();
			int horizontalOffset = config.getTrebuchetHorizontalOffset();
			int verticalOffset = config.getTrebuchetVerticalOffset();
			rotPoint = origin.clone();
			
			switch (getDirection()) {
				case NORTH:
					rotPoint.add(0, verticalOffset, horizontalOffset);
					break;
				case EAST:
					rotPoint.add(horizontalOffset, verticalOffset, 0);
					break;
				case SOUTH:
					rotPoint.add(0, verticalOffset, -horizontalOffset);
					break;
				case WEST:
					rotPoint.add(-horizontalOffset, verticalOffset, 0);
					break;
				default:
					break;
			}
			rotPoint.add(0.5, 0.5, 0.5);
		}
		return rotPoint;
	}
	
	public void setFiringAngleThreshold(double firingAngleThreshold) {
		this.firingAngleThreshold = firingAngleThreshold;
	}
	
	public double getFiringAngleThreshold() {
		return firingAngleThreshold;
	}
	
	public void setFiringPower(double firingPower) {
		this.firingPower = firingPower;
	}
	
	public double getFiringPower() {
		return firingPower;
	}
	
	public void setFiringAmmoAmount(int firingAmmoAmount) {
		this.firingAmmoAmount = firingAmmoAmount;
	}
	
	public int getFiringAmmoAmount() {
		return firingAmmoAmount;
	}
	
	public Material getFiringAmmoMaterial() {
		return firingAmmoMaterial;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}
	
	@Override
	public Schematic getSchematic() {
		return config.getTrebuchetSchematic();
	}

	@Override
	public int qtXMin() {
		int sub = 0;
		
		switch (getDirection()) {
			case NORTH:
				sub = config.getTrebuchetLeftWidth();
				break;
			case EAST:
				sub = config.getTrebuchetBackwardLength();
				break;
			case SOUTH:
				sub = config.getTrebuchetRightWidth();
				break;
			case WEST:
				sub = config.getTrebuchetForwardLength();
				break;
			default:
				throw new IllegalStateException("Direction must be a primary cardinal.");
		}
		
		return qtXMid() - sub;
	}

	@Override
	public int qtXMid() {
		Location crateLoc = getCrate().getLocation();
		int x = crateLoc.getBlockX();
		switch (getDirection()) {
			case EAST:
				x += config.getTrebuchetBackwardLength(); 
				break;
			case WEST:
				x -= config.getTrebuchetBackwardLength();
				break;
			case NORTH:
			case SOUTH:
			default:
				break;
		}
		return x;
	}

	@Override
	public int qtXMax() {
		int sub = 0;
		
		switch (getDirection()) {
			case NORTH:
				sub = config.getTrebuchetRightWidth();
				break;
			case EAST:
				sub = config.getTrebuchetForwardLength();
				break;
			case SOUTH:
				sub = config.getTrebuchetLeftWidth();
				break;
			case WEST:
				sub = config.getTrebuchetBackwardLength();
				break;
			default:
				throw new IllegalStateException("Direction must be a primary cardinal.");
		}
		
		return qtXMid() + sub;
	}

	@Override
	public int qtZMin() {
		int sub = 0;
		
		switch (getDirection()) {
			case NORTH:
				sub = config.getTrebuchetForwardLength();
				break;
			case EAST:
				sub = config.getTrebuchetLeftWidth();
				break;
			case SOUTH:
				sub = config.getTrebuchetBackwardLength();
				break;
			case WEST:
				sub = config.getTrebuchetRightWidth();
				break;
			default:
				throw new IllegalStateException("Direction must be a primary cardinal.");
		}
		
		return qtZMid() - sub;
	}

	@Override
	public int qtZMid() {
		Location crateLoc = getCrate().getLocation();
		int x = crateLoc.getBlockX();
		switch (getDirection()) {
			case SOUTH:
				x += config.getTrebuchetBackwardLength(); 
				break;
			case NORTH:
				x -= config.getTrebuchetBackwardLength();
				break;
			case EAST:
			case WEST:
			default:
				break;
		}
		return x;
	}

	@Override
	public int qtZMax() {
		int sub = 0;
		
		switch (getDirection()) {
			case NORTH:
				sub = config.getTrebuchetForwardLength();
				break;
			case EAST:
				sub = config.getTrebuchetLeftWidth();
				break;
			case SOUTH:
				sub = config.getTrebuchetBackwardLength();
				break;
			case WEST:
				sub = config.getTrebuchetRightWidth();
				break;
			default:
				throw new IllegalStateException("Direction must be a primary cardinal.");
		}
		
		return qtZMid() + sub;
	}

	@Override
	public void fire(EngineerPlayer player) {
		ArtilleryManager am = SerennoCobalt.get().getCitadelManager().getArtilleryManager();
		ArtilleryMissileRunner missileRunner = am.getMissileRunner(Trebuchet.class);
		missileRunner.fireMissile(player, this);
		player.notify(ChatColor.YELLOW + "You have fired the " + getDisplayName());
	}

	@Override
	public long getCooldown() {
		return config.getTrebuchetCooldown();
	}

	@Override
	public int getMissileCount() {
		return config.getTrebuchetMissiles();
	}

	@Override
	public double getReinforcementDamage() {
		return config.getTrebuchetReinforcementDamage();
	}

	@Override
	public double getBastionDamage() {
		return config.getTrebuchetBastionDamage();
	}
	
	@Override
	public Menu getInterface() {
		return SerennoCobalt.get().getCitadelManager().getArtilleryManager().getTrebuchetMenu();
	}

	@Override
	public long getBlockDamageCooldown() {
		return config.getTrebuchetBlockDamageCD();
	}
	
}
