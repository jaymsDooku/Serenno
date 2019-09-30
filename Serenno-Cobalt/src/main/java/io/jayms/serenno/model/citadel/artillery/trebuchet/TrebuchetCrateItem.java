package io.jayms.serenno.model.citadel.artillery.trebuchet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import net.md_5.bungee.api.ChatColor;

public class TrebuchetCrateItem extends CustomItem {
	
	private TrebuchetCrate trebuchetCrate;
	
	public void setTrebuchetCrate(TrebuchetCrate trebuchetCrate) {
		this.trebuchetCrate = trebuchetCrate;
	}
	
	public TrebuchetCrateItem() {
		super(SerennoCobalt.get());
	}
	
	@Override
	protected ItemStackBuilder getItemStackBuilder() {
		return new ItemStackBuilder(Material.WORKBENCH, 1)
				.meta(new ItemMetaBuilder()
						.name(trebuchetCrate.getDisplayName()));
	}
	
	@Override
	public boolean preventOnLeftClick() {
		return false;
	}
	
	@Override
	public boolean preventOnRightClick() {
		return false;
	}
	
	@Override
	public boolean preventOnBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		CitadelPlayer cp = SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player);
		
		if (!cp.isReinforcementFortification()) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You need to reinforce your " + trebuchetCrate.getDisplayName());
			return true;
		}
		return false;
	}

	@Override
	public Runnable onBlockPlace(BlockPlaceEvent e) {
		return () -> {
			Player player = e.getPlayer();
			SerennoCobalt.get().getCitadelManager().getArtilleryManager().placeArtilleryCrate(player, trebuchetCrate, e.getBlock().getLocation());
		};
	}
}
