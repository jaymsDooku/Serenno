package io.jayms.serenno.model.citadel.artillery;

import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

public abstract class AbstractMissile<T extends Artillery> implements ArtilleryMissile<T> {

	private EngineerPlayer shooter;
	private ArtilleryMissileState state;
	
	protected AbstractMissile(EngineerPlayer shooter) {
		this.shooter = shooter;
	}
	
	@Override
	public EngineerPlayer getShooter() {
		return shooter;
	}
	
	@Override
	public boolean update() {
		this.state = state.update(this); 
		
		if (state.isAcceptingState()) {
			state.update(this);
			return true;
		}
		return false;
	}
	
	@Override
	public void setMissileState(ArtilleryMissileState set) {
		this.state = set;
	}
	
	@Override
	public ArtilleryMissileState getMissileState() {
		return state;
	}
	
}
