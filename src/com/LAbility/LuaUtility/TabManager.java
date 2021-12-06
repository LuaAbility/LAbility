package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import com.LAbility.ScheduleManager;
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
        if (commandSender instanceof Player) {
            if (args.length == 1) {
                List<String> basicCommand = new ArrayList<String>();
                basicCommand.add("player");
                basicCommand.add("admin");
                basicCommand.add("util");
                if (commandSender.isOp()) {
                    basicCommand.add("ob");
                    basicCommand.add("see");
                    basicCommand.add("add");
                    basicCommand.add("remove");
                    basicCommand.add("list");
                    basicCommand.add("reroll");
                    basicCommand.add("skip");
                    basicCommand.add("start");
                    basicCommand.add("stop");
                }
                basicCommand.add("check");
                basicCommand.add("ablist");
                basicCommand.add("ability");
                basicCommand.add("yes");
                basicCommand.add("no");

                Collections.sort(basicCommand);
                return basicCommand;
            } else if (args.length > 1) {
                if (args[0].equalsIgnoreCase("ablist")) {
                    List<String> advancedCommand = new ArrayList<String>();
                    int allData = main.abilities.size() / 8 + 1;
                    for (int i = 1; i <= allData; i++) {
                        advancedCommand.add(i + "");
                    }

                    return advancedCommand;
                }

                if (args[0].equalsIgnoreCase("ability")) {
                    return abilityList();
                }

                if (args[0].equalsIgnoreCase("ob")) {
                    return playerList();
                }

                if (args[0].equalsIgnoreCase("see")) {
                    return playerList();
                }

                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 2) return playerList();
                    if (args.length == 3) return abilityList();
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 2) return playerList();
                    if (args.length == 3) return abilityList();
                }

                if (args[0].equalsIgnoreCase("reroll")) {
                    return playerList();
                }
            }
        }
        return null;
    }

    private List<String> abilityList(){
        List<String> command = new ArrayList<String>();
        for (Ability a : main.abilities) {
            command.add(a.abilityID);
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
}
