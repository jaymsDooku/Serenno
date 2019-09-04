package io.jayms.serenno.model.citadel;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;

public class ReinforcementMode {

	public enum ReinforceMethod {
		FORTIFY, REINFORCE;
	}
	
	private ReinforcementBlueprint reinforcementBlueprint;
	private Group groupToReinforce;
	private ReinforceMethod method;
	
	public ReinforcementMode(ReinforcementBlueprint reinforcementBlueprint, Group groupToReinforce, ReinforceMethod method) {
		this.reinforcementBlueprint = reinforcementBlueprint;
		this.groupToReinforce = groupToReinforce;
		this.method = method;
	}
	
	public ReinforcementBlueprint getReinforcementBlueprint() {
		return reinforcementBlueprint;
	}
	
	public Group getGroupToReinforce() {
		return groupToReinforce;
	}
	
	public ReinforceMethod getMethod() {
		return method;
	}
	
}
