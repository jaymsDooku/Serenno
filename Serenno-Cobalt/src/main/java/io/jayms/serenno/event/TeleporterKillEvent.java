package io.jayms.serenno.event;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TeleporterKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Vehicle killerVehicle;
    private Player killed;

    public TeleporterKillEvent(Vehicle killerVehicle, Player killed) {
        this.killerVehicle = killerVehicle;
        this.killed = killed;
    }

    public Vehicle getKillerVehicle() {
        return killerVehicle;
    }

    public Player getKilled() {
        return killed;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
