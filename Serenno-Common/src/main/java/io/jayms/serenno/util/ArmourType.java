package io.jayms.serenno.util;

import org.bukkit.inventory.ItemStack;

public enum ArmourType {

	HELMET(3),
	CHEST(2),
	LEGS(1),
	BOOTS(0);
	
	private int index;
	
	private ArmourType(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static ArmourType getArmourType(ItemStack is) {
		if (ItemUtil.isHelmet(is)) {
			return HELMET;
		} else if (ItemUtil.isChest(is)) {
			return CHEST;
		} else if (ItemUtil.isLegs(is)) {
			return LEGS;
		} else if (ItemUtil.isBoots(is)) {
			return BOOTS;
		}
		return null;
	}
	
}
