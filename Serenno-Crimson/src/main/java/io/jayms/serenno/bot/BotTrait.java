package io.jayms.serenno.bot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DefaultKits;
import io.jayms.serenno.util.ItemUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.util.PlayerAnimation;

public class BotTrait extends Trait implements Listener {

	private Bot bot;
	private Player target;
	
	public BotTrait() {
		super("serenno-bot");
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public boolean hasHealthPotions() {
		Inventory inventory = npc.getTrait(Inventory.class);
		List<ItemStack> contents = Arrays.asList(inventory.getContents());
		return contents.stream().filter(i -> { 
				if (i == null) return false;
				return i.getType() == Material.SPLASH_POTION && ItemUtil.isPotion(i, PotionType.INSTANT_HEAL); 
			}).findFirst().isPresent();
	}
	
	private Player getPlayer() {
		return (Player) npc.getEntity();
	}
	
	public void chooseHandItem(Material material) {
		Player player = getPlayer();
		
		Inventory inventory = npc.getTrait(Inventory.class);
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];
			if (it == null) continue;
			if (it.getType() == material) {
				player.getInventory().setItemInMainHand(it);
			}
		}
	}
	
	public void takeItem(Material material) {
		Inventory inventory = npc.getTrait(Inventory.class);
		ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];
			if (it == null) continue;
			if (it.getType() == material) {
				contents[i] = null;
				break;
			}
		}
		inventory.setContents(contents);
	}
	
	public void consumePotion(ItemStack item) {
		Player player = getPlayer();
		
		chooseHandItem(item.getType());
		PlayerAnimation.ARM_SWING.play(player, 256);
		ThrownPotion healthPot = player.launchProjectile(ThrownPotion.class);
		healthPot.setItem(DefaultKits.health());
		
		takeItem(item.getType());
	}
	
	@Override
	public void run() {
		if (target == null || bot == null) {
			return;
		}
		
		Player player = getPlayer();
		
		switch (bot.getState()) {
			case ATTACKING:
				chooseHandItem(Material.DIAMOND_SWORD);
				
				if (player.getLocation().distanceSquared(target.getLocation()) > 16) {
					player.setSprinting(true);
				}
				
				if (player.isSprinting()) {
					npc.getNavigator().getLocalParameters().speedModifier(1.2f);
				} else {
					npc.getNavigator().getLocalParameters().speedModifier(1);
				}
				
				if (player.getHealth() < 10 && hasHealthPotions()) {
					bot.setState(BotState.POTTING);
				}
				
				if (!npc.getNavigator().isNavigating()) {
					npc.getNavigator().setTarget(target, true);
				}
				break;
			case POTTING:
				if (player.getHealth() >= 10 || !hasHealthPotions()) {
					bot.setState(BotState.ATTACKING);
					return;
				}
						
				Location botLoc = player.getLocation();
				Location targetLoc = target.getLocation();
				
				Vector botLocVec = botLoc.toVector();
				Vector targetLocVec = targetLoc.toVector();
				Vector targetToBot = botLocVec.subtract(targetLocVec);
				Vector normTargetToBot = targetToBot.normalize();
				
				Location potLoc = botLoc.clone();
				potLoc = potLoc.add(normTargetToBot.multiply(3));
				
				npc.getNavigator().setTarget(potLoc);
				
				if (player.getEyeLocation().distance(target.getEyeLocation()) >= 3 && !bot.isThrowingPotion()) {
					consumePotion(DefaultKits.health());
					bot.setThrowingPotion(true);
				}
				break;
			case PEARLING:
			default:
				break;
		}
	}
	
	@EventHandler
	public void onSplash(PotionSplashEvent e) {
		ThrownPotion potion = e.getPotion();
		if (!(potion.getShooter() instanceof Player)) {
			return;
		}
		
		Player shooter = (Player) potion.getShooter();
		if (!CitizensAPI.getNPCRegistry().isNPC(shooter)) {
			return;
		}
		
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(shooter);
		Bot bot = Bot.getBot(npc);
		if (bot.isThrowingPotion()) {
			bot.setThrowingPotion(false);
		}
	}
	
	public void setBot(Bot bot) {
		this.bot = bot;
	}
	
	public void setTarget(Player target) {
		this.target = target;
	}

}
