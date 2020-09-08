package io.jayms.serenno.game.menu;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.vaultbattle.VaultBattleRequest;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.vault.VaultMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class VaultBattleScalingMenu extends SingleMenu {

    public VaultBattleScalingMenu() {
        super(ChatColor.YELLOW + "Select Scaling");
    }

    @Override
    public boolean onOpen(Player player) {
        return true;
    }

    @Override
    public void onClose(Player player) {
    }

    @Override
    public Inventory newInventory(Map<String, Object> initData) {
        Arena arena = (Arena) initData.get("arena");
        if (!(arena instanceof VaultMap)) {
            return null;
        }

        addButton(0, getScaleButton("5%", 0.1, initData));
        addButton(1, getScaleButton("10%", 0.1, initData));
        addButton(2, getScaleButton("20%", 0.2, initData));
        addButton(3, getScaleButton("25%", 0.25, initData));
        addButton(4, getScaleButton("50%", 0.5, initData));
        addButton(5, getScaleButton("100%", 1.0, initData));
        addButton(6, getScaleButton("150%", 1.5, initData));
        addButton(7, getScaleButton("200%", 2.0, initData));
        addButton(8, getScaleButton("250%", 2.5, initData));

        int size = 9;
        setSize(size);

        Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
        refresh(inventory);
        return inventory;
    }

    private SimpleButton getScaleButton(String scaleText, double scale, Map<String, Object> initData) {
        return new SimpleButton.Builder(this)
                .setItemStack(new ItemStackBuilder(Material.GOLD_INGOT, 1)
                        .meta(new ItemMetaBuilder()
                                .name(scaleText)).build())
                .setPickUpAble(false)
                .setClickHandler(new ClickHandler() {

                    @Override
                    public void handleClick(InventoryClickEvent e) {
                        Player player = (Player) e.getWhoClicked();
                        SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);

                        initData.put("scaling", scale);
                        SerennoCrimson.get().getGameManager().getVaultSideMenu().open(player, initData);
                    }

                }).build();
    }

}
