package io.jayms.serenno.menu;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;

public abstract class AbstractMenu implements Menu {

	private String name;
	private int size;
	private Set<UUID> using = Sets.newConcurrentHashSet();
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public boolean allowPlayerInventory() {
		return false;
	}
	
	public Set<UUID> getUsing() {
		return using;
	}

	protected AbstractMenu(String name) {
		this.name = name;
	}

	@Override
	public boolean hasOpen(Player player) {
		return using.contains(player.getUniqueId());
	}
	
	@Override
	public void onClose(Player player, Inventory inventory, Map<String, Object> data) {
	}
	
	private Map<Player, Map<String, Object>> data = Maps.newConcurrentMap();
	
	public Map<Player, Map<String, Object>> getData() {
		return data;
	}

	@Override
	public void open(Player player, Map<String, Object> initData) {
		if (!onOpen(player)) return;
		
		if (initData != null) {
			data.put(player, initData);
		}
		
		using.add(player.getUniqueId());
		Inventory inventory = getInventory(player, initData);
		if (inventory == null) return;
		player.openInventory(inventory);
	}

	@Override
	public void close(Player player, Inventory inventory) {
		Map<String, Object> initData = data.remove(player);
		if (initData != null) {
			onClose(player, inventory, initData);
		}
		
		onClose(player);
		using.remove(player.getUniqueId());
	}
	
	protected ItemStack emptyPane = new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
			.durability((short) 7)
			.meta(new ItemMetaBuilder().name("")).build();

}
