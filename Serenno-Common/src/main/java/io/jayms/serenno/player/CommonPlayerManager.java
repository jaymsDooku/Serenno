package io.jayms.serenno.player;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.manager.PlayerManager;
import io.jayms.serenno.rank.Rank;
import io.jayms.serenno.rank.RankHandler;

public class CommonPlayerManager extends PlayerManager<CommonPlayer> {

	private RankHandler rankHandler;
	
	public CommonPlayerManager(SerennoCommon common) {
		super();
		this.rankHandler = new RankHandler(common);
	}
	
	@Override
	protected Function<Player, CommonPlayer> getPlayerInstantiator() {
		return (p) -> {
			return new CommonPlayer(p);
		};
	}

	@Override
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		CommonPlayer corePlayer = this.get(player);
		
		corePlayer.setRank(Rank.MEMBER);
		if (player.getUniqueId().equals(UUID.fromString("55de7677-2818-4ec6-8d0e-dd59f3bd9c5e"))) {
			corePlayer.setRank(Rank.ADMIN);
		}
		
		rankHandler.initPermissions(corePlayer);
	}

	@Override
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		CommonPlayer corePlayer = this.remove(player);
		
		rankHandler.removePermissions(corePlayer);
	}
	
}
