package io.jayms.serenno.model.citadel.artillery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.item.CustomItemManager;
import io.jayms.serenno.model.citadel.artillery.trebuchet.Trebuchet;
import io.jayms.serenno.model.citadel.artillery.trebuchet.TrebuchetCrateItem;

public enum ArtilleryType {

	TREBUCHET(Trebuchet.class, () -> {
		return (ArtilleryCrateItem) CustomItemManager.getCustomItemManager().getCustomItem(TrebuchetCrateItem.ID, TrebuchetCrateItem.class);
	}),
	CANNON(null, null);
	
	private Class<? extends Artillery> artilleryClazz;
	private Supplier<? extends ArtilleryCrateItem> artilleryCrateItemSupplier;
	
	private ArtilleryType(Class<? extends Artillery> artilleryClazz, Supplier<? extends ArtilleryCrateItem> artilleryCrateItemSupplier) {
		this.artilleryClazz = artilleryClazz;
		this.artilleryCrateItemSupplier = artilleryCrateItemSupplier;
	}
	
	public ItemStack getNewItem() {
		return artilleryCrateItemSupplier.get().getItemStack();
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
