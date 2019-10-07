package io.jayms.serenno.model.citadel.artillery.trebuchet;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.google.common.collect.Sets;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.artillery.AbstractMissileRunner;
import io.jayms.serenno.model.citadel.artillery.Artillery;

public class TrebuchetMissileRunner extends AbstractMissileRunner<TrebuchetMissile> implements Listener {
	
	private static Set<FallingBlock> missileBlocks = Sets.newConcurrentHashSet();
	
	public static void addMissileBlock(FallingBlock fb) {
		missileBlocks.add(fb);
	}

	public TrebuchetMissileRunner() {
		Bukkit.getPluginManager().registerEvents(this, SerennoCobalt.get());
	}
	
	@Override
	public Class<? extends Artillery> getArtilleryType() {
		return Trebuchet.class;
	}
	
	@Override
	public void fireMissile(EngineerPlayer shooter, Artillery artillery) {
		TrebuchetMissile missile = new TrebuchetMissile(shooter, ((Trebuchet)artillery));
		missiles.add(missile);
	}
	
	@Override
	public boolean update(TrebuchetMissile missile) {
		return missile.update();
	}
	
	@EventHandler
	public void onMissileLand(EntityChangeBlockEvent e) {
		if (missiles.isEmpty()) {
			return;
		}
		for (TrebuchetMissile missile : missiles) {
			if (missile.getMissileBlock().getUniqueId().equals(e.getEntity().getUniqueId())) {
				missile.setMissileBlock(null);
				e.setCancelled(true);
			}
		}
	}
	
}
