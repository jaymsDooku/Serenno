package io.jayms.serenno.model.citadel.artillery.trebuchet;

import java.util.Set;

import org.bukkit.entity.FallingBlock;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.google.common.collect.Sets;

import io.jayms.serenno.model.citadel.artillery.AbstractMissileRunner;
import io.jayms.serenno.model.citadel.artillery.Artillery;

public class TrebuchetMissileRunner extends AbstractMissileRunner<TrebuchetMissile> {

	public TrebuchetMissileRunner() {
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
	
}
