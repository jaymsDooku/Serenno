package io.jayms.serenno.vault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.jayms.serenno.event.reinforcement.PlayerFortificationModeEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.lobby.event.SentToLobbyEvent;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.vault.event.CoreDestroyEvent;

public class VaultMapListener implements Listener {

	private VaultMapManager vm;
	
	public VaultMapListener(VaultMapManager vm) {
		this.vm = vm;
	}
	
	@EventHandler
	public void onReinforcementDestroy(ReinforcementDestroyEvent e) {
		Reinforcement reinforcement = e.getReinforcement();
		Location loc = reinforcement.getLocation();
		World world = loc.getWorld();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(world);
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase(world);
		Core core = database.getCoreSource().get(reinforcement);
		if (core == null) {
			return;
		}
		
		PlayerReinforcementDestroyEvent playerE = (e instanceof PlayerReinforcementDestroyEvent) ? (PlayerReinforcementDestroyEvent) e : null;
		CoreDestroyEvent event = new CoreDestroyEvent((playerE != null ) ? playerE.getDestroyer() : null, core);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	@EventHandler
	public void onFortificationMode(PlayerFortificationModeEvent e) {
		if (e.getGroupName() == null) {
			return;
		}
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		Group group = database.getGroupSource().get(e.getGroupName());
		ReinforcementBlueprint blueprint = database.getReinforcementBlueprintSource().get(e.getBlueprintItem());
		if (group == null) {
			return;
		}
		e.setGroup(group);
		e.setBlueprint(blueprint);
	}
	
	@EventHandler
	public void onLobbySent(SentToLobbyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		vaultMap.leaveVaultMap(player);
	}
	
}
