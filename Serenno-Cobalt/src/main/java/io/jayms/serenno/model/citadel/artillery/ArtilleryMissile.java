package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

public interface ArtilleryMissile<T extends Artillery> {
	
	EngineerPlayer getShooter();
	
	double getSpeed();
	
	double getGravity();
	
	double getDamage();
	
	double getBastionDamage();
	
	double getReinforcementDamage();

	void setLocation(Location set);
	
	Location getLocation();
	
	T getArtillery();
	
	void setMissileState(ArtilleryMissileState set);
	
	ArtilleryMissileState getMissileState();
	
	boolean update();
	
}
