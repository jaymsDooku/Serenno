package io.jayms.serenno.command;

import java.util.Arrays;
import java.util.Collection;

import io.jayms.serenno.manager.*;
import io.jayms.serenno.model.citadel.artillery.ArtilleryWorld;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.citadel.snitch.SnitchWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
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
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.artillery.ArtilleryType;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

	private CitadelManager cm = SerennoCobalt.get().getCitadelManager();
	private ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
	private BastionManager bm = SerennoCobalt.get().getCitadelManager().getBastionManager();
	private SnitchManager sm = SerennoCobalt.get().getCitadelManager().getSnitchManager();
	private ArtilleryManager am = SerennoCobalt.get().getCitadelManager().getArtilleryManager();
	
	@Subcommand("give")
	public void give(Player player, String thingType, String thingName, int amount) {
		ItemStack giveIt;
		if (thingType.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint rb = rm.getReinforcementBlueprint(thingName);
			
			ViewReinforcementBlueprintEvent event = new ViewReinforcementBlueprintEvent(player, thingName, rb);
			Bukkit.getPluginManager().callEvent(event);
			
			rb = event.getReinforcementBlueprint();

			if (rb == null) {
				player.sendMessage(ChatColor.RED + "Blueprint doesn't exist.");
				return;
			}

			giveIt = new ItemStack(rb.getItemStack());
			giveIt.setAmount(amount);
		} else if (thingType.equalsIgnoreCase("bastion")) {
			BastionBlueprint bb = bm.getBastionBlueprint(thingName);
			
			ViewBastionBlueprintEvent event = new ViewBastionBlueprintEvent(player, thingName, bb);
			Bukkit.getPluginManager().callEvent(event);
			
			bb = event.getBastionBlueprint();

			if (bb == null) {
				player.sendMessage(ChatColor.RED + "Blueprint doesn't exist.");
				return;
			}

			giveIt = new ItemStack(bb.getItemStack());
			giveIt.setAmount(amount);
		} else if (thingType.equalsIgnoreCase("artillery")) {
			thingName = thingName.toUpperCase();
			ArtilleryType artilleryType = ArtilleryType.valueOf(thingName);
			if (artilleryType == null) {
				player.sendMessage(ChatColor.RED + "That is not a valid artillery type.");
				return;
			}
			giveIt = artilleryType.getNewItem();
		} else {
			player.sendMessage(ChatColor.RED + "Citadel doesn't have that thing to give you.");
			return;
		}
		player.getInventory().addItem(giveIt);
	}
	
	@Subcommand("blueprint")
	public void blueprint(Player player, String blueprintType, String blueprintName) {
		if (blueprintType.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(blueprintName);
			
			ViewReinforcementBlueprintEvent event = new ViewReinforcementBlueprintEvent(player, blueprintName, blueprint);
			Bukkit.getPluginManager().callEvent(event);
			
			blueprint = event.getReinforcementBlueprint();
			
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			player.sendMessage(blueprint.toString());
		} else if (blueprintType.equalsIgnoreCase("bastion")) {
			BastionBlueprint blueprint = bm.getBastionBlueprint(blueprintName);
			
			ViewBastionBlueprintEvent event = new ViewBastionBlueprintEvent(player, blueprintName, blueprint);
			Bukkit.getPluginManager().callEvent(event);
			
			blueprint = event.getBastionBlueprint();
			
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			player.sendMessage(blueprint.toString());
		} else {
			player.sendMessage(ChatColor.RED + "That is an invalid blueprint type.");
		}
	}
	
	@Subcommand("blueprint create")
	public void createBlueprint(Player player, String blueprintName, String blueprintType) {
		ItemStack item = player.getInventory().getItemInMainHand();
		if (blueprintType.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
					.name(blueprintName)
					.displayName(ChatColor.GRAY + "Stone")
					.defaultDamage(1)
					.acidTime(1000 * 60 * 5)
					.maturationTime(1000 * 60 * 1)
					.damageCooldown(0)
					.itemStack(item)
					.regenRate(new RegenRate(1, 60000))
					.maxHealth(50)
					.unreinforceableMaterials(Arrays.asList(Material.WEB))
					.build();
			
			ReinforcementBlueprintCreationEvent event = new ReinforcementBlueprintCreationEvent(player, rb);
			Bukkit.getPluginManager().callEvent(event);
			
			rb = event.getReinforcementBlueprint();
			
			if (!event.isCancelled()) {
				rm.registerReinforcementBlueprint(rb);
			}
			player.sendMessage(ChatColor.YELLOW + "You have created a new reinforcement blueprint:");
			player.sendMessage(rb.toString());
		} else if (blueprintType.equalsIgnoreCase("bastion")) {
			BastionBlueprint bb = BastionBlueprint.builder()
					.name(blueprintName)
					.displayName(ChatColor.DARK_RED + "Vault Bastion")
					.itemStack(new ItemStackBuilder(Material.SPONGE, 1)
							.meta(new ItemMetaBuilder()
									.name(ChatColor.DARK_RED + "Vault Bastion"))
							.build())
					.pearlConfig(PearlConfig.builder()
							.consumeOnBlock(false)
							.block(true)
							.blockMidAir(false)
							.damage(2)
							.build())
					.requiresMaturity(false)
					.shape(BastionShape.SQUARE)
					.radius(10)
					.build();
			
			BastionBlueprintCreationEvent event = new BastionBlueprintCreationEvent(player, bb);
			Bukkit.getPluginManager().callEvent(event);
			
			bb = event.getBastionBlueprint();
			
			if (!event.isCancelled()) {
				bm.registerBastionBlueprint(bb);
			}
			player.sendMessage(ChatColor.YELLOW + "You have created a new bastion blueprint:");
			player.sendMessage(bb.toString());
		} else {
			player.sendMessage(ChatColor.RED + "That is not a valid type of blueprint.");
		}
	}
	
	@Subcommand("blueprint delete")
	public void deleteBlueprint(Player player, String blueprintName, String blueprintType) {
		if (blueprintType.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint rb = rm.getReinforcementBlueprint(blueprintName);
			
			ReinforcementBlueprintDeletionEvent event = new ReinforcementBlueprintDeletionEvent(player, blueprintName, rb);
			Bukkit.getPluginManager().callEvent(event);
			
			rb = event.getReinforcementBlueprint();
			
			if (rb == null) {
				return;
			}
			
			if (!event.isCancelled()) {
				rm.unregisterReinforcementBlueprint(rb);
			}
			player.sendMessage(ChatColor.YELLOW + "You have deleted a reinforcement blueprint:" + rb.getName());
		} else if (blueprintType.equalsIgnoreCase("bastion")) {
			BastionBlueprint bb = bm.getBastionBlueprint(blueprintName);
			
			BastionBlueprintDeletionEvent event = new BastionBlueprintDeletionEvent(player, blueprintName, bb);
			Bukkit.getPluginManager().callEvent(event);
			
			bb = event.getBastionBlueprint();
			
			if (bb == null) {
				return;
			}
			
			if (!event.isCancelled()) {
				bm.unregisterBastionBlueprint(bb);
			}
			player.sendMessage(ChatColor.YELLOW + "You have deleted a bastion blueprint: " + bb.getName());
		} else {
			player.sendMessage(ChatColor.RED + "That is not a valid type of blueprint.");
		}
	}
	
	@Subcommand("blueprints")
	public void listBlueprints(Player player) {
		Collection<ReinforcementBlueprint> reinforcementBlueprints = rm.getReinforcementBlueprints();
		Collection<BastionBlueprint> bastionBlueprints = bm.getBastionBlueprints();
		
		ListBlueprintsEvent event = new ListBlueprintsEvent(player, reinforcementBlueprints, bastionBlueprints);
		Bukkit.getPluginManager().callEvent(event);
		
		reinforcementBlueprints = event.getReinforcementBlueprints();
		bastionBlueprints = event.getBastionBlueprints();
		
		player.sendMessage(ChatColor.GOLD + "Reinforcement Blueprints");
		player.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "-----------------");
		for (ReinforcementBlueprint blueprint : reinforcementBlueprints) {
			player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + blueprint.getName());
		}
		player.sendMessage(ChatColor.GOLD + "Bastion Blueprints");
		player.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "-----------------");
		for (BastionBlueprint blueprint : bastionBlueprints) {
			player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + blueprint.getName());
		}
	}
	
	@Subcommand("blueprint displayName")
	public void blueprintDisplayName(Player player, String name, String type, String displayName) {
		if (type.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
			
			ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
			Bukkit.getPluginManager().callEvent(event);
			
			blueprint = event.getReinforcementBlueprint();
			
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			blueprint.setDisplayName(displayName);
			
			ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
			Bukkit.getPluginManager().callEvent(updateEvent);
			
			player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " display name to " + ChatColor.RESET + displayName);
		} else if (type.equalsIgnoreCase("bastion")) {
			BastionBlueprint blueprint = bm.getBastionBlueprint(name);
			
			BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
			Bukkit.getPluginManager().callEvent(event);
			
			blueprint = event.getBastionBlueprint();
			
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			blueprint.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
			
			BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
			Bukkit.getPluginManager().callEvent(updateEvent);
			
			player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " display name to " + displayName);
		}
	}
	
	@Subcommand("blueprint defaultDamage")
	public void blueprintDefaultDamage(Player player, String name, double defDamage) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setDefaultDamage(defDamage);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " default damage to " + ChatColor.GOLD + defDamage);
	}
	
	@Subcommand("blueprint regenRate amount")
	public void blueprintRegenRateAmount(Player player, String name, double regenRateAmount) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		RegenRate regen = blueprint.getRegenRate();
		regen.setAmount(regenRateAmount);
		blueprint.setRegenRate(regen);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " regen rate amount to " + ChatColor.GOLD + regenRateAmount);
	}
	
	@Subcommand("blueprint regenRate interval")
	public void blueprintRegenRateInterval(Player player, String name, long regenRateInterval) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		RegenRate regen = blueprint.getRegenRate();
		regen.setInterval(regenRateInterval);
		blueprint.setRegenRate(regen);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " regen rate interval to " + ChatColor.GOLD + regenRateInterval);
	}
	
	@Subcommand("blueprint acidTime")
	public void blueprintAcidTime(Player player, String name, long acidTime) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setAcidTime(acidTime);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " acid time to " + ChatColor.GOLD + acidTime);
	}
	
	@Subcommand("blueprint damageCooldown")
	public void blueprintDamageCooldown(Player player, String name, long damageCooldown) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setDamageCooldown(damageCooldown);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " damage cooldown to " + ChatColor.GOLD + damageCooldown);
	}
	
	@Subcommand("blueprint maturationTime")
	public void blueprintMaturationTime(Player player, String name, long maturationTime) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setMaturationTime(maturationTime);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " maturation time to " + ChatColor.GOLD + maturationTime);
	}
	
	@Subcommand("blueprint maturationScale")
	public void blueprintMaturationScale(Player player, String name, double maturationScale) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setMaturationScale(maturationScale);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " maturation scale to " + ChatColor.GOLD + maturationScale);
	}
	
	@Subcommand("blueprint maxHealth")
	public void blueprintMaxHealth(Player player, String name, double maxHealth) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		
		ReinforcementBlueprintModifyEvent event = new ReinforcementBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getReinforcementBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setMaxHealth(maxHealth);
		
		ReinforcementBlueprintUpdateEvent updateEvent = new ReinforcementBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " max health to " + ChatColor.GOLD + maxHealth);
	}
	
	@Subcommand("blueprint radius")
	public void blueprintRadius(Player player, String name, int radius) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setRadius(radius);
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " radius to " + ChatColor.GOLD + radius);
	}
	
	@Subcommand("blueprint shape")
	public void blueprintShape(Player player, String name, String shape) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		BastionShape bastionShape = BastionShape.valueOf(shape.toUpperCase());
		if (bastionShape == null) {
			player.sendMessage(ChatColor.RED + "That is not a valid bastion shape.");
			return;
		}
		blueprint.setShape(bastionShape);
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " shape to " + ChatColor.GOLD + bastionShape);
	}
	
	@Subcommand("blueprint requiresMaturity")
	public void blueprintRequiresMaturity(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setRequiresMaturity(!blueprint.requiresMaturity());
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " requires maturity to " + ChatColor.GOLD + blueprint.requiresMaturity());
	}
	
	@Subcommand("blueprint pearlConfig block")
	public void blueprintPearlBlock(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlBlock(!blueprint.getPearlConfig().block());
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl block to " + ChatColor.GOLD + blueprint.getPearlConfig().block());
	}
	
	@Subcommand("blueprint pearlConfig blockMidAir")
	public void blueprintPearlBlockMidAir(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlBlockMidAir(!blueprint.getPearlConfig().blockMidAir());
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl block mid air to " + ChatColor.GOLD + blueprint.getPearlConfig().blockMidAir());
	}
	
	@Subcommand("blueprint pearlConfig consumeOnBlock")
	public void blueprintPearlConsumeOnBlock(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlConsumeOnBlock(!blueprint.getPearlConfig().consumeOnBlock());
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl consume on block to " + ChatColor.GOLD + blueprint.getPearlConfig().consumeOnBlock());
	}
	
	@Subcommand("blueprint pearlConfig requiresMaturity")
	public void blueprintPearlRequiresMaturity(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlRequiresMaturity(!blueprint.getPearlConfig().requiresMaturity());
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl consume on block to " + ChatColor.GOLD + blueprint.getPearlConfig().consumeOnBlock());
	}
	
	@Subcommand("blueprint pearlConfig damage")
	public void blueprintPearlDamage(Player player, String name, double damage) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		
		BastionBlueprintModifyEvent event = new BastionBlueprintModifyEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(event);
		
		blueprint = event.getBastionBlueprint();
		
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlDamage(damage);
		
		BastionBlueprintUpdateEvent updateEvent = new BastionBlueprintUpdateEvent(player, name, blueprint);
		Bukkit.getPluginManager().callEvent(updateEvent);
		
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl damage to " + ChatColor.GOLD + blueprint.getPearlConfig().getDamage());
	}

	@Subcommand("stats")
	public void stats(Player player) {
		World world = player.getWorld();
		ReinforcementWorld reinforcementWorld = rm.getReinforcementWorld(world);
		BastionWorld bastionWorld = bm.getBastionWorld(world);
		SnitchWorld snitchWorld = sm.getSnitchWorld(world);
		ArtilleryWorld artilleryWorld = am.getArtilleryWorld(world);

		player.sendMessage(ChatColor.YELLOW + "Statistics");
		player.sendMessage(ChatColor.GOLD + "=======================");
		player.sendMessage(ChatColor.GOLD + "Loaded reinforcements in world: " + ChatColor.YELLOW + reinforcementWorld.getAllReinforcements().size());
		player.sendMessage(ChatColor.GOLD + "Loaded bastions in world: " + ChatColor.YELLOW + bastionWorld.getAllBastions().size());
		player.sendMessage(ChatColor.GOLD + "Loaded snitches in world: " + ChatColor.YELLOW + snitchWorld.getAllSnitches().size());
		player.sendMessage(ChatColor.GOLD + "Loaded artilleries in world: " + ChatColor.YELLOW + artilleryWorld.getAllArtilleries().size());
	}
	
}
