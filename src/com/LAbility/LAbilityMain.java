package com.LAbility;

import com.LAbility.LuaUtility.AbilityList;
import com.LAbility.LuaUtility.LuaAbilityLoader;
import com.LAbility.LuaUtility.TabManager;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.luaj.vm2.LuaFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;
    public static Plugin plugin;
    public UtilitiesWrapper utilitiesWrapper;
    public GameManager gameManager;
    public int hasError = 0;
    public ArrayList<EventExecutor> oldExcutor = new ArrayList<>();
    public AbilityList<Ability> abilities = new AbilityList<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        hasError = 0;
        abilities = LuaAbilityLoader.LoadAllLuaAbilities();
        gameManager = new GameManager();

        getCommand("la").setExecutor(new CommandManager(this));
        getCommand("la").setTabCompleter(new TabManager(this));
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        getConfig().options().copyDefaults(true);
        saveConfig();

        assignAllPlayer();
        if (hasError > 0) Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 " + abilities.size() + "개 능력 로드 완료!");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.1 비활성화 되었습니다.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    public Listener registerEvent(Ability ability, Class<? extends Event> event, int cooldown, LuaFunction function) {
        ability.eventFunc.add( new Ability.ActiveFunc(event, cooldown, function) );
        Listener listener = new Listener() {};

        Bukkit.getConsoleSender().sendMessage(ability.abilityName + " / " + event.getName());
        this.getServer().getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                Bukkit.getConsoleSender().sendMessage(ability.abilityName + " / " + event.getClass().getName());
                gameManager.RunEvent(ability, event);
            }
        }, this, false);

        return null;
    }

    public int addPassiveScript(Ability ability, int tick, LuaFunction function) {
        ability.passiveFunc.add(new Ability.PassiveFunc(tick, function));
        return 0;
    }

    public void assignAllPlayer(){
        for (Player p : getServer().getOnlinePlayers()){
            if (!gameManager.players.contains(p)) gameManager.players.add(new LAPlayer(p));
        }
    }
}
