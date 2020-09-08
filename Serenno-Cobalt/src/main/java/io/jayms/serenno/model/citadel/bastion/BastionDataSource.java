package io.jayms.serenno.model.citadel.bastion;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.util.SerennoDependentDataSource;

import java.util.Collection;

public interface BastionDataSource extends SerennoDependentDataSource<Bastion, Reinforcement, ReinforcementWorld> {

    void persistAll(ReinforcementWorld world, Collection<Bastion> bastions, ReinforcementWorld.UnloadCallback callback);

}
