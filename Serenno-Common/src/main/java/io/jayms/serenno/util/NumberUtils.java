package io.jayms.serenno.util;

import net.md_5.bungee.api.ChatColor;

public final class NumberUtils {

	public static ChatColor getPrimaryColor(double healthPerc) {
		if (healthPerc <= 0.33) {
			return ChatColor.RED;
		} else if (healthPerc <= 0.66) {
			return ChatColor.YELLOW;
		} else {
			return ChatColor.GREEN;
		}
	}
	
	public static ChatColor getSecondaryColor(double healthPerc) {
		if (healthPerc <= 0.33) {
			return ChatColor.DARK_RED;
		} else if (healthPerc <= 0.66) {
			return ChatColor.GOLD;
		} else {
			return ChatColor.DARK_GREEN;
		}
	}
	
}
