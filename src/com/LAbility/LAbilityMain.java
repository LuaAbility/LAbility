package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("\2471[\247bLAbility\2471] \2477v1.0 활성화 되었어요.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("\2471[\247bLAbility\2471] \2477v1.0 비활성화 되었어요.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }
}
