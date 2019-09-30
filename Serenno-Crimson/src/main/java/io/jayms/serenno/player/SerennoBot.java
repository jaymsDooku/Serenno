package io.jayms.serenno.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import io.jayms.serenno.bot.Bot;
import mkremins.fanciful.FancyMessage;
import net.citizensnpcs.api.npc.NPC;

public class SerennoBot extends SerennoPlayer {

	private UUID id;
	private NPC npc;
	
	public SerennoBot(NPC npc) {
		super((Player) npc.getEntity());
		this.npc = npc;
		this.id = ((Player) npc.getEntity()).getUniqueId();
	}
	
	@Override
	public UUID getID() {
		return id;
	}
	
	@Override
	public Player getBukkitPlayer() {
		Bot bot =  getBot();
		if (bot == null) return null;
		return bot.getPlayer();
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
	
	@Override
	public void sendMessage(FancyMessage message) {
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
