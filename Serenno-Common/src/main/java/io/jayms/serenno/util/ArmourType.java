package io.jayms.serenno.util;

import io.jayms.serenno.armourtype.ArmorType;
import org.bukkit.inventory.ItemStack;

public enum ArmourType {

	HELMET(3, ArmorType.HELMET),
	CHEST(2, ArmorType.CHESTPLATE),
	LEGS(1, ArmorType.LEGGINGS),
	BOOTS(0, ArmorType.BOOTS);
	
	private int index;
	private ArmorType armourType;
	
	private ArmourType(int index, ArmorType type) {
		this.armourType = type;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}

	public ArmorType getArmourType() {
		return armourType;
	}

	public static ArmourType getArmourType(ArmorType type) {
		for (ArmourType armourType : values()) {
			if (armourType.getArmourType() == type) {
				return armourType;
			}
		}
		return null;
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
