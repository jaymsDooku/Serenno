package io.jayms.serenno.bot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.jayms.serenno.util.ItemUtil;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;

public class BotTrait extends Trait {

	private Player target;
	
	public BotTrait() {
		super("serenno-bot");
	}
	
	public BotTrait(Player target) {
		super("serenno-bot");
		this.target = target;
	}
	
	public boolean hasHealthPotions() {
		Inventory inventory = npc.getTrait(Inventory.class);
		List<ItemStack> contents = Arrays.asList(inventory.getContents());
		return contents.stream().filter(i -> { 
				if (i == null) return false;
				return i.getType() == Material.SPLASH_POTION && ItemUtil.isPotion(i, PotionEffectType.HEAL); 
			}).findFirst().isPresent();
	}
	
	@Override
	public void run() {
		if (target == null) {
			return;
		}
		
		Player player = (Player) npc.getEntity();
		if (player.getLocation().distanceSquared(target.getLocation()) > 16) {
			player.setSprinting(true);
		}
		
		if (player.isSprinting()) {
			npc.getNavigator().getLocalParameters().speedModifier(1.2f);
		} else {
			npc.getNavigator().getLocalParameters().speedModifier(1);
		}
		
		if (player.getHealth() < 10 && hasHealthPotions()) {
			Location botLoc = player.getLocation();
			Location targetLoc = target.getLocation();
			
			Vector botLocVec = botLoc.toVector();
			Vector targetLocVec = targetLoc.toVector();
			Vector targetToBot = botLocVec.subtract(targetLocVec);
			Vector normTargetToBot = targetToBot.normalize();
			
			Location potLoc = botLoc.clone();
			potLoc = potLoc.add(normTargetToBot.multiply(2.5));
			
			npc.getNavigator().setTarget(potLoc);
			return;
		}
		
		if (!npc.getNavigator().isNavigating()) {
			npc.getNavigator().setTarget(target, true);
		}
	}
	
	public void setTarget(Player target) {
		this.target = target;
	}

}
