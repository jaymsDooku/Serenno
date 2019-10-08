package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCobaltConfigManager;
import io.jayms.serenno.model.citadel.artillery.AbstractMissile;
import io.jayms.serenno.model.citadel.artillery.ArtilleryMissileState;

public class TrebuchetMissile extends AbstractMissile<Trebuchet> implements Listener {

	public static final ArtilleryMissileState<TrebuchetMissile> LAUNCH = new TrebuchetMissileLaunchState();
	public static final ArtilleryMissileState<TrebuchetMissile> PROGRESS = new TrebuchetMissileProgressState();
	public static final ArtilleryMissileState<TrebuchetMissile> FINISH = new TrebuchetMissileFinishState();
	
	private SerennoCobaltConfigManager config = SerennoCobalt.get().getConfigManager();
	
	private Trebuchet trebuchet;
	private Location loc;
	private Location prevLocation;
	private Location lowStart;
	private boolean launching;
	private FallingBlock missileBlock;
	
	private double rotation;
	private double dRotation;
	
	public TrebuchetMissile(EngineerPlayer engineer, Trebuchet trebuchet) {
		super(engineer);
		this.trebuchet = trebuchet;
		this.lowStart = trebuchet.getRotationPoint().clone().subtract(0, config.getTrebuchetHeight() - 4, 0);
		this.loc = getStartingPoint(trebuchet);
		
		this.missileBlock = loc.getWorld().spawnFallingBlock(loc, Material.STONE, (byte) 1);
		missileBlock.setDropItem(false);
		TrebuchetMissileRunner.addMissileBlock(missileBlock);
		
		setMissileState(LAUNCH);
		Bukkit.getPluginManager().registerEvents(this, SerennoCobalt.get());
	}
	
	public void setPrevLocation(Location prevLocation) {
		this.prevLocation = prevLocation;
	}
	
	public Location getPrevLocation() {
		return prevLocation;
	}
	
	public void setMissileBlock(FallingBlock missileBlock) {
		this.missileBlock = missileBlock;
	}
	
	public FallingBlock getMissileBlock() {
		return missileBlock;
	}
	
	public void setLaunching(boolean launching) {
		this.launching = launching;
	}
	
	public boolean isLaunching() {
		return launching;
	}
	
	public Location getLowStart() {
		return lowStart;
	}
	
	public Location getStartingPoint(Trebuchet trebuchet) {
		Location loc = lowStart;
		switch (trebuchet.getDirection()) {
			case NORTH:
				loc = loc.add(0, 0, -7);
				break;
			case EAST:
				loc = loc.add(7, 0, 0);
				break;
			case SOUTH:
				loc = loc.add(0, 0, 7);
				break;
			case WEST:
				loc = loc.add(-7, 0, 0);
				break;
			default:
				break;
		}
		return loc;
	}
	
	public void setdRotation(double dRotation) {
		this.dRotation = dRotation;
	}
	
	public double getdRotation() {
		return dRotation;
	}
	
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	public double getRotation() {
		return rotation;
	}
	
	@Override
	public double getSpeed() {
		return 1.5;
	}

	@Override
	public double getGravity() {
		return 0.1;
	}

	@Override
	public double getDamage() {
		return 5;
	}

	@Override
	public double getBastionDamage() {
		return config.getTrebuchetBastionDamage();
	}

	@Override
	public double getReinforcementDamage() {
		return config.getTrebuchetReinforcementDamage();
	}
	
	@Override
	public void setLocation(Location set) {
		this.loc = set;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public Trebuchet getArtillery() {
		return trebuchet;
	}
	
	@EventHandler
	public void onMissileLand(EntityChangeBlockEvent e) {
		if (getMissileBlock().getUniqueId().equals(e.getEntity().getUniqueId())) {
			setMissileBlock(null);
			e.setCancelled(true);
		}
	}
	
}
