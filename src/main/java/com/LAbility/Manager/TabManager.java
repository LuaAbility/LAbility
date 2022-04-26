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
            basicCommand.add("ablist");
            basicCommand.add("ability");
            basicCommand.add("rlist");
            basicCommand.add("rule");
            basicCommand.add("team");

            if (commandSender.isOp()) {
                basicCommand.add("ob");
                basicCommand.add("ruleset");
                basicCommand.add("see");
                basicCommand.add("add");
                basicCommand.add("remove");
                basicCommand.add("list");
                basicCommand.add("reroll");
                basicCommand.add("skip");
                basicCommand.add("start");
                basicCommand.add("stop");
                basicCommand.add("out");
            }

            return basicCommand;
        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("ablist")) {
                List<String> advancedCommand = new ArrayList<String>();
                int allData = main.abilities.size() / 8 + 1;
                for (int i = 1; i <= allData; i++) {
                    advancedCommand.add(i + "");
                }

                if (args.length == 2) return advancedCommand;
            }

            if (args[0].equalsIgnoreCase("ability")) {
                if (args.length == 2) return abilityList();
            }

            if (args[0].equalsIgnoreCase("rule") || args[0].equalsIgnoreCase("ruleset")) {
                if (args.length == 2) return ruleList();
            }

            if (args[0].equalsIgnoreCase("ob")) {
                if (args.length == 2) return playerList();
            }

            if (args[0].equalsIgnoreCase("out")) {
                if (args.length == 2) return survivePlayerList();
            }

            if (args[0].equalsIgnoreCase("see")) {
                if (args.length == 2) return survivePlayerList();
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 2) return playerList();
                if (args.length == 3) return abilityList();
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 2) return playerList();
                if (args.length == 3) return playerAbilityList(args[2]);
            }

            if (args[0].equalsIgnoreCase("reroll")) {
                if (args.length == 2) return playerList();
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

                    return team;
                } else {
                    if (args[1].equalsIgnoreCase("create")) {
                        if (args.length == 3) return blank;
                        if (args.length == 4) return colorList();
                        if (args.length == 5) return bool();
                    }

                    if (args[1].equalsIgnoreCase("remove")) {
                        if (args.length == 3) return teamList();
                    }

                    if (args[1].equalsIgnoreCase("join")) {
                        if (args.length == 3) return teamMembers(false);
                        if (args.length == 4) return teamList();
                    }

                    if (args[1].equalsIgnoreCase("leave")) {
                        if (args.length == 3) return teamMembers(true);
                    }

                    if (args[1].equalsIgnoreCase("list")) return blank;

                    if (args[1].equalsIgnoreCase("divide")) return blank;

                    if (args[1].equalsIgnoreCase("auto")) {
                        if (args.length == 3) {
                            List<String> auto = new ArrayList<String>();
                            auto.add("player");
                            auto.add("team");

                            return auto;
                        } else return blank;
                    }
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

    private List<String> teamMembers(boolean isJoin) {
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.players) {
            if ((isJoin && lap.getTeam() != null) || (!isJoin && lap.getTeam() == null))
                command.add(lap.getPlayer().getName());
        }
        return command;
    }
}
