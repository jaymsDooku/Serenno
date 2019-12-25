package io.jayms.serenno.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class UI {

	private UIScoreboard scoreboard;
	private List<UIHandler> uiHandlers;
	private List<ActionBarHandler> abHandlers;
	
	public UIScoreboard getScoreboard() {
		return scoreboard;
	}
	
	public List<ActionBarHandler> getActionBarHandlers() {
		return abHandlers;
	}
	
	public List<UIHandler> getUIHandlers() {
		return uiHandlers;
	}
	
	public UI(String title) {
		this.scoreboard = new UIScoreboard(title);
		this.uiHandlers = new ArrayList<>();
		this.abHandlers = new ArrayList<>();
	}
	
	public StringBuilder update(Player player) {
		for (int i = 0; i < uiHandlers.size(); i++) {
			UIHandler uiHandler = uiHandlers.get(i);
			uiHandler.handle(player, scoreboard);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < abHandlers.size(); i++) {
			ActionBarHandler abHandler = abHandlers.get(i);
			abHandler.handle(player, sb);
		}
		return sb;
	}
	
}
