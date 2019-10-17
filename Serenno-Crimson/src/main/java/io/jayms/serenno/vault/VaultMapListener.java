package io.jayms.serenno.vault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
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
		VaultMapDatabase database = vaultMap.getDatabase(world);
		Core core = database.getCoreSource().get(reinforcement);
		if (core == null) {
			return;
		}
		
		PlayerReinforcementDestroyEvent playerE = (e instanceof PlayerReinforcementDestroyEvent) ? (PlayerReinforcementDestroyEvent) e : null;
		CoreDestroyEvent event = new CoreDestroyEvent((playerE != null ) ? playerE.getDestroyer() : null, core);
		Bukkit.getPluginManager().callEvent(event);
	}
	
}
