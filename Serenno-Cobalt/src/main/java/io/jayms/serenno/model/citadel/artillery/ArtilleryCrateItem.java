package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.util.ItemUtil;
import net.md_5.bungee.api.ChatColor;

public abstract class ArtilleryCrateItem extends CustomItem {
	
	public ArtilleryCrateItem() {
		super(SerennoCobalt.get());
	}
	
	public abstract String getDisplayName();
	
	@Override
	protected ItemStackBuilder getItemStackBuilder() {
		return new ItemStackBuilder(Material.WORKBENCH, 1)
				.meta(new ItemMetaBuilder()
						.name(getDisplayName()));
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
			player.sendMessage(ChatColor.RED + "You need to reinforce your " + ItemUtil.getName(e.getItemInHand()));
			return true;
		}
		return false;
	}
}
