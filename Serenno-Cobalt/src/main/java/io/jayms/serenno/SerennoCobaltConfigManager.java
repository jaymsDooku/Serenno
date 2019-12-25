package io.jayms.serenno;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class SerennoCobaltConfigManager extends CoreConfigManager {
	
	public SerennoCobaltConfigManager(ACivMod plugin) {
		super(plugin);
	}
	
	@Override
	protected boolean parseInternal(ConfigurationSection config) {
		String schemName = config.getString("artillery.trebuchet.schematic", "trebuchet.schematic");
		File schemFolder = new File(plugin.getDataFolder(), "schematics");
		if (!schemFolder.exists()) {
			schemFolder.mkdir();
		}
		File schemFile = new File(schemFolder, schemName);
		if (!schemFile.exists()) {
			plugin.warning("Schematic file for trebuchet doesn't exist.");
			return false;
		}
		
		try {
			trebuchetSchematic = ClipboardFormats.findByFile(schemFile).load(schemFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cannonForwardLength = config.getInt("artillery.cannon.length.forward", 11);
		cannonBackwardLength = config.getInt("artillery.cannon.length.backward", 10);
		cannonLeftWidth = config.getInt("artillery.cannon.width.left", 10);
		cannonRightWidth = config.getInt("artillery.cannon.width.right", 10);
		cannonHeight = config.getInt("artillery.cannon.height", 17);
		
		cannonHorizontalOffset = config.getInt("artillery.cannon.firingPoint.horizontalOffset", 3);
		cannonVerticalOffset = config.getInt("artillery.cannon.firingPoint.verticalOffset", 14);
		
		cannonCooldown = config.getLong("artillery.cannon.cooldown", 6000);
		cannonImpactRadius = config.getInt("artillery.cannon.missile.impactRadius", 4);
		cannonPlayerDamage = config.getInt("artillery.cannon.missile.playerDamage", 5);
		cannonBastionDamage = config.getDouble("artillery.cannon.missile.bastionDamage", 5);
		cannonReinforcementDamage = config.getDouble("artillery.cannon.missile.reinforcementDamage", 5);
		
		cannonBlockDamageCD = config.getLong("artillery.cannon.blockDamageCD", 2500);
		
		trebuchetForwardLength = config.getInt("artillery.trebuchet.length.forward", 11);
		trebuchetBackwardLength = config.getInt("artillery.trebuchet.length.backward", 10);
		trebuchetLeftWidth = config.getInt("artillery.trebuchet.width.left", 10);
		trebuchetRightWidth = config.getInt("artillery.trebuchet.width.right", 10);
		trebuchetHeight = config.getInt("artillery.trebuchet.height", 17);
		
		trebuchetHorizontalOffset = config.getInt("artillery.trebuchet.pointOfRotation.horizontalOffset", 3);
		trebuchetVerticalOffset = config.getInt("artillery.trebuchet.pointOfRotation.verticalOffset", 14);
		
		trebuchetCooldown = config.getLong("artillery.trebuchet.cooldown", 6000);
		trebuchetImpactRadius = config.getInt("artillery.trebuchet.missile.impactRadius", 4);
		trebuchetPlayerDamage = config.getInt("artillery.trebuchet.missile.playerDamage", 5);
		trebuchetBastionDamage = config.getDouble("artillery.trebuchet.missile.bastionDamage", 5);
		trebuchetReinforcementDamage = config.getDouble("artillery.trebuchet.missile.reinforcementDamage", 5);
		
		trebuchetBlockDamageCD = config.getLong("artillery.trebuchet.blockDamageCD", 2500);
		
		defaultReinforcementWorld = config.getString("citadel.reinforcement.defaultWorld", "world");
		return true;
	}
	
	private String defaultReinforcementWorld;
	
	public String getDefaultReinforcementWorld() {
		return defaultReinforcementWorld;
	}
	
	private Schematic cannonSchematic;
	private int cannonRightWidth;
	private int cannonLeftWidth;
	private int cannonForwardLength;
	private int cannonBackwardLength;
	private int cannonHeight;
	
	private int cannonHorizontalOffset;
	private int cannonVerticalOffset;
	
	private long cannonCooldown;
	private int cannonImpactRadius;
	private double cannonImpactHorizontal;
	private double cannonImpactVertical;
	private double cannonPlayerDamage;
	private double cannonBastionDamage;
	private double cannonReinforcementDamage;
	
	private long cannonBlockDamageCD;
	
	public long getCannonBlockDamageCD() {
		return cannonBlockDamageCD;
	}
	
	public Schematic getCannonSchematic() {
		return cannonSchematic;
	}

	public int getCannonRightWidth() {
		return cannonRightWidth;
	}

	public int getCannonLeftWidth() {
		return cannonLeftWidth;
	}

	public int getCannonForwardLength() {
		return cannonForwardLength;
	}

	public int getCannonBackwardLength() {
		return cannonBackwardLength;
	}

	public int getCannonHeight() {
		return cannonHeight;
	}

	public int getCannonHorizontalOffset() {
		return cannonHorizontalOffset;
	}

	public int getCannonVerticalOffset() {
		return cannonVerticalOffset;
	}

	public long getCannonCooldown() {
		return cannonCooldown;
	}

	public int getCannonImpactRadius() {
		return cannonImpactRadius;
	}

	public double getCannonImpactHorizontal() {
		return cannonImpactHorizontal;
	}

	public double getCannonImpactVertical() {
		return cannonImpactVertical;
	}

	public double getCannonPlayerDamage() {
		return cannonPlayerDamage;
	}

	public double getCannonBastionDamage() {
		return cannonBastionDamage;
	}

	public double getCannonReinforcementDamage() {
		return cannonReinforcementDamage;
	}

	private Schematic trebuchetSchematic;
	private int trebuchetRightWidth;
	private int trebuchetLeftWidth;
	private int trebuchetForwardLength;
	private int trebuchetBackwardLength;
	private int trebuchetHeight;
	
	private int trebuchetHorizontalOffset;
	private int trebuchetVerticalOffset;
	
	private long trebuchetCooldown;
	private int trebuchetImpactRadius;
	private double trebuchetImpactHorizontal;
	private double trebuchetImpactVertical;
	private double trebuchetPlayerDamage;
	private double trebuchetBastionDamage;
	private double trebuchetReinforcementDamage;
	
	private long trebuchetBlockDamageCD;
	
	public double getTrebuchetImpactHorizontal() {
		return trebuchetImpactHorizontal;
	}
	
	public double getTrebuchetImpactVertical() {
		return trebuchetImpactVertical;
	}
	
	public int getTrebuchetImpactRadius() {
		return trebuchetImpactRadius;
	}
	
	public long getTrebuchetBlockDamageCD() {
		return trebuchetBlockDamageCD;
	}
	
	public double getTrebuchetBastionDamage() {
		return trebuchetBastionDamage;
	}
	
	public double getTrebuchetReinforcementDamage() {
		return trebuchetReinforcementDamage;
	}
	
	public double getTrebuchetPlayerDamage() {
		return trebuchetPlayerDamage;
	}
	
	public long getTrebuchetCooldown() {
		return trebuchetCooldown;
	}
	
	public int getTrebuchetHorizontalOffset() {
		return trebuchetHorizontalOffset;
	}
	
	public int getTrebuchetVerticalOffset() {
		return trebuchetVerticalOffset;
	}
	
	public Schematic getTrebuchetSchematic() {
		return trebuchetSchematic;
	}

	public int getTrebuchetRightWidth() {
		return trebuchetRightWidth;
	}

	public int getTrebuchetLeftWidth() {
		return trebuchetLeftWidth;
	}

	public int getTrebuchetForwardLength() {
		return trebuchetForwardLength;
	}

	public int getTrebuchetBackwardLength() {
		return trebuchetBackwardLength;
	}

	public int getTrebuchetHeight() {
		return trebuchetHeight;
	}
	
}
