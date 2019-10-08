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
		return true;
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
