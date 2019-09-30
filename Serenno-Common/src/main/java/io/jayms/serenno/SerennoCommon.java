package io.jayms.serenno;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;

import io.jayms.serenno.armourtype.ArmorListener;
import io.jayms.serenno.db.DBManager;
import io.jayms.serenno.player.CommonPlayerManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class SerennoCommon extends ACivMod {

	public static File getSchematicsFolder() {
		File schemFolder = new File(get().getDataFolder(), "schematics");
		if (!schemFolder.exists()) {
			if (schemFolder.mkdir()) {
				get().getLogger().info("Created new schematics folder.");
			}
		}
		return schemFolder;
	}
	
	private static SerennoCommon instance;
	
	public static SerennoCommon get() {
		return instance;
	}
	
	private SerennoCommonConfigManager configManager;
	
	public SerennoCommonConfigManager getConfigManager() {
		return configManager;
	}
	
	private DBManager dbManager;
	
	public DBManager getDBManager() {
		return dbManager;
	}
	
	private CommonPlayerManager commonPlayerManager;
	
	public CommonPlayerManager getCommonPlayerManager() {
		return commonPlayerManager;
	}
	
	private SerennoCommonListener commonListener;
	
	public SerennoCommonListener getCommonListener() {
		return commonListener;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		
		configManager = new SerennoCommonConfigManager(this);
		if (!configManager.parse()) {
			getLogger().severe("Errors in config file, shutting down");
			Bukkit.shutdown();
			return;
		}
		
		dbManager = new DBManager();
		dbManager.establishConnection();
		
		commonPlayerManager = new CommonPlayerManager(this);
		
		getServer().getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), this);
		getServer().getPluginManager().registerEvents(commonListener = new SerennoCommonListener(configManager), this);
		
		ProtocolLibrary.getProtocolManager().addPacketListener(
			      new PacketAdapter(
			      this, 
			      ListenerPriority.NORMAL, 
			      Arrays.asList(new PacketType[] { PacketType.Status.Server.OUT_SERVER_INFO }), 
			      new ListenerOptions[] { ListenerOptions.ASYNC })
			      {
			        public void onPacketSending(PacketEvent event)
			        {
			        	WrappedServerPing serverPing = (WrappedServerPing)event.getPacket().getServerPings().read(0); 
			        	serverPing.setPlayers(Arrays.asList(new WrappedGameProfile("1", "long live wp, death to nox")));
			        }
			      });
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	protected String getPluginName() {
		return "Serenno-Common";
	}
}
