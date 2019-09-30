package io.jayms.serenno.game.menu;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.menu.SingleMenu;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.util.PlayerTools;

public class DuelTeamReportMenu extends SingleMenu {
	
	public static void open(Player player, DuelTeam team, DuelTeam toSwitch, Map<String, Object> initData) {
		DuelTeamReportMenu reportMenu = new DuelTeamReportMenu(team, toSwitch);
		reportMenu.open(player, initData);
		new MenuController(reportMenu);
	}

	private DuelTeam duelTeam;
	private DuelTeam toSwitch;
	
	public DuelTeamReportMenu(DuelTeam duelTeam, DuelTeam toSwitch) {
		super(ChatColor.WHITE + duelTeam.getTeam().getLeader().getName() + "'s " + duelTeam.getTeamColor() + "Team");
		this.duelTeam = duelTeam;
		this.toSwitch = toSwitch;
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
		Team team = duelTeam.getTeam();
		List<SerennoPlayer> players = team.getAll();
		for (int i = 0; i < players.size(); i++) {
			SerennoPlayer player = players.get(i);
			SerennoPlayer nextPlayer;
			if ((i + 1) >= players.size()) {
				nextPlayer = players.get(0);
			} else {
				nextPlayer = players.get(i + 1);
			}
			addButton(i, getPlayerButton(player, nextPlayer, initData));
		}
		
		String switchItemName = ChatColor.YELLOW + "Switch to " + ChatColor.WHITE + toSwitch.getTeam().getLeader().getName() + "'s " + ChatColor.RED + "Team";
		addButton(53, new SimpleButton.Builder(this)
				.setPickUpAble(false)
				.setItemStack(new ItemStackBuilder(Material.SPECTRAL_ARROW, 1)
						.meta(new ItemMetaBuilder().name(switchItemName)).build())
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						DuelTeamReportMenu.open((Player) e.getWhoClicked(), duelTeam, toSwitch, initData);
					}
				})
				.build());
		
		int size = 54;
		setSize(size);
		
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(inventory);
		return inventory;
	}
	
	private SimpleButton getPlayerButton(SerennoPlayer player, SerennoPlayer nextPlayer, Map<String, Object> initData) {
		return new SimpleButton.Builder(this)
				.setItemStack(PlayerTools.getHead(player.getID(), ChatColor.RED + player.getName()))
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						PlayerReportMenu reportMenu = new PlayerReportMenu(player, nextPlayer);
						reportMenu.open((Player) e.getWhoClicked(), initData);
						new MenuController(reportMenu);
					}
					
				}).build();
	}

}

