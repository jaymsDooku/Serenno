package io.jayms.serenno.game.kiteditor;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Sets;

import io.jayms.serenno.game.DefaultKits;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.kit.ItemStackBuilder;

public class DiamondKitChest extends KitEditorChest {

	public DiamondKitChest(KitEditor kitEditor, Location kitEditorChest) {
		super(kitEditor, kitEditorChest);
	}

	@Override
	public void fill(Inventory inv) {
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
					.durability((short)0)
					.build());
		}
		
		setChestItem(0, DefaultKits.dhelmet());
		setChestItem(1, DefaultKits.dchest());
		setChestItem(2, DefaultKits.dlegs());
		setChestItem(3, DefaultKits.dboots());
		setChestItem(4, DefaultKits.sword());
		setChestItem(5, DefaultKits.pearls());
		setChestItem(9, DefaultKits.fres(60 * 8));
		setChestItem(10, DefaultKits.regen((60 * 2) + 30));
		setChestItem(11, DefaultKits.strength((60 * 2) + 30));
		setChestItem(12, DefaultKits.speed((60 * 2) + 30));
		setChestItem(18, DefaultKits.carrots());
		setChestItem(19, DefaultKits.steak());
	}

	@Override
	public Set<DuelType> getValidDuelTypes() {
		return Sets.newHashSet(DuelType.NODEBUFF, DuelType.CLASSES, DuelType.DEBUFF, DuelType.KOTH, DuelType.VAULTBATTLE);
	}

}
