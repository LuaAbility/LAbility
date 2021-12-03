package com.LAbility;

import com.LAbility.LuaUtility.LuaAbilityLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;
    public static Plugin plugin;
    public ArrayList<Ability> abilities;

    @Override
    public void onEnable() {
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        abilities = LuaAbilityLoader.LoadAllLuaAbilities();

        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 " + abilities.size() + "개 능력 로드 완료!");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 비활성화 되었습니다.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }
}
