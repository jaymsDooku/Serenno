package io.jayms.serenno.game.vaultbattle.pearling;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class SpectatorPearlItem extends CustomItem {
	
	public static final int ID = 300;
	
	public SpectatorPearlItem(int id) {
		super(SerennoCrimson.get(), id);
	}
	
	public SerennoPlayer getPearled(ItemStack it) {
		NBTTagCompound compound = CustomItem.getNBTCompound(it);
		if (compound == null) {
			return null;
		}
		String pearledUID = compound.getString("pearled-uuid");
		if (pearledUID == null) {
			return null;
		}
		UUID pearledUUID = UUID.fromString(pearledUID);
		Player pearledPlayer = Bukkit.getPlayer(pearledUUID);
		if (pearledPlayer == null) {
			return null;
		}
		return SerennoCrimson.get().getPlayerManager().get(pearledPlayer);
	}
	
	@Override
	public void populateNBT(NBTTagCompound compound, Map<String, Object> data) {
		SerennoPlayer pearled = (SerennoPlayer) data.get("pearled");
		compound.setString("pearled-uuid", pearled.getID().toString());
	}
	
	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		SerennoPlayer pearled = (SerennoPlayer) data.get("pearled");
		return new ItemStackBuilder(Material.ENDER_PEARL, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.RED + (pearled != null ? pearled.getName() : "No one") + "'s Pearl")
						.enchant(Enchantment.DURABILITY, 1, false)
						.flag(ItemFlag.HIDE_ENCHANTS));
	}
	
	@Override
	public boolean preventOnLeftClick() {
		return false;
	}
	
	@Override
	public boolean preventOnRightClick() {
		return true;
	}

	@Override
	public Runnable getLeftClick(PlayerInteractEvent e) {
		return null;
	}

	@Override
	public Runnable getRightClick(PlayerInteractEvent e) {
		return () -> {
			Player who = e.getPlayer();
			SerennoPlayer pearled = getPearled(e.getItem());
			if (pearled == null) {
				who.sendMessage(ChatColor.RED + "This pearl holds no one.");
				return;
			}
			who.sendMessage(ChatColor.RED + pearled.getName() + ChatColor.YELLOW + " is held in this pearl.");
		};
	}
}
