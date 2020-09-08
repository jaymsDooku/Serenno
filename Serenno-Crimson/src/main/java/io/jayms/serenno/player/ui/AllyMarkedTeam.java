package io.jayms.serenno.player.ui;

import io.jayms.serenno.ui.UITeam;
import net.md_5.bungee.api.ChatColor;

public class AllyMarkedTeam implements UITeam {

    public static final AllyMarkedTeam TEAM = new AllyMarkedTeam();

    private AllyMarkedTeam() {
    }

    @Override
    public String getName() {
        return "ally-marked";
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.DARK_GREEN;
    }

}
