package io.jayms.serenno.bot;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.maxopoly.finale.Finale;
import com.github.maxopoly.finale.combat.CPSHandler;
import com.github.maxopoly.finale.combat.Hit;
import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.kit.Kit;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.util.PlayerAnimation;

public class Bot {
	
	private static Map<Integer, Bot> aliveNpcs = Maps.newConcurrentMap();
	
	public static Bot getBot(NPC npc) {
		return aliveNpcs.get(npc.getId());
	}
	
	public static void aliveNPC(Bot bot) {
		aliveNpcs.put(bot.getNpc().getId(), bot);
	}
	
	public static void killNPC(NPC npc) {
		npc.despawn();
		npc.destroy();
	}
	
	public static void killAndRemoveNPC(NPC npc) {
		aliveNpcs.remove(npc.getId());
		killNPC(npc);
	}
	
	public static void clearAndKillAllNPCs() {
		for (Bot aliveBot : aliveNpcs.values()) {
			killNPC(aliveBot.getNpc());
		}
		aliveNpcs.clear();
	}
	
	public interface SpawnCallback {
		
		void onSpawn(Player botPlayer);
		
	}
	
	public static Location getLeftSide(final Location location, final double distance) {
		final float angle = location.getYaw() / 60;
		return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
	}
	
	public static Location getRightSide(final Location location, final double distance) {
		final float angle = location.getYaw() / 60;
		return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
	}

	private NPC npc;
	private BotTrait botTrait;
	private Strafe strafe;
	private StrafeDirection strafeDirection;
	private int strafeCounter;
	private int strafeThreshold = 20;
	
	private BotState state = BotState.ATTACKING;
	private boolean throwingPotion;
	
	public Bot(String name) {
		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
		this.botTrait = new BotTrait();
		aliveNPC(this);
	}
	
	public void setThrowingPotion(boolean throwingPotion) {
		this.throwingPotion = throwingPotion;
	}
	
	public boolean isThrowingPotion() {
		return throwingPotion;
	}
	
	public void setState(BotState state) {
		this.state = state;
	}
	
	public BotState getState() {
		return state;
	}
	
	public void spawn(SpawnCallback spawnCb) {
		NPC bot = npc;
		bot.spawn(SerennoCrimson.get().getLobby().getLobbySpawn());
		bot.setProtected(false);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Player player = (Player) bot.getEntity();
				/*bot.getNavigator().getDefaultParameters().addRunCallback(() -> {
					if (player.isSprinting()) {
						bot.getNavigator().getLocalParameters().speedModifier(5);
					} else {
						bot.getNavigator().getLocalParameters().speedModifier(1);
					}
				});*/
				bot.getNavigator().getDefaultParameters().pathDistanceMargin(0);
				bot.getNavigator().getDefaultParameters().attackRange(10);
				bot.getNavigator().getDefaultParameters().attackDelayTicks(3);
				bot.getNavigator().getDefaultParameters().useNewPathfinder(false);
				bot.getNavigator().getDefaultParameters().updatePathRate(1);
				bot.getNavigator().getDefaultParameters().entityTargetLocationMapper((e) -> {
					if (strafeDirection == StrafeDirection.GOING_RIGHT) {
						strafeCounter++;
					} else {
						strafeCounter--;
					}
					
					if (strafeCounter > strafeThreshold) {
						strafe = Strafe.RIGHT;
					} else if (strafeCounter < -strafeThreshold) {
						strafe = Strafe.LEFT;
					} else {
						strafe = Strafe.FORWARD;
					}
					
					if (strafeCounter > strafeThreshold*2) {
						strafeDirection = StrafeDirection.GOING_LEFT;
					}
					if (strafeCounter < -strafeThreshold*2) {
						strafeDirection = StrafeDirection.GOING_RIGHT;
					}
					
					Location loc = e.getLocation();
					Location botLoc = bot.getEntity().getLocation();
					switch (strafe) {
						case LEFT:
							Location left = getLeftSide(botLoc, 1);
							Location diff = left.subtract(botLoc);
							Location newLoc = loc.clone().add(diff);
							return newLoc;
						case RIGHT:
							Location right = getRightSide(botLoc, 1);
							Location diff1 = right.subtract(botLoc);
							Location newLoc1 = loc.clone().add(diff1);
							return newLoc1;
						case FORWARD:
						default:
							return loc;
					}
				});
				bot.getNavigator().getDefaultParameters().attackStrategy(new AttackStrategy() {
				
					@Override
					public boolean handle(LivingEntity attacker, LivingEntity target) {
						Player player = (Player) attacker;
						CPSHandler cpsHandler = Finale.getPlugin().getManager().getCPSHandler();
						cpsHandler.updateClicks(player);
						PlayerAnimation.ARM_SWING.play(player, 256);
						if (cpsHandler.getCPS(attacker.getUniqueId()) >= 9) {
							return true;
						}
						Finale.getPlugin().getManager().getCombatRunnable().getHitQueue().add(new Hit(player, target));
						if (target.getLocation().distance(attacker.getLocation()) > 4.5) {
							player.setSprinting(true);
						}
						return true;
					}
				
				});
				bot.addTrait(botTrait);
				spawnCb.onSpawn(player);
			}
		}.runTaskLater(SerennoCrimson.get(), 20L);
	}
	
	public BotTrait getBotTrait() {
		return botTrait;
	}
	
	public Player getPlayer() {
		return (Player) npc.getEntity();
	}
	
	public NPC getNpc() {
		return npc;
	}
	
	public void setStrafe(Strafe strafe) {
		this.strafe = strafe;
	}
	
	public Strafe getStrafe() {
		return strafe;
	}
	
	public void setStrafeDirection(StrafeDirection strafeDirection) {
		this.strafeDirection = strafeDirection;
	}
	
	public StrafeDirection getStrafeDirection() {
		return strafeDirection;
	}
	
	public static void loadKit(Bot bot, Kit kit) {
		NPC npc = bot.getNpc();
		new BukkitRunnable() {
			
			public void run() {
				Inventory inventory = npc.getTrait(Inventory.class);
				inventory.setContents(kit.contents());
				new BukkitRunnable() {
					
					public void run() {
						Equipment equipment = npc.getTrait(Equipment.class);
						equipment.set(EquipmentSlot.HELMET, kit.helmet());
						equipment.set(EquipmentSlot.CHESTPLATE, kit.chestplate());
						equipment.set(EquipmentSlot.LEGGINGS, kit.leggings());
						equipment.set(EquipmentSlot.BOOTS, kit.boots());
						equipment.set(EquipmentSlot.HAND, kit.mainHand());
						equipment.set(EquipmentSlot.OFF_HAND, kit.offhand());
					}
					
				}.runTaskLater(SerennoCrimson.get(), 1L);
			}
			
		}.runTaskLater(SerennoCrimson.get(), 1L);
	}
	
}
