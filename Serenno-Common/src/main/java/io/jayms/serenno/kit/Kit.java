package io.jayms.serenno.kit;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.jayms.serenno.armourtype.ArmorEquipEvent;
import io.jayms.serenno.armourtype.ArmorEquipEvent.EquipMethod;
import io.jayms.serenno.armourtype.ArmorType;
import io.jayms.serenno.util.MaterialTools;

public class Kit {

	public static Kit fromBase64(String b64) throws IOException {
		return new Kit(ItemStackDecoder.itemStackArrayFromBase64(b64));
	}
	
	private ItemStack[] contents;
	
	public Kit() {
		contents = new ItemStack[41];
	}
	
	public Kit(ItemStack[] contents) {
		this.contents = contents;
	}
	
	public Kit(Player player) {
		contents = player.getInventory().getContents();
	}
	
	public ItemStack helmet() {
		return contents[39];
	}
	
	public Kit helmet(ItemStack helm) {
		contents[39] = helm;
		return this;
	}
	
	public ItemStack chestplate() {
		return contents[38];
	}
	
	public Kit chestplate(ItemStack chest) {
		contents[38] = chest;
		return this;
	}
	
	public ItemStack leggings() {
		return contents[37];
	}
	
	public Kit leggings(ItemStack legs) {
		contents[37] = legs;
		return this;
	}
	
	public ItemStack boots() {
		return contents[36];
	}
	
	public Kit boots(ItemStack boots) {
		contents[36] = boots;
		return this;
	}
	
	public Kit range(int lower, int upper, ItemStack it) {
		for (int i = lower; i < upper; i++) {
			contents[i] = it;
		}
		return this;
	}
	
	public Kit set(int i, ItemStack it) {
		contents[i] = it;
		return this;
	}
	
	public Kit offhand(ItemStack it) {
		contents[40] = it;
		return this;
	}
	
	public ItemStack[] armour() {
		ItemStack[] armour = new ItemStack[4];
		armour[0] = contents[36];
		armour[1] = contents[37];
		armour[2] = contents[38];
		armour[3] = contents[39];
		return armour;
	}
	
	public ItemStack mainHand() {
		return contents[0];
	}
	
	public ItemStack offhand() {
		return contents[40];
	}
	
	public ItemStack[] contents() {
		ItemStack[] result = new ItemStack[36];
		for (int i = 0; i < result.length; i++) {
			result[i] = contents[i];
		}
		return result;
	}
	
	public void load(Player player) {
		PlayerInventory inv = player.getInventory();
		ItemStack oldHelm = inv.getHelmet();
		ItemStack oldChest = inv.getChestplate();
		ItemStack oldLegs = inv.getLeggings();
		ItemStack oldBoots = inv.getBoots();
		ItemStack[] newArmour = armour();
		ItemStack newHelm = newArmour[3];
		ItemStack newChest = newArmour[2];
		ItemStack newLegs = newArmour[1];
		ItemStack newBoots = newArmour[0];
		
		if (MaterialTools.isHelmet(newHelm) || MaterialTools.isHelmet(oldHelm)) {
			ArmorEquipEvent armorEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.HELMET, oldHelm, newHelm);
			Bukkit.getPluginManager().callEvent(armorEvent);
			if (!armorEvent.isCancelled()) {
				inv.setHelmet(newHelm);
			}
		}
		if (MaterialTools.isChestplate(newChest) || MaterialTools.isChestplate(oldChest)) {
			ArmorEquipEvent armorEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.CHESTPLATE, oldChest, newChest);
			Bukkit.getPluginManager().callEvent(armorEvent);
			if (!armorEvent.isCancelled()) {
				inv.setChestplate(newChest);
			}
		}
		if (MaterialTools.isLeggings(newLegs) || MaterialTools.isLeggings(oldLegs)) {
			ArmorEquipEvent armorEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.LEGGINGS, oldLegs, newLegs);
			Bukkit.getPluginManager().callEvent(armorEvent);
			if (!armorEvent.isCancelled()) {
				inv.setLeggings(newLegs);
			}
		}
		if (MaterialTools.isBoots(newBoots) || MaterialTools.isLeggings(oldBoots)) {
			ArmorEquipEvent armorEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.BOOTS, oldBoots, newBoots);
			Bukkit.getPluginManager().callEvent(armorEvent);
			if (!armorEvent.isCancelled()) {
				inv.setBoots(newBoots);
			}
		}
		
		inv.setContents(contents);
		player.updateInventory();
	}
	
	public String toBase64() {
		return ItemStackEncoder.itemStackArrayToBase64(contents);
	}
}
