package com.LAbility;

import com.LAbility.LuaUtility.AbilityList;
import com.LAbility.LuaUtility.LuaAbilityLoader;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    public boolean enabled = false;
    public ArrayList<Class<? extends Event>> registerdClassList = new ArrayList<Class<? extends Event>>();
    public AbilityList<Ability> abilities = new AbilityList<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        getCommand("la").setExecutor(new CommandManager(this));
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        getConfig().options().copyDefaults(true);
        saveConfig();
        if (!new File(getDataFolder(), "Ability/0. ExampleFolder/data.yml").exists()) saveResource("Ability/0. ExampleFolder/data.yml", false);
        if (!new File(getDataFolder(), "Ability/0. ExampleFolder/main.lua").exists()) saveResource("Ability/0. ExampleFolder/main.lua", false);

        abilities = LuaAbilityLoader.LoadAllLuaAbilities();
        gameManager = new GameManager();
        gameManager.isGameStarted = true;

        for (Player p : getServer().getOnlinePlayers()){
            gameManager.players.add(new LAPlayer(p));
        }

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

    public Listener registerEvent(Ability ability, Class<? extends Event> event, int cooldown, LuaFunction function) {
        ability.eventFunc.add( new Ability.ActiveFunc(event, cooldown, function) );
        Listener listener = new Listener() {};

        if (registerdClassList.contains(event)) return null;
        registerdClassList.add(event);
        this.getServer().getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                gameManager.RunEvent(ability, event);
            }
        }, this, false);

        return null;
    }

    public int addPassiveScript(Ability ability, int tick, LuaFunction function) {
        ability.passiveFunc.add(new Ability.PassiveFunc(tick, function));
        return 0;
    }
}
