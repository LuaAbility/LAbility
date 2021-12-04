package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {
	public final LAbilityMain main;

	public CommandManager(LAbilityMain main_) {
		main = main_;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("la")) {
			if (args.length == 0) {
				LAPlayer temp = new LAPlayer();
				temp.player = sender.getServer().getPlayer(sender.getName());
				temp.ability.add(LAbilityMain.instance.abilities.get(0));
				LAbilityMain.instance.gameManager.players.add(temp);
				return true;
			}
		}
		return false;
	}
}
