package io.jayms.serenno.util;

public class Rotation {

	private float yaw;
	private float pitch;
	
	public Rotation(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
}
