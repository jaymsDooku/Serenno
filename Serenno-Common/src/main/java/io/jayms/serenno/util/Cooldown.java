package io.jayms.serenno.util;

import java.util.HashMap;
import java.util.Map;

public class Cooldown<K> {

	private final Map<K, Long> cooldowns = new HashMap<>();

	public void putOnCooldown(K key, long duration) {
		cooldowns.put(key, System.currentTimeMillis() + duration);
	}
	
	public void removeCooldown(K key) {
		cooldowns.remove(key);
	}
	
	public boolean isOnCooldown(K key) {
		if (!cooldowns.containsKey(key)) {
			return false;
		}
		
		Long endCooldown = cooldowns.get(key);
		if (System.currentTimeMillis() > endCooldown) {
			cooldowns.remove(key);
			return false;
		}
		return true;
	}
	
	public long getTimeLeft(K key) {
		if (!cooldowns.containsKey(key)) {
			return 0;
		}
		
		Long endCooldown = cooldowns.get(key);
		return endCooldown - System.currentTimeMillis();
	}
	
	public double getReadableTimeLeft(K key) {
		long left = getTimeLeft(key);
		if (left == -1) return 0;
		double dleft = (double) left;
		return (dleft / 1000);
	}
	
}
