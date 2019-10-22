package io.jayms.serenno.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import io.jayms.serenno.model.citadel.artillery.ArtilleryType;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

	private ReinforcementManager rm = SerennoCobalt.get().getCitadelManager().getReinforcementManager();
	private BastionManager bm = SerennoCobalt.get().getCitadelManager().getBastionManager();
	
	@Subcommand("give")
	public void give(Player player, String thingType, String thingName, int amount) {
		ItemStack giveIt;
		if (thingType.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint rb = rm.getReinforcementBlueprint(thingName);
			giveIt = rb.getItemStack();
			giveIt.setAmount(amount);
		} else if (thingType.equalsIgnoreCase("bastion")) {
			BastionBlueprint bb = bm.getBastionBlueprint(thingName);
			giveIt = bb.getItemStack();
			giveIt.setAmount(amount);
		} else if (thingType.equalsIgnoreCase("artillery")) {
			ArtilleryCrate crate = ArtilleryType.TREBUCHET.getArtilleryCrate();
			giveIt = crate.getItemStack();
		} else {
			player.sendMessage(ChatColor.RED + "Citadel doesn't have that thing to give you.");
			return;
		}
		player.getInventory().addItem(giveIt);
	}
	
	@Subcommand("blueprint create")
	public void blueprintCreate(Player player, String name, String type) {
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (type.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint blueprint = ReinforcementBlueprint.builder()
					.name(name)
					.displayName(name)
					.defaultDamage(1)
					.acidTime(1000 * 20)
					.damageCooldown(0)
					.itemStack(item)
					.regenRate(new RegenRate(1, 1000 * 60 * 5))
					.maxHealth(50)
					.build();
			rm.registerReinforcementBlueprint(blueprint);
			player.sendMessage(ChatColor.YELLOW + "You have created a reinforcement blueprint: " + ChatColor.GOLD + name);
		} else if (type.equalsIgnoreCase("bastion")) {
			BastionBlueprint blueprint = BastionBlueprint.builder()
					.name(name)
					.displayName(name)
					.shape(BastionShape.SQUARE)
					.radius(10)
					.itemStack(item)
					.requiresMaturity(true)
					.pearlConfig(PearlConfig.builder()
							.block(true)
							.blockMidAir(false)
							.consumeOnBlock(false)
							.damage(1)
							.build())
					.build();
			bm.registerBastionBlueprint(blueprint);
			player.sendMessage(ChatColor.YELLOW + "You have created a bastion blueprint: " + ChatColor.GOLD + name);
		}
	}
	
	@Subcommand("blueprint displayName")
	public void blueprintDisplayName(Player player, String name, String type, String displayName) {
		if (type.equalsIgnoreCase("reinforcement")) {
			ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			blueprint.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
			player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " display name to " + ChatColor.RESET + displayName);
		} else if (type.equalsIgnoreCase("bastion")) {
			BastionBlueprint blueprint = bm.getBastionBlueprint(name);
			if (blueprint == null) {
				player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
				return;
			}
			blueprint.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
			player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " display name to " + ChatColor.RESET + displayName);
		}
	}
	
	@Subcommand("blueprint defaultDamage")
	public void blueprintDefaultDamage(Player player, String name, double defDamage) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setDefaultDamage(defDamage);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " default damage to " + ChatColor.GOLD + defDamage);
	}
	
	@Subcommand("blueprint regenRate amount")
	public void blueprintRegenRateAmount(Player player, String name, double regenRateAmount) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		RegenRate regen = blueprint.getRegenRate();
		regen.setAmount(regenRateAmount);
		blueprint.setRegenRate(regen);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " regen rate amount to " + ChatColor.GOLD + regenRateAmount);
	}
	
	@Subcommand("blueprint regenRate interval")
	public void blueprintRegenRateInterval(Player player, String name, long regenRateInterval) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		RegenRate regen = blueprint.getRegenRate();
		regen.setInterval(regenRateInterval);
		blueprint.setRegenRate(regen);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " regen rate interval to " + ChatColor.GOLD + regenRateInterval);
	}
	
	@Subcommand("blueprint acidTime")
	public void blueprintAcidTime(Player player, String name, long acidTime) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setAcidTime(acidTime);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " acid time to " + ChatColor.GOLD + acidTime);
	}
	
	@Subcommand("blueprint damageCooldown")
	public void blueprintDamageCooldown(Player player, String name, long damageCooldown) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setAcidTime(damageCooldown);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " damage cooldown to " + ChatColor.GOLD + damageCooldown);
	}
	
	@Subcommand("blueprint maturationTime")
	public void blueprintMaturationTime(Player player, String name, long maturationTime) {
		ReinforcementBlueprint blueprint = rm.getReinforcementBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setMaturationTime(maturationTime);
		player.sendMessage(ChatColor.YELLOW + "You have set reinforcement blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " maturation time to " + ChatColor.GOLD + maturationTime);
	}
	
	@Subcommand("blueprint radius")
	public void blueprintRadius(Player player, String name, int radius) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setRadius(radius);
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " radius to " + ChatColor.GOLD + radius);
	}
	
	@Subcommand("blueprint shape")
	public void blueprintShape(Player player, String name, String shape) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
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
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " shape to " + ChatColor.GOLD + bastionShape);
	}
	
	@Subcommand("blueprint requiresMaturity")
	public void blueprintRequiresMaturity(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setRequiresMaturity(!blueprint.requiresMaturity());
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " requires maturity to " + ChatColor.GOLD + blueprint.requiresMaturity());
	}
	
	@Subcommand("blueprint pearlConfig block")
	public void blueprintPearlBlock(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlBlock(!blueprint.getPearlConfig().block());
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl block to " + ChatColor.GOLD + blueprint.getPearlConfig().block());
	}
	
	@Subcommand("blueprint pearlConfig blockMidAir")
	public void blueprintPearlBlockMidAir(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlBlockMidAir(!blueprint.getPearlConfig().blockMidAir());
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl block mid air to " + ChatColor.GOLD + blueprint.getPearlConfig().blockMidAir());
	}
	
	@Subcommand("blueprint pearlConfig consumeOnBlock")
	public void blueprintPearlConsumeOnBlock(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlConsumeOnBlock(!blueprint.getPearlConfig().consumeOnBlock());
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl consume on block to " + ChatColor.GOLD + blueprint.getPearlConfig().consumeOnBlock());
	}
	
	@Subcommand("blueprint pearlConfig requiresMaturity")
	public void blueprintPearlRequiresMaturity(Player player, String name) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlRequiresMaturity(!blueprint.getPearlConfig().requiresMaturity());
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl consume on block to " + ChatColor.GOLD + blueprint.getPearlConfig().consumeOnBlock());
	}
	
	@Subcommand("blueprint pearlConfig damage")
	public void blueprintPearlDamage(Player player, String name, double damage) {
		BastionBlueprint blueprint = bm.getBastionBlueprint(name);
		if (blueprint == null) {
			player.sendMessage(ChatColor.RED + "That blueprint doesn't exist.");
			return;
		}
		blueprint.setPearlDamage(damage);
		player.sendMessage(ChatColor.YELLOW + "You have set bastion blueprint " + ChatColor.GOLD + name + ChatColor.YELLOW + " pearl damage to " + ChatColor.GOLD + blueprint.getPearlConfig().getDamage());
	}
	
}
