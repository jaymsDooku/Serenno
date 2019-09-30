package io.jayms.serenno.model.citadel.artillery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import io.jayms.serenno.model.citadel.artillery.trebuchet.Trebuchet;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetCrate;

public enum ArtilleryType {

	TREBUCHET(Trebuchet.class, TrebuchetCrate.class),
	CANNON(null, null);
	
	private Class<? extends Artillery> artilleryClazz;
	private Class<? extends ArtilleryCrate> artilleryCrateClazz;
	
	private ArtilleryType(Class<? extends Artillery> artilleryClazz, Class<? extends ArtilleryCrate> artilleryCrateClazz) {
		this.artilleryClazz = artilleryClazz;
		this.artilleryCrateClazz = artilleryCrateClazz;
	}
	
	public ArtilleryCrate getArtilleryCrate() {
		try {
			return artilleryCrateClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Artillery getArtillery(ArtilleryCrate crate) {
		try {
			Constructor<? extends Artillery> constructor = artilleryClazz.getConstructor(ArtilleryCrate.class);
			return constructor.newInstance(crate);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
