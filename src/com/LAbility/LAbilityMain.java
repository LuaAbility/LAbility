package com.LAbility;

import com.LAbility.LuaUtility.LuaAbilityLoader;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.luaj.vm2.LuaFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;
    public static Plugin plugin;
    public UtilitiesWrapper utilitiesWrapper;
    public GameManager gameManager;
    public ArrayList<Ability> abilities;
    public boolean enabled = false;

    private final HashMap<Class<? extends Event>, ArrayList<LuaFunction>> eventListeners = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");

        getCommand("la").setExecutor(new CommandManager(this));
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (!new File(getDataFolder(), "Ability/0. ExampleFolder/data.yml").exists()) saveResource("Ability/0. ExampleFolder/data.yml", false);
        if (!new File(getDataFolder(), "Ability/0. ExampleFolder/main.lua").exists()) saveResource("Ability/0. ExampleFolder/main.lua", false);

        abilities = LuaAbilityLoader.LoadAllLuaAbilities();
        gameManager = new GameManager();
        gameManager.isGameStarted = true;

        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 " + abilities.size() + "개 능력 로드 완료!");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
        enabled = true;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 비활성화 되었습니다.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
        enabled = false;
    }

    public Listener registerEvent(Ability ability, Class<? extends Event> event, LuaFunction function) {
        getEventListeners(event).add(function);
        Listener listener = new Listener() {};
        this.getServer().getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                gameManager.RunEvent(ability, function, event);
            }
        }, this, false);
        return null;
    }

    public int addPassiveScript(Ability ability, LuaFunction function) {
        ability.abilityFunc = function;

        return 0;
    }

    private ArrayList<LuaFunction> getEventListeners(Class<? extends Event> event) {
        this.eventListeners.computeIfAbsent(event, k -> new ArrayList<>());
        return this.eventListeners.get(event);
    }
}
