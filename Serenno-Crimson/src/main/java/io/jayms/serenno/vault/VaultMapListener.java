package io.jayms.serenno.vault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.event.BastionBlueprintCreationEvent;
import io.jayms.serenno.event.BastionBlueprintModifyEvent;
import io.jayms.serenno.event.BastionBlueprintUpdateEvent;
import io.jayms.serenno.event.ListBlueprintsEvent;
import io.jayms.serenno.event.ReinforcementBlueprintCreationEvent;
import io.jayms.serenno.event.ReinforcementBlueprintModifyEvent;
import io.jayms.serenno.event.ReinforcementBlueprintUpdateEvent;
import io.jayms.serenno.event.ViewBastionBlueprintEvent;
import io.jayms.serenno.event.ViewReinforcementBlueprintEvent;
import io.jayms.serenno.event.bastion.BastionPlacementEvent;
import io.jayms.serenno.event.reinforcement.PlayerFortificationModeEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.lobby.event.SentToLobbyEvent;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
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
	public void onBastionCreate(BastionPlacementEvent e) {
		Reinforcement rein = e.getReinforcement();
		Location loc = rein.getLocation();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(loc.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		ItemStack item = e.getItemPlaced();
		BastionBlueprint bb = database.getBastionBlueprintSource().get(item);
		if (bb == null) {
			return;
		}
		e.setBlueprint(bb);
	}
	
	@EventHandler
	public void onListBlueprints(ListBlueprintsEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setReinforcementBlueprints(database.getReinforcementBlueprintSource().getAll());
		e.setBastionBlueprints(database.getBastionBlueprintSource().getAll());
	}
	
	@EventHandler
	public void onViewReinforcementBlueprint(ViewReinforcementBlueprintEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setReinforcementBlueprint(database.getReinforcementBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onViewBastionBlueprint(ViewBastionBlueprintEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setBastionBlueprint(database.getBastionBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onReinforcementBlueprintCreate(ReinforcementBlueprintCreationEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		e.setCancelled(true);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getReinforcementBlueprintSource().create(e.getReinforcementBlueprint());
	} 
	
	@EventHandler
	public void onBastionBlueprintCreate(BastionBlueprintCreationEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		e.setCancelled(true);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getBastionBlueprintSource().create(e.getBastionBlueprint());
	}
	
	@EventHandler
	public void onReinforcementBlueprintModify(ReinforcementBlueprintModifyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setReinforcementBlueprint(database.getReinforcementBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onBastionBlueprintModify(BastionBlueprintModifyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setBastionBlueprint(database.getBastionBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onReinforcementBlueprintUpdate(ReinforcementBlueprintUpdateEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getReinforcementBlueprintSource().update(e.getReinforcementBlueprint());
	}
	
	@EventHandler
	public void onBastionBlueprintUpdate(BastionBlueprintUpdateEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getBastionBlueprintSource().update(e.getBastionBlueprint());
	}
	
	//if we ever want to change snitch radius
	/*@EventHandler
	public void onSnitchCreate(SnitchPlacementEvent e) {
		Reinforcement rein = e.getReinforcement();
		Location loc = rein.getLocation();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(loc.getWorld());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		
	}*/
	
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
