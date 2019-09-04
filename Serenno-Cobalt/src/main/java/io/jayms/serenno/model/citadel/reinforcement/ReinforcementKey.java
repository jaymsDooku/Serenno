package io.jayms.serenno.model.citadel.reinforcement;

import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;

public class ReinforcementKey {

	private ChunkCoord chunkCoords;
	private Coords coords;
	
	public ReinforcementKey(ChunkCoord chunkCoords, Coords coords) {
		this.chunkCoords = chunkCoords;
		this.coords = coords;
	}
	
	public void setChunkCoords(ChunkCoord chunkCoords) {
		this.chunkCoords = chunkCoords;
	}
	
	public void setCoords(Coords coords) {
		this.coords = coords;
	}
	
	public ChunkCoord getChunkCoords() {
		return chunkCoords;
	}
	
	public Coords getCoords() {
		return coords;
	}
	
}
