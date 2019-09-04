package io.jayms.serenno.util;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Sets;

public class PotionEffectCache {

	private Set<PotionEffect> effects;
	
	public PotionEffectCache(Player player) {
		effects = Sets.newHashSet(player.getActivePotionEffects());
	}
	
	public void restore(Player player) {
		PlayerTools.clearEffects(player);
		player.addPotionEffects(effects);
	}
}
