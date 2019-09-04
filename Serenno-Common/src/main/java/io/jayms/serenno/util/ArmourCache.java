package io.jayms.serenno.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmourCache {
	
	private Map<Material, Armour> armour = new HashMap<>();
	
	public ArmourCache(ItemStack[] contents) {
		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];
			if (it != null) {
				armour.put(it.getType(), new Armour(ItemUtil.getToughness(it), ItemUtil.getArmour(it)));
			}
		}
	}
	
	public ItemStack[] restore(Player player) {
		ItemStack[] contents = player.getEquipment().getArmorContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];
			if (it != null) {
				Armour a = armour.get(it.getType());
				if (a != null) {
					System.out.println("a: " + a);
					contents[i] = ItemUtil.setArmour(it, a.getToughness(), a.getArmour());
				}
			}
		}
		return contents;
	}
	
}
