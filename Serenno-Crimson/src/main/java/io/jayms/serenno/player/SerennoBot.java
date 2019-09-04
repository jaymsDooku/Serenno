package io.jayms.serenno.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import io.jayms.serenno.bot.Bot;
import net.citizensnpcs.api.npc.NPC;

public class SerennoBot extends SerennoPlayer {

	private NPC npc;
	
	public SerennoBot(NPC npc) {
		super((Player) npc.getEntity());
		this.npc = npc;
	}
	
	@Override
	public Player getBukkitPlayer() {
		return getBot().getPlayer();
	}
	
	@Override
	public void teleport(Location loc) {
		if (npc.isSpawned()) {
			npc.teleport(loc, TeleportCause.PLUGIN);
		}
	}
	
	@Override
	public void sendMessage(String message) {
	}
	
	public NPC getNpc() {
		return npc;
	}
	
	public Bot getBot() {
		return Bot.getBot(npc);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SerennoBot)) {
			return false;
		}
		
		SerennoBot bot = (SerennoBot) obj;
		return bot.npc.getId() == npc.getId();
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(npc.getId());
	}

}
