package io.jayms.serenno.item;

import java.util.UUID;

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
	
	private final JavaPlugin plugin;
	private final UUID ID;
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public UUID getID() {
		return ID;
	}
	
	protected CustomItem(JavaPlugin plugin) {
		this(plugin, UUID.randomUUID());
	}
	
	protected CustomItem(JavaPlugin plugin, UUID ID) {
		this.plugin = plugin;
		this.ID = ID;
	}

	protected abstract ItemStackBuilder getItemStackBuilder();
	
	public ItemStack getItemStack() {
		ItemStack it = getItemStackBuilder().build();
		return setCustomItemID(it, ID);
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
		return customIt.ID.equals(this.ID);
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
	public static UUID getCustomItemID(ItemStack it) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		if (!nmsStack.hasTag()) return null;
		
		NBTTagCompound compound = nmsStack.getTag();
		String IDStr = compound.getString(NBT_CUSTOM_ITEM_ID);
		
		try {
			return UUID.fromString(IDStr);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static ItemStack setCustomItemID(ItemStack it, UUID ID) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.setString(NBT_CUSTOM_ITEM_ID, ID.toString());
		nmsStack.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
}
