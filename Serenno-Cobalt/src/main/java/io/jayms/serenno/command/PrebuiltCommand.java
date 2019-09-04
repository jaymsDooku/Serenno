package io.jayms.serenno.command;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.PrebuiltManager;
import io.jayms.serenno.model.prebuilt.PrebuiltStructure;

@CommandAlias("prebuilt")
public class PrebuiltCommand extends BaseCommand {

	private PrebuiltManager pm = SerennoCobalt.get().getPrebuiltManager();
	
	@Subcommand("save")
	public void save(Player player, String pbName) {
		pm.save(player, pbName);
	}
	
	@Subcommand("load")
	public void load(Player player, String pbName) {
		PrebuiltStructure ps = pm.load(player, pbName);
		
	}
	
}
