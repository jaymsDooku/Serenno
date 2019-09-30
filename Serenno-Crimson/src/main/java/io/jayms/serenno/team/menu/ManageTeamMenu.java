package io.jayms.serenno.team.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.menu.ClickHandler;
import io.jayms.serenno.menu.PerPlayerMenu;
import io.jayms.serenno.menu.SimpleButton;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.player.SerennoPlayerManager;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.team.TeamManager;
import io.jayms.serenno.util.MathTools;
import io.jayms.serenno.util.PlayerTools;

public class ManageTeamMenu extends PerPlayerMenu {
	
	public ManageTeamMenu() {
		super(ChatColor.YELLOW + "Manage Team");
	}
	
	@Override
	public boolean onOpen(Player player) {
		addButton(player, 2, getInviteNewMembersButton(player));
		addButton(player, 6, getManageMembersButton(player));
		setSize(9);
		return true;
	}

	@Override
	public void onClose(Player player) {
	}

	@Override
	public Inventory newInventory(Player player, Map<String, Object> initData) {
		Inventory inventory = Bukkit.createInventory(null, this.getSize(), this.getName());
		refresh(player, inventory);
		return inventory;
	}
	
	private SerennoPlayerManager pm = SerennoCrimson.get().getPlayerManager();
	
	private SimpleButton getInviteNewMembersButton(Player player) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.ARROW, 1)
						.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Invite New Members"))
						.build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						player.closeInventory();
						
						SerennoPlayer sp = pm.get(player);
						
						TeamManager teamManager = SerennoCrimson.get().getTeamManager();
						Team team = teamManager.getTeam(sp);
						
						List<Player> forInvMenu = new ArrayList<>();
						for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
							if (!team.inTeam(pm.get(onlinePlayer))) {
								forInvMenu.add(onlinePlayer);
							}
						}
						
						if (forInvMenu.isEmpty()) {
							player.sendMessage(ChatColor.RED + "There are no other players to invite right now.");
							return;
						}
						
						for (int i = 0; i < forInvMenu.size(); i++) {
							Player forInvPlayer = forInvMenu.get(i);
							addButton(player, i, getInvitePlayerButton(player, forInvPlayer));
						}
						
						setSize(MathTools.ceil(forInvMenu.size(), 9));
						open(player, null);
					}
					
				}).build();
	}
	
	private SimpleButton getManageMembersButton(Player player) {
		return new SimpleButton.Builder(this)
				.setItemStack(new ItemStackBuilder(Material.FENCE_GATE, 1)
						.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Manage Members"))
						.build())
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						player.closeInventory();
						
						SerennoPlayerManager spm = SerennoCrimson.get().getPlayerManager();
						SerennoPlayer sp = spm.get(player);
						
						TeamManager teamManager = SerennoCrimson.get().getTeamManager();
						Team team = teamManager.getTeam(sp);
						
						List<Player> forManageMenu = new ArrayList<>();
						for (SerennoPlayer member : team.getMembers()) {
							forManageMenu.add(member.getBukkitPlayer());
						}
						
						if (forManageMenu.isEmpty()) {
							player.sendMessage(ChatColor.RED + "There are no other players to invite right now.");
							return;
						}
						
						for (int i = 0; i < forManageMenu.size(); i++) {
							Player forInvPlayer = forManageMenu.get(i);
							addButton(player, i, getManagePlayerButton(player, forInvPlayer));
						}
						
						setSize(MathTools.ceil(forManageMenu.size(), 9));
						open(player, null);
					}
					
				}).build();
	}
	
	private TeamManager tm = SerennoCrimson.get().getTeamManager();
	
	private SimpleButton getInvitePlayerButton(Player player, Player toInvite) {
		return new SimpleButton.Builder(this)
				.setItemStack(PlayerTools.getHead(toInvite.getUniqueId(), toInvite.getName()))
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						SerennoPlayer sp = pm.get(player);
						Team team = tm.getTeam(sp);
						if (team == null) {
							player.sendMessage(ChatColor.RED + "You aren't apart of a team.");
							return;
						}
						
						SerennoPlayer inviteeSp = SerennoCrimson.get().getPlayerManager().get(toInvite);
						Team inviteeTeam = SerennoCrimson.get().getTeamManager().getTeam(inviteeSp);
						if (inviteeTeam != null) {
							player.sendMessage(ChatColor.RED + "That player is already part of a team.");
							return;
						}
						
						tm.invite(inviteeSp, team);
						player.closeInventory();
					}
					
				}).build();
	}
	
	private SimpleButton getManagePlayerButton(Player player, Player member) {
		return new SimpleButton.Builder(this)
				.setItemStack(PlayerTools.getHead(member.getUniqueId(), member.getName()))
				.setPickUpAble(false)
				.setClickHandler(new ClickHandler() {
					
					@Override
					public void handleClick(InventoryClickEvent e) {
						Player player = (Player) e.getWhoClicked();
						player.closeInventory();
						
						SerennoPlayerManager spm = SerennoCrimson.get().getPlayerManager();
						SerennoPlayer sp = spm.get(player);
						
						TeamManager teamManager = SerennoCrimson.get().getTeamManager();
						Team team = teamManager.getTeam(sp);
						
						List<Player> forInvMenu = new ArrayList<>();
						for (SerennoPlayer member : team.getMembers()) {
							forInvMenu.add(member.getBukkitPlayer());
						}
						
						if (forInvMenu.isEmpty()) {
							player.sendMessage(ChatColor.RED + "There are no other players to invite right now.");
							return;
						}
						
						for (int i = 0; i < forInvMenu.size(); i++) {
							Player forInvPlayer = forInvMenu.get(i);
							addButton(player, i, getInvitePlayerButton(player, forInvPlayer));
						}
						
						setSize(MathTools.ceil(forInvMenu.size(), 9));
						open(player, null);
					}
					
				}).build();
	}

}
