package io.jayms.serenno.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.jayms.serenno.kit.ItemStackBuilder;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public abstract class CustomItem {
	
	public static final String NBT_CUSTOM_ITEM_ID = "custom-item-id";
	private static final Random rng = new Random();
	
	private final JavaPlugin plugin;
	private final int ID;
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public int getID() {
		return ID;
	}
	
	protected CustomItem(JavaPlugin plugin) {
		this(plugin, rng.nextInt(Integer.MAX_VALUE));
	}
	
	protected CustomItem(JavaPlugin plugin, int ID) {
		this.plugin = plugin;
		this.ID = ID;
	}
	
	public void populateNBT(NBTTagCompound compound, Map<String, Object> data) {
	}

	protected abstract ItemStackBuilder getItemStackBuilder(Map<String, Object> data);
	
	public ItemStack getItemStack() {
		return getItemStack(new HashMap<>());
	}
	
	public ItemStack getItemStack(Map<String, Object> data) {
		ItemStack it = getItemStackBuilder(data).build();
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.setInt(NBT_CUSTOM_ITEM_ID, ID);
		populateNBT(compound, data);
		nmsStack.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	public abstract boolean preventOnLeftClick();
	
	public abstract boolean preventOnRightClick();
	
	public boolean preventOnBlockPlace(BlockPlaceEvent e) {
		return false;
	}
	
	public Runnable getLeftClick(PlayerInteractEvent e) {
		return null;
	}
	
	public Runnable getRightClick(PlayerInteractEvent e) {
		return null;
	}
	
	public Runnable onSwitchSlot(PlayerItemHeldEvent e) {
		return null;
	}
	
	public Runnable onBlockPlace(BlockPlaceEvent e) {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CustomItem)) {
			return false;
		}
		
		CustomItem customIt = (CustomItem) obj;
		return customIt.ID == this.ID;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}
	
	public static int getCustomItemID(ItemStack it) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		if (!nmsStack.hasTag()) return -1;
		
		NBTTagCompound compound = nmsStack.getTag();
		return compound.getInt(NBT_CUSTOM_ITEM_ID);
	}
	
	public static NBTTagCompound getNBTCompound(ItemStack it) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		if (!nmsStack.hasTag()) return null;
		
		return nmsStack.getTag();
	}
}
