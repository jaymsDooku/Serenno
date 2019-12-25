package io.jayms.serenno.ui;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class UIScoreboard {

	private static Map<String, String> cache = new HashMap<>();

    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;
    
    private Map<Player, UITeam> uiTeams;

    public UIScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
        this.uiTeams = new ConcurrentHashMap<>();
    }
    
    private org.bukkit.scoreboard.Team getTeam(Scoreboard mcBoard, String t, boolean friendly, String prefix) {
		org.bukkit.scoreboard.Team team;
		try {
            team = mcBoard.registerNewTeam(t);
            team.setCanSeeFriendlyInvisibles(friendly);
            team.setPrefix(prefix);
        } catch (IllegalArgumentException e) {
            team = mcBoard.getTeam(t);
        }
		return team;
	}
    
    public void removeTeam(Player player) {
    	if (!uiTeams.containsKey(player)) {
    		return;
    	}
    	
    	UITeam current = uiTeams.get(player);
		Team currentTeam = getTeam(scoreboard, current.getName(), true, current.getColour() + "");
		if (currentTeam.hasEntry(player.getName())) {
			currentTeam.removeEntry(player.getName());
		}
    }
    
    public void setTeam(Player player, UITeam uiTeam) {
    	removeTeam(player);
    	
    	Team team = getTeam(scoreboard, uiTeam.getName(), true, uiTeam.getColour() + "");
    	if (!team.hasEntry(player.getName())) {
    		team.addEntry(player.getName());
    	}
    	
    	uiTeams.put(player, uiTeam);
    }

    public void add(String text, Integer score) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    public boolean remove(Integer score, String text) {
        return remove(score, text, true);
    }

    public boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if(b)
            removed.add(score);

        return true;
    }

    public String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score) &&
                    !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    private Map.Entry<Team, String> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[pos];
        String result;

        if (!cache.containsKey(color.toString()))
            cache.put(color.toString(), color.toString());

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);

        return new AbstractMap.SimpleEntry<>(team, result);
    }

    private void applyText(Team team, String text, String result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next();

        team.setPrefix(prefix);

        if(!team.hasEntry(result))
            team.addEntry(result);

        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(prefix);
            String suffix = iterator.next();

            if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                prefix = prefix.substring(0, prefix.length() - 1);
                team.setPrefix(prefix);
                prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                suffix = suffix.substring(1);
            }

            if (prefixColor == null)
                prefixColor = "";

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

    public void update() {
    	removed.stream().forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = obj.getScore(s);

                if (score == null)
                    continue;

                if (score.getScore() != remove)
                    continue;

                scoreboard.resetScores(s);
            }
        });

        removed.clear();
    	
    	if (updated.isEmpty()) {
            return;
        }
    	
        if (obj == null) {
            obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            obj.setDisplayName(title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Team t = scoreboard.getTeam(ChatColor.values()[text.getValue()].toString());
            Map.Entry<Team, String> team;

            if(!updated.contains(text.getKey())) {
                continue;
            }

            if(t != null) {
                String color = ChatColor.values()[text.getValue()].toString();

                if (!cache.containsKey(color)) {
                    cache.put(color, color);
                }

                team = new AbstractMap.SimpleEntry<>(t, cache.get(color));
                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            obj.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if(obj != null)
            obj.setDisplayName(this.title);
    }

    public void reset() {
        for (Team t : teams)
            t.unregister();
        teams.clear();
        scores.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
        for (Player p : players) {
        	p.setScoreboard(scoreboard);
        }
    }
}