package io.jayms.serenno.model.citadel.artillery;

import java.util.HashSet;
import java.util.Set;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;
import com.google.common.collect.Sets;

public abstract class AbstractMissileRunner<T extends ArtilleryMissile> implements ArtilleryMissileRunner<T> {

	protected Set<T> missiles = Sets.newConcurrentHashSet();

	@Override
	public Set<T> getMissiles() {
		return missiles;
	}
	
	@Override
	public void haltMissile(T missile) {
		missiles.remove(missile);
	}
	
	@Override
	public void update() {
		if (missiles.isEmpty()) return;
		Set<T> toRemove = new HashSet<>();
		for (T missile : missiles) {
			if (update(missile)) {
				toRemove.add(missile);
			}
		}
		if (!toRemove.isEmpty()) {
			missiles.removeAll(toRemove);
		}
	}
	
}
