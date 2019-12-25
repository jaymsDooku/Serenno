package io.jayms.serenno.model.citadel.artillery.cannon;

import com.boydti.fawe.object.schematic.Schematic;
import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.artillery.AbstractArtillery;
import io.jayms.serenno.model.citadel.artillery.ArtilleryCrate;
import net.md_5.bungee.api.ChatColor;

public class Cannon extends AbstractArtillery {
	
	public static final String NAME = "cannon";
	public static final String DISPLAY_NAME = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Cannon";

	public Cannon(ArtilleryCrate crate) {
		super(crate);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public Schematic getSchematic() {
		return config.getCannonSchematic();
	}

	@Override
	public boolean fire(EngineerPlayer player) {
		return true;
	}

	@Override
	public long getCooldown() {
		return config.getCannonCooldown();
	}

	@Override
	public double getReinforcementDamage() {
		return config.getCannonReinforcementDamage();
	}

	@Override
	public double getBastionDamage() {
		return config.getCannonBastionDamage();
	}

	@Override
	public Menu getInterface() {
		return SerennoCobalt.get().getCitadelManager().getArtilleryManager().getTrebuchetMenu();
	}

	@Override
	public long getBlockDamageCooldown() {
		return config.getCannonBlockDamageCD();
	}

	@Override
	public int getUpperY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLowerY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtXMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtXMid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtXMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtZMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtZMid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int qtZMax() {
		// TODO Auto-generated method stub
		return 0;
	}

}
