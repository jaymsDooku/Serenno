package io.jayms.serenno.model.citadel.artillery;

import io.jayms.serenno.manager.ArtilleryManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class HitBoxProjector implements Runnable {

    private ArtilleryManager artilleryManager;

    public HitBoxProjector(ArtilleryManager artilleryManager) {
        this.artilleryManager = artilleryManager;
    }

    @Override
    public void run() {
        Collection<ArtilleryCrate> crates = artilleryManager.getCrates();
        if (crates.isEmpty()) return;
        for (ArtilleryCrate crate : crates) {
            Artillery artillery = crate.getArtillery();
            if (!artillery.isAssembled()) {
                artillery.projectHitBox();
            }
        }
    }

}
