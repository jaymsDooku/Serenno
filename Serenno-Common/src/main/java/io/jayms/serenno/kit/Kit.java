package io.jayms.serenno.kit;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.jayms.serenno.armourtype.ArmorEquipEvent;
import io.jayms.serenno.armourtype.ArmorEquipEvent.EquipMethod;
import io.jayms.serenno.armourtype.ArmorType;

public class Kit {

	public static Kit fromBase64(String b64) throws IOException {
		return new Kit(ItemStackDecoder.itemStackArrayFromBase64(b64));
	}
	
	private int heldSlot = -1;
	private ItemStack[] contents;
	
	public Kit() {
		contents = new ItemStack[41];
	}
	
	public Kit(ItemStack[] contents) {
		this.contents = contents;
	}
	
	public Kit(Player player) {
		heldSlot = player.getInventory().getHeldItemSlot();
		contents = player.getInventory().getContents();
	}
	
	public int getHeldSlot() {
		return heldSlot;
	}
	
	public void setHeldSlot(int heldSlot) {
		this.heldSlot = heldSlot;
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
		
		ArmorEquipEvent helmetEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.HELMET, oldHelm, newHelm);
		Bukkit.getPluginManager().callEvent(helmetEvent);
		if (!helmetEvent.isCancelled()) {
			inv.setHelmet(newHelm);
		}
		ArmorEquipEvent chestEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.CHESTPLATE, oldChest, newChest);
		Bukkit.getPluginManager().callEvent(chestEvent);
		if (!chestEvent.isCancelled()) {
			inv.setChestplate(newChest);
		}
		ArmorEquipEvent leggingsEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.LEGGINGS, oldLegs, newLegs);
		Bukkit.getPluginManager().callEvent(leggingsEvent);
		if (!leggingsEvent.isCancelled()) {
			inv.setLeggings(newLegs);
		}
		ArmorEquipEvent bootsEvent = new ArmorEquipEvent(player, EquipMethod.ARTIFICIAL, ArmorType.BOOTS, oldBoots, newBoots);
		Bukkit.getPluginManager().callEvent(bootsEvent);
		if (!bootsEvent.isCancelled()) {
			inv.setBoots(newBoots);
		}
		
		inv.setContents(contents);
		
		if (heldSlot != -1) {
			inv.setHeldItemSlot(heldSlot);
		}
		player.updateInventory();
	}
	
	public String toBase64() {
		return ItemStackEncoder.itemStackArrayToBase64(contents);
	}
}
