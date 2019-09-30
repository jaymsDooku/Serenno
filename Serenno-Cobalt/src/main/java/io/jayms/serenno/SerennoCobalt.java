package io.jayms.serenno;

import org.bukkit.Bukkit;

import co.aikar.commands.PaperCommandManager;
import io.jayms.serenno.command.CitadelCommand;
import io.jayms.serenno.command.CompanyCommand;
import io.jayms.serenno.command.GroupCommand;
import io.jayms.serenno.command.PrebuiltCommand;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.FinanceManager;
import io.jayms.serenno.manager.GroupManager;
import io.jayms.serenno.manager.PrebuiltManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class SerennoCobalt extends ACivMod {
	
	private static SerennoCobalt instance;
	
	public static SerennoCobalt get() {
		return instance;
	}
	
	private PaperCommandManager commandManager;
	
	public PaperCommandManager getCommandManager() {
		return commandManager;
	}

	private FinanceManager financeManager;
	
	public FinanceManager getFinanceManager() {
		return financeManager;
	}
	
	private CitadelManager citadelManager;
	
	public CitadelManager getCitadelManager() {
		return citadelManager;
	}
	
	private GroupManager groupManager;
	
	public GroupManager getGroupManager() {
		return groupManager;
	}
	
	private PrebuiltManager prebuiltManager;
	
	public PrebuiltManager getPrebuiltManager() {
		return prebuiltManager;
	}
	
	private SerennoCobaltConfigManager configManager;
	
	public SerennoCobaltConfigManager getConfigManager() {
		return configManager;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		
		configManager = new SerennoCobaltConfigManager(this);
		if (!configManager.parse()) {
			getLogger().severe("Errors in config file, shutting down");
			Bukkit.shutdown();
			return;
		}
		
		this.financeManager = new FinanceManager();
		this.groupManager = new GroupManager();
		this.citadelManager = new CitadelManager();
		this.prebuiltManager = new PrebuiltManager(citadelManager);
		
		this.commandManager = new PaperCommandManager(this);
		commandManager.enableUnstableAPI("help");
		
		commandManager.registerCommand(new CompanyCommand());
		commandManager.registerCommand(new GroupCommand());
		commandManager.registerCommand(new PrebuiltCommand());
		commandManager.registerCommand(new CitadelCommand());
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	protected String getPluginName() {
		return "Serenno-Cobalt";
	}
	
}
