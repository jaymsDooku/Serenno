package io.jayms.serenno.model.citadel.reinforcement;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Material;

import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;
import io.jayms.serenno.util.SerennoDataSource;

public interface ReinforcementDataSource extends SerennoDataSource<Reinforcement, Coords> {
	
	void persistAll(Collection<Reinforcement> reinforcements);
	
	boolean isAcidBlock(Material type);
	
	Map<Coords, Reinforcement> getAll(ChunkCoord coord);
	
}
