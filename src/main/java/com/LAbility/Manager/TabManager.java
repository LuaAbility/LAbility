package com.LAbility.Manager;

import com.LAbility.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TabManager implements TabCompleter {
    public final LAbilityMain main;

    public TabManager(LAbilityMain main_) {
        main = main_;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> blank = new ArrayList<String>();
        if (args.length == 1) {
            List<String> basicCommand = new ArrayList<String>();

            if (commandSender instanceof Player) {
                basicCommand.add("check");
                basicCommand.add("yes");
                basicCommand.add("no");
            }

            if (commandSender.isOp()) {
                // admin
                basicCommand.add("start");
                basicCommand.add("stop");
                basicCommand.add("skip");
                basicCommand.add("forcegod");
                basicCommand.add("reroll");
                basicCommand.add("ob");
                basicCommand.add("see");
                basicCommand.add("out");
                basicCommand.add("add");
                basicCommand.add("remove");

                // setting
                basicCommand.add("ruleset");
                basicCommand.add("spawn");
                basicCommand.add("raffle");
                basicCommand.add("health");
                basicCommand.add("god");
                basicCommand.add("border");
                basicCommand.add("item");
                basicCommand.add("equip");
                basicCommand.add("ban");
                basicCommand.add("unban");
                basicCommand.add("edit");
            }

            basicCommand.add("ablist");
            basicCommand.add("ability");
            basicCommand.add("rlist");
            basicCommand.add("rule");
            basicCommand.add("list");



            return filterCommand(basicCommand, args[0]);
        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("ablist")) {
                List<String> advancedCommand = new ArrayList<String>();
                int allData = main.abilities.size() / 8 + 1;
                for (int i = 1; i <= allData; i++) {
                    advancedCommand.add(i + "");
                }

                if (args.length == 2) return filterCommand(advancedCommand, args[1]);
            }

            if (args[0].equalsIgnoreCase("ability")) {
                if (args.length == 2) return filterCommand(abilityList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("rule") || args[0].equalsIgnoreCase("ruleset")) {
                if (args.length == 2) return filterCommand(ruleList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("ob")) {
                if (args.length == 2) return filterCommand(playerList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("out")) {
                if (args.length == 2) return filterCommand(survivePlayerList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("see")) {
                if (args.length == 2) return filterCommand(survivePlayerList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 2) return filterCommand(playerList(), args[1]);
                if (args.length == 3) return filterCommand(abilityList(), args[2]);
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 2) return filterCommand(playerList(), args[1]);
                if (args.length == 3) return filterCommand(playerAbilityList(args[2]), args[2]);
            }

            if (args[0].equalsIgnoreCase("spawn")) {
                if (args.length == 2) return filterCommand(teamList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("item")) {
                if (args.length == 2) return filterCommand(teamList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("equip")) {
                if (args.length == 2) return filterCommand(teamList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("edit")) {
                if (args.length == 2) return filterCommand(variableList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("reroll")) {
                if (args.length == 2) return filterCommand(playerList(), args[1]);
            }

            if (args[0].equalsIgnoreCase("team")) {
                if (args.length == 2) {
                    List<String> team = new ArrayList<String>();
                    team.add("create");
                    team.add("remove");
                    team.add("join");
                    team.add("leave");
                    team.add("list");
                    team.add("divide");
                    team.add("auto");

                    return filterCommand(team, args[1]);
                } else {
                    if (args[1].equalsIgnoreCase("create")) {
                        if (args.length == 3) return filterCommand(blank, args[2]);
                        if (args.length == 4) return filterCommand(colorList(), args[3]);
                        if (args.length == 5) return filterCommand(bool(), args[4]);
                    }

                    if (args[1].equalsIgnoreCase("remove")) {
                        if (args.length == 3) return filterCommand(teamList(), args[2]);
                    }

                    if (args[1].equalsIgnoreCase("join")) {
                        if (args.length == 3) return filterCommand(teamMembers(false), args[2]);
                        if (args.length == 4) return filterCommand(teamList(), args[3]);
                    }

                    if (args[1].equalsIgnoreCase("change")) {
                        if (args.length == 3) return filterCommand(teamMembers(true), args[2]);
                        if (args.length == 4) return filterCommand(teamListExcpetPlayer(args[2]), args[3]);
                    }

                    if (args[1].equalsIgnoreCase("leave")) {
                        if (args.length == 3) return filterCommand(teamMembers(true), args[2]);
                    }

                    if (args[1].equalsIgnoreCase("list")) return blank;

                    if (args[1].equalsIgnoreCase("divide")) return blank;

                    if (args[1].equalsIgnoreCase("auto")) {
                        if (args.length == 3) {
                            List<String> auto = new ArrayList<String>();
                            auto.add("player");
                            auto.add("team");

                            return filterCommand(auto, args[2]);
                        } else return blank;
                    }
                }
            }

            if (args[0].equalsIgnoreCase("border")) {
                if (args.length == 2) {
                    List<String> border = new ArrayList<String>();
                    border.add("size");
                    border.add("time");

                    return filterCommand(border, args[1]);
                }
            }
        }
        return blank;
    }

    private List<String> abilityList() {
        List<String> command = new ArrayList<String>();
        for (Ability a : main.abilities) {
            command.add(a.abilityID);
        }
        return command;
    }

    private List<String> ruleList() {
        List<String> command = new ArrayList<String>();
        for (LARule a : main.rules) {
            command.add(a.ruleID);
        }
        return command;
    }

    private List<String> playerList() {
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.players) {
            command.add(lap.getPlayer().getName());
        }
        return command;
    }

    private List<String> survivePlayerList() {
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.getSurvivePlayer()) {
            command.add(lap.getPlayer().getName());
        }
        return command;
    }

    private List<String> playerAbilityList(String playerName) {
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.players) {
            if (lap.getPlayer().getName().equals(playerName)) {
                for (Ability a : lap.getAbility()) {
                    command.add(a.abilityID);
                }
            }
        }
        return command;
    }

    private List<String> colorList() {
        List<String> color = new ArrayList<String>();
        for (ChatColor c : ChatColor.values()) color.add(c.name());
        return color;
    }

    private List<String> bool() {
        List<String> bool = new ArrayList<String>();
        bool.add("true");
        bool.add("false");
        return bool;
    }

    private List<String> teamList() {
        List<String> team = new ArrayList<String>();
        for (LATeam t : main.teamManager.teams) {
            team.add(t.teamName);
        }
        return team;
    }

    private List<String> teamListExcpetPlayer(String playerName) {
        List<String> team = new ArrayList<String>();
        LATeam plTeam = null;
        for (LAPlayer lap : main.gameManager.players) {
            if (lap.getPlayer().getName().equals(playerName)) {
                plTeam = lap.getTeam();
            }
        }

        if (plTeam != null) {
            for (LATeam t : main.teamManager.teams) {
                if (!plTeam.equals(t)) team.add(t.teamName);
            }
        }
        return team;
    }

    private List<String> teamMembers(boolean isJoin) {
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.players) {
            if ((isJoin && lap.getTeam() != null) || (!isJoin && lap.getTeam() == null))
                command.add(lap.getPlayer().getName());
        }
        return command;
    }

    private List<String> variableList() {
        List<String> command = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : main.gameManager.variable.entrySet()) {
            command.add(entry.getKey());
        }
        return command;
    }

    private List<String> filterCommand(List<String> target, String base){
        if (base.length() < 1)
            return target;

        ArrayList<String> result = new ArrayList<>();
        while (base.startsWith(" "))
            base = base.substring(1);


        for (String s : target)
            if (s.toLowerCase().startsWith(base.toLowerCase()))
                result.add(s);

        if (result.size() < 1)
            return target;

        return result;
    }
}
