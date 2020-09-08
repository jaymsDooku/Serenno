package io.jayms.serenno.model.citadel.snitch;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.util.SerennoDependentDataSource;

import java.util.Collection;

public interface SnitchDataSource extends SerennoDependentDataSource<Snitch, Reinforcement, ReinforcementWorld> {

    void persistAll(ReinforcementWorld world, Collection<Snitch> snitches, ReinforcementWorld.UnloadCallback callback);

}
