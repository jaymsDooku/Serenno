package io.jayms.serenno.listener.citadel;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;

public class CitadelChunkListener implements Listener {

	private ReinforcementManager rm;
	
	public CitadelChunkListener(ReinforcementManager rm) {
		this.rm = rm;
	}
	
	@EventHandler
	public void chunkLoad(ChunkLoadEvent e) {
		Chunk chunk = e.getChunk();
		ReinforcementWorld reinWorld = rm.getReinforcementWorld(chunk.getWorld());
		if (reinWorld == null) {
			return;
		}
		reinWorld.loadChunkData(e.getChunk());
	}
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent e) {
		Chunk chunk = e.getChunk();
		ReinforcementWorld reinWorld = rm.getReinforcementWorld(chunk.getWorld());
		if (reinWorld == null) {
			return;
		}
		reinWorld.unloadChunkData(e.getChunk());
	}
	
}
