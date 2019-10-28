package io.jayms.serenno.vault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.event.BastionBlueprintCreationEvent;
import io.jayms.serenno.event.BastionBlueprintDeletionEvent;
import io.jayms.serenno.event.BastionBlueprintModifyEvent;
import io.jayms.serenno.event.BastionBlueprintUpdateEvent;
import io.jayms.serenno.event.ListBlueprintsEvent;
import io.jayms.serenno.event.ReinforcementBlueprintCreationEvent;
import io.jayms.serenno.event.ReinforcementBlueprintDeletionEvent;
import io.jayms.serenno.event.ReinforcementBlueprintModifyEvent;
import io.jayms.serenno.event.ReinforcementBlueprintUpdateEvent;
import io.jayms.serenno.event.ViewBastionBlueprintEvent;
import io.jayms.serenno.event.ViewReinforcementBlueprintEvent;
import io.jayms.serenno.event.bastion.BastionPlacementEvent;
import io.jayms.serenno.event.reinforcement.PlayerFortificationModeEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDamageEvent;
import io.jayms.serenno.event.reinforcement.PlayerReinforcementDestroyEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDamageEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.lobby.event.SentToLobbyEvent;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.vault.event.CoreCreateEvent;
import io.jayms.serenno.vault.event.CoreDamageEvent;
import io.jayms.serenno.vault.event.CoreDestroyEvent;
import net.md_5.bungee.api.ChatColor;

public class VaultMapListener implements Listener {

	private VaultMapManager vm;
	
	public VaultMapListener(VaultMapManager vm) {
		this.vm = vm;
	}
	
	@EventHandler
	public void onReinforcementCreate(PlayerReinforcementCreationEvent e) {
		Reinforcement reinforcement = e.getReinforcement();
		Location loc = reinforcement.getLocation();
		Material material = loc.getBlock().getType();
		if (material != Material.EMERALD_BLOCK) {
			return;
		}
		
		World world = loc.getWorld();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(world.getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase(world);
		ChatColor teamColor = database.getTeamColourFromGroupName(reinforcement.getGroup().getName());
		if (teamColor == null) {
			e.getPlacer().sendMessage(ChatColor.RED + "That group doesn't have a vault team colour.");
			return;
		}
		Core core = new Core(database, teamColor, reinforcement);
		CoreCreateEvent event = new CoreCreateEvent(e.getPlacer(), core);
		Bukkit.getPluginManager().callEvent(event);
		database.getCoreSource().create(core);
		e.getPlacer().sendMessage(ChatColor.YELLOW + "You have created a core for " + core.getTeamColor() + reinforcement.getGroup().getName());
	}
	
	@EventHandler
	public void onReinforcementDamage(ReinforcementDamageEvent e) {
		Reinforcement reinforcement = e.getReinforcement();
		Location loc = reinforcement.getLocation();
		World world = loc.getWorld();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(world.getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase(world);
		Core core = database.getCoreSource().get(reinforcement);
		if (core == null) {
			return;
		}
		
		PlayerReinforcementDamageEvent playerE = (e instanceof PlayerReinforcementDamageEvent) ? (PlayerReinforcementDamageEvent) e : null;
		CoreDamageEvent event = new CoreDamageEvent((playerE != null ) ? playerE.getDamager() : null, core, e.getDamage());
		Bukkit.getPluginManager().callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@EventHandler
	public void onReinforcementDestroy(ReinforcementDestroyEvent e) {
		Reinforcement reinforcement = e.getReinforcement();
		Location loc = reinforcement.getLocation();
		World world = loc.getWorld();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(world.getUID());
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
		core.getVaultMapDatabase().getCoreSource().delete(core);
		if (playerE != null) {
			playerE.getDestroyer().sendMessage(ChatColor.YELLOW + "You have destroyed a core for " + ChatColor.GOLD + reinforcement.getGroup().getName());
		}
	}
	
	@EventHandler
	public void onBastionCreate(BastionPlacementEvent e) {
		Reinforcement rein = e.getReinforcement();
		Location loc = rein.getLocation();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(loc.getWorld().getUID());
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
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
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
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setReinforcementBlueprint(database.getReinforcementBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onViewBastionBlueprint(ViewBastionBlueprintEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setBastionBlueprint(database.getBastionBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onReinforcementBlueprintCreate(ReinforcementBlueprintCreationEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		e.setCancelled(true);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getReinforcementBlueprintSource().create(e.getReinforcementBlueprint());
	} 
	
	@EventHandler
	public void onReinforcementBlueprintDeletion(ReinforcementBlueprintDeletionEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		ReinforcementBlueprint rb = database.getReinforcementBlueprintSource().get(e.getBlueprintName());
		database.getReinforcementBlueprintSource().delete(rb);
		
		e.setCancelled(true);
		e.setReinforcementBlueprint(rb);
	}
	
	@EventHandler
	public void onBastionBlueprintCreate(BastionBlueprintCreationEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		e.setCancelled(true);
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getBastionBlueprintSource().create(e.getBastionBlueprint());
	}
	
	@EventHandler
	public void onBastionBlueprintDeletion(BastionBlueprintDeletionEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		BastionBlueprint bb = database.getBastionBlueprintSource().get(e.getBlueprintName());
		database.getBastionBlueprintSource().delete(bb);
		
		e.setCancelled(true);
		e.setBastionBlueprint(bb);
	}
	
	@EventHandler
	public void onReinforcementBlueprintModify(ReinforcementBlueprintModifyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setReinforcementBlueprint(database.getReinforcementBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onBastionBlueprintModify(BastionBlueprintModifyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		e.setBastionBlueprint(database.getBastionBlueprintSource().get(e.getBlueprintName()));
	}
	
	@EventHandler
	public void onReinforcementBlueprintUpdate(ReinforcementBlueprintUpdateEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		database.getReinforcementBlueprintSource().update(e.getReinforcementBlueprint());
	}
	
	@EventHandler
	public void onBastionBlueprintUpdate(BastionBlueprintUpdateEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
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
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		VaultMapDatabase database = vaultMap.getDatabase();
		Group group = database.getGroupSource().get(e.getGroupName().toLowerCase());
		if (group == null) {
			return;
		}
		e.setGroup(group);
		ReinforcementBlueprint blueprint = database.getReinforcementBlueprintSource().get(e.getBlueprintItem());
		if (blueprint == null) {
			return;
		}
		e.setBlueprint(blueprint);
	}
	
	@EventHandler
	public void onLobbySent(SentToLobbyEvent e) {
		Player player = e.getPlayer();
		VaultMap vaultMap = vm.getWorldToVaultMaps().get(player.getWorld().getUID());
		if (vaultMap == null) {
			return;
		}
		vaultMap.leaveVaultMap(player);
	}
	
}
