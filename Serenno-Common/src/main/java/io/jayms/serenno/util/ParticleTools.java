package io.jayms.serenno.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleTools {

	public static void displayColoredParticle(final Location loc, ParticleEffect type, final String hexVal, final float xOffset, final float yOffset, final float zOffset) {
		int r = 0;
		int g = 0;
		int b = 0;
		if (hexVal.length() <= 6) {
			r = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
			g = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
			b = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
		} else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
			r = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
			g = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
			b = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
		}
		float red = r / 255.0F;
		final float green = g / 255.0F;
		final float blue = b / 255.0F;
		if (red <= 0) {
			red = 1 / 255.0F;
		}
		loc.setX(loc.getX() + Math.random() * xOffset);
		loc.setY(loc.getY() + Math.random() * yOffset);
		loc.setZ(loc.getZ() + Math.random() * zOffset);

		if (type != ParticleEffect.RED_DUST && type != ParticleEffect.REDSTONE && type != ParticleEffect.SPELL_MOB && type != ParticleEffect.MOB_SPELL && type != ParticleEffect.SPELL_MOB_AMBIENT && type != ParticleEffect.MOB_SPELL_AMBIENT) {
			type = ParticleEffect.RED_DUST;
		}
		type.display(red, green, blue, 1F, 0, loc, 255.0);
	}
	
	public static void displayColoredParticle(final Location loc, final String hexVal) {
		displayColoredParticle(loc, ParticleEffect.RED_DUST, hexVal, 0, 0, 0);
	}

	public static void displayColoredParticle(final Location loc, final String hexVal, final float xOffset, final float yOffset, final float zOffset) {
		displayColoredParticle(loc, ParticleEffect.RED_DUST, hexVal, xOffset, yOffset, zOffset);
	}
	
	public static void drawLine(Location start, Location dest, int particles, ParticlePlay play) {
		Vector dir = dest.clone().subtract(start).toVector();
		double length = dir.length();
		double ratio = length / particles;
		Vector d = dir.normalize().multiply(ratio);
		Location loc = start.clone();
		for (int i = 0; i < particles; i++) {
			loc.add(d);
			play.play(loc);
		}
	}
	
	public interface ParticlePlay {
		
		void play(Location loc);
		
	}
	
}
