package io.jayms.serenno.command.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.menu.DuelTypeMenu.MenuType;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "kiteditor")
public class KitEditorCommand extends StandaloneCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player senderPlayer = (Player) sender;
		
		Map<String, Object> initData = new HashMap<>();
		initData.put("menuType", MenuType.KIT_EDITOR);
		SerennoCrimson.get().getGameManager().getDuelTypeMenu().open(senderPlayer, initData);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}

}