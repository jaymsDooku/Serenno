package io.jayms.serenno.util;

import java.util.Random;

import org.bukkit.util.Vector;

public class MathTools {

	private static final Random r = new Random();
	
	public static double random(double rangeMin, double rangeMax) {
		return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
	}
	
	public static Vector rotateXZ(final Vector vec, final double theta) {
		final Vector vec2 = vec.clone();
		final double x = vec2.getX();
		final double z = vec2.getZ();
		vec2.setX(x * Math.cos(Math.toRadians(theta)) - z * Math.sin(Math.toRadians(theta)));
		vec2.setZ(x * Math.sin(Math.toRadians(theta)) + z * Math.cos(Math.toRadians(theta)));
		return vec2;
	}
	
	public static final Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static final Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static final Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }
	
	public static int ceil(double i, int v){
	    return (int) (Math.ceil(i/v) * v);
	}
	
}
