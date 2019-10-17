package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.boydti.fawe.object.schematic.Schematic;
import com.github.maxopoly.finale.classes.engineer.EngineerPlayer;

import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import vg.civcraft.mc.civmodcore.locations.QTBox;

public interface Artillery extends QTBox {

	int getID();
	
	String getName();
	
	String getDisplayName();
	
	ArtilleryCrate getCrate();
	
	void setDirection(BlockFace dir);
	
	BlockFace getDirection();
	
	Location getLocation();
	
	Reinforcement getReinforcement();
	
	Schematic getSchematic();
	
	void assemble(EngineerPlayer player);
	
	void disassemble();
	
	void fire(EngineerPlayer player);
	
	long getCooldown();
	
	double getReinforcementDamage();
	
	double getBastionDamage();
	
	boolean isAssembled();
	
	Menu getInterface();
	
	void dealBlockDamage(Player player);
	
	long getBlockDamageCooldown();
	
	int getUpperY();
	
	int getLowerY();
	
}
