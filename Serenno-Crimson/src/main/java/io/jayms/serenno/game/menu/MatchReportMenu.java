package io.jayms.serenno.game.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.PerPlayerMenu;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.PlayerTools;

public class MatchReportMenu extends PerPlayerMenu {

	private Duel duel;
	
	public MatchReportMenu(Duel duel) {
		super(ChatColor.WHITE + "Game " + ChatColor.RED + "#" + duel.getID());
		this.duel = duel;
	}
	
	@Override
	public boolean onOpen(Player player) {
		return true;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Player player, Map<String, Object> initData) {
		SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(player);
		
		int size = 9;
		DuelTeam own = duel.getTeam(sp);
		DuelTeam winner = duel.getWinner();
		DuelTeam loser = duel.getLoser();
		addButton(player, 2, getTeamButton(own, winner, loser));
		addButton(player, 4, getSummaryButton(own));
		addButton(player, 6, getTeamButton(own, loser, winner));
		setSize(size);
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(player, inventory);
		return inventory;
	}
	
	private SimpleButton getTeamButton(DuelTeam own, DuelTeam team, DuelTeam switchTo) {
		ItemStack head = PlayerTools.getHead(team.getTeam().getLeader().getID(), ChatColor.RED + team.getTeam().getLeader().getName());
		duel.getStatistics().loreStatsTeamTotal(head, own, team);
		
		return new SimpleButton.Builder(this)
				.setItemStack(head)
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Map<String, Object> initData = new HashMap<>();
						initData.put("duel", duel);
						Player player = (Player) e.getWhoClicked();
						if (team.getTeam().size() > 1) {
							DuelTeamReportMenu.open(player, team, switchTo, initData);
						} else {
							PlayerReportMenu.open(player, team.getTeam().getLeader(), switchTo.getTeam().getLeader(), initData);
						}
					}
					
				}).build();
	}
	
	private SimpleButton getSummaryButton(DuelTeam team) {
		ItemStack summaryIt = new ItemStackBuilder(Material.BOOK, 1)
				.meta(new ItemMetaBuilder()
				.name(ChatColor.YELLOW + "Match Summary")).build();
		duel.getStatistics().loreStatsTotal(summaryIt, team);
		
		return new SimpleButton.Builder(this)
				.setItemStack(summaryIt)
				.setPickUpAble(false).build();
	}

}
