package com.LAbility.Manager;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LARule;
import com.LAbility.LAbilityMain;
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

            if (args[0].equalsIgnoreCase("rule") || args[0].equalsIgnoreCase("ruleset") ) {
                if (args.length == 2) return ruleList();
            }

            if (args[0].equalsIgnoreCase("ob")) {
                if (args.length == 2) return playerList();
            }

            if (args[0].equalsIgnoreCase("out")) {
                if (args.length == 2) return survivePlayerList();
            }

            if (args[0].equalsIgnoreCase("see")) {
                if (args.length == 2) return playerList();
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
        }
        return blank;
    }

    private List<String> abilityList(){
        List<String> command = new ArrayList<String>();
        for (Ability a : main.abilities) {
            command.add(a.abilityID);
        }
        return command;
    }

    private List<String> ruleList(){
        List<String> command = new ArrayList<String>();
        for (LARule a : main.rules) {
            command.add(a.ruleID);
        }
        return command;
    }

    private List<String> playerList(){
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.players) {
            command.add(lap.getPlayer().getName());
        }
        return command;
    }

    private List<String> survivePlayerList(){
        List<String> command = new ArrayList<String>();
        for (LAPlayer lap : main.gameManager.getSurvivePlayer()) {
            command.add(lap.getPlayer().getName());
        }
        return command;
    }

    private List<String> playerAbilityList(String playerName){
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
}
