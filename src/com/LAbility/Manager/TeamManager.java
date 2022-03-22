package com.LAbility.Manager;

import com.LAbility.Ability;
import com.LAbility.Event.GameEndEvent;
import com.LAbility.Event.PlayerEliminateEvent;
import com.LAbility.LAPlayer;
import com.LAbility.LATeam;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.List.BanIDList;
import com.LAbility.LuaUtility.List.PlayerList;
import com.LAbility.LuaUtility.List.TeamList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TeamManager {
    public TeamList<LATeam> teams = new TeamList<>();
    public ArrayList<ChatColor> presetColor = new ArrayList<>();
    public ArrayList<String> presetName = new ArrayList<>();

    public TeamManager() {
        presetColor.add(ChatColor.RED);
        presetColor.add(ChatColor.BLUE);
        presetColor.add(ChatColor.GREEN);
        presetColor.add(ChatColor.YELLOW);
        presetColor.add(ChatColor.LIGHT_PURPLE);
        presetColor.add(ChatColor.AQUA);
        presetColor.add(ChatColor.DARK_RED);
        presetColor.add(ChatColor.DARK_BLUE);
        presetColor.add(ChatColor.DARK_GREEN);
        presetColor.add(ChatColor.DARK_PURPLE);
        presetColor.add(ChatColor.DARK_AQUA);
        presetColor.add(ChatColor.GOLD);
        presetColor.add(ChatColor.GRAY);
        presetColor.add(ChatColor.WHITE);
        presetColor.add(ChatColor.BLACK);
        presetColor.add(ChatColor.DARK_GRAY);

        presetName.add("빨강");
        presetName.add("파랑");
        presetName.add("초록");
        presetName.add("노랑");
        presetName.add("핑크");
        presetName.add("청록");
        presetName.add("진빨강");
        presetName.add("진파랑");
        presetName.add("진초록");
        presetName.add("보라");
        presetName.add("진청록");
        presetName.add("금");
        presetName.add("은");
        presetName.add("하양");
        presetName.add("검정");
        presetName.add("회색");
    }

    public ArrayList<LAPlayer> getTeamMember(LATeam team, boolean allMember) {
        ArrayList<LAPlayer> teamMember = new ArrayList<>();
        for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
            if (lap.getTeam().equals(team) && (allMember || lap.isSurvive)) teamMember.add(lap);
        }

        return teamMember;
    }

    public PlayerList<LAPlayer> getOpponentTeam(LAPlayer lap, boolean allMember) {
        PlayerList<LAPlayer> opponentTeam = new PlayerList<>();

        if (lap.getTeam() == null) {
            for (LAPlayer p : LAbilityMain.instance.gameManager.players) {
                if (!lap.getPlayer().getName().equals(p.getPlayer().getName()) && (allMember || lap.isSurvive)) opponentTeam.add(p);
            }
        } else {
            for (LAPlayer p : LAbilityMain.instance.gameManager.players) {
                if (!lap.getTeam().equals(p.getTeam()) && (allMember || lap.isSurvive)) opponentTeam.add(p);
            }
        }

        return opponentTeam;
    }

    public PlayerList<LAPlayer> getMyTeam(LAPlayer lap, boolean allMember) {
        PlayerList<LAPlayer> myTeam = new PlayerList<>();

        if (lap.getTeam() == null) myTeam.add(lap);

        else {
            for (LAPlayer p : LAbilityMain.instance.gameManager.players) {
                if (lap.getTeam().equals(p.getTeam()) && (allMember || lap.isSurvive)) myTeam.add(p);
            }
        }

        return myTeam;
    }

    public void divideTeamByTeamCount(int teamCount) {
        int[] order = new int[LAbilityMain.instance.gameManager.players.size()];
        Random random = new Random();

        for (int i = 0; i < order.length; i++) order[i] = i;

        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(0, order.length);
            int temp = order[0];
            order[0] = order[randomIndex];
            order[randomIndex] = temp;
        }

        if (teamCount > 1) {
            teams = new TeamList<>();
            for (int currentCount = 0; currentCount < teamCount; currentCount++) {
                teams.add(new LATeam(presetColor.get(currentCount), presetName.get(currentCount), false));
            }
        }

        if (teams.size() > 0) {
            for (int i = 0; i < order.length; i++) {
                int teamIndex = ((i + 1) % teams.size());
                LAPlayer lap = LAbilityMain.instance.gameManager.players.get(order[i]);
                lap.setTeam(teams.get(teamIndex));
            }
        }
    }

    public void divideTeamByMemberCount(int memberCount) {
        int[] order = new int[LAbilityMain.instance.gameManager.players.size()];
        Random random = new Random();

        for (int i = 0; i < order.length; i++) order[i] = i;

        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(0, order.length);
            int temp = order[0];
            order[0] = order[randomIndex];
            order[randomIndex] = temp;
        }

        if (memberCount > 1 && memberCount < order.length) {
            teams = new TeamList<>();
            int maxCount = (int) Math.ceil((double) order.length / memberCount);
            for (int currentCount = 0; currentCount < maxCount; currentCount++) {
                teams.add(new LATeam(presetColor.get(currentCount), presetName.get(currentCount), false));
            }
        }

        if (teams.size() > 0) {
            for (int i = 0; i < order.length; i++) {
                int teamIndex = ((i + 1) % teams.size());
                LAPlayer lap = LAbilityMain.instance.gameManager.players.get(order[i]);
                lap.setTeam(teams.get(teamIndex));
            }
        }
    }

    public void createTeam(ChatColor color, String teamName, boolean teamAttack) {
        teams.add(new LATeam(color, teamName, teamAttack));
    }

    public void removeTeam(String teamName) {
        for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
            if (lap.getTeam().teamName.equals(teamName)) lap.setTeam(null);
        }
        teams.remove(teamName);
    }

    public void joinTeam(LAPlayer lap, LATeam team) { lap.setTeam(team); }

    public void leaveTeam(LAPlayer lap) { lap.setTeam(null); }

    public void ShowAllMember(CommandSender sender) {
        sender.sendMessage("\2476-------[\247eTeam List\2476]-------");
        for (LATeam lap : teams) {
            int index = 0;
            String memberString = lap.color + lap.teamName + "\2477 : \247a";
            ArrayList<LAPlayer> member = getTeamMember(lap, true);

            for (LAPlayer p : member) {
                memberString += p.getPlayer().getName();
                if (index++ < (member.size() - 1)) {
                    memberString += ", ";
                }
            }
            if (index == 0) memberString += "\247c없음";
            sender.sendMessage(memberString);
        }

        ArrayList<LAPlayer> players = LAbilityMain.instance.gameManager.players;
        ArrayList<LAPlayer> noTeam = new ArrayList<>();

        for (LAPlayer p : players) if (p.getTeam() == null) noTeam.add(p);

        if (noTeam.size() > 0) {
            int index = 0;
            String memberString = "\247f팀 없음\2477 : \247a";

            for (LAPlayer p : noTeam) {
                memberString += p.getPlayer().getName();
                if (index++ < (noTeam.size() - 1)) {
                    memberString += ", ";
                }
            }

            sender.sendMessage(memberString);
        }
    }
}
