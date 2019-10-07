package io.jayms.serenno.model.citadel.artillery;

import java.util.Set;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

public interface ArtilleryMissileRunner<T extends ArtilleryMissile> {
	
	Class<? extends Artillery> getArtilleryType();
	
	Set<T> getMissiles();
	
	void fireMissile(EngineerPlayer shooter, Artillery artillery);
	
	void haltMissile(T missile);
	
	boolean update(T missile);
	
	void update();
	
}
