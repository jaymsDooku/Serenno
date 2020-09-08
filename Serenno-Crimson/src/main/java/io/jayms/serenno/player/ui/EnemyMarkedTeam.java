package io.jayms.serenno.player.ui;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class EnemyMarkedTeam implements UITeam {

    public static final EnemyMarkedTeam TEAM = new EnemyMarkedTeam();

    private EnemyMarkedTeam() {
    }

    @Override
    public String getName() {
        return "enemy-marked";
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.GOLD;
    }

}
