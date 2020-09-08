package io.jayms.serenno.model.citadel.reinforcement;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.SerennoDependentDataSource;
import org.bukkit.Material;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld.UnloadCallback;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;
import io.jayms.serenno.util.SerennoDataSource;

public interface ReinforcementDataSource extends SerennoDataSource<Reinforcement, Coords> {
	
	void persistAll(Collection<Reinforcement> reinforcements, UnloadCallback callback);
	
	boolean isAcidBlock(Material type);
	
	Map<Coords, Reinforcement> getAll(ChunkCoord coord);
	
}
