package com.LAbility;

import com.LAbility.LuaUtility.AbilityList;
import com.LAbility.LuaUtility.LuaAbilityLoader;
import com.LAbility.LuaUtility.TabManager;
import com.LAbility.LuaUtility.Wrapper.GameWrapper;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.*;
import java.util.ArrayList;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;
    public static Plugin plugin;
    public UtilitiesWrapper utilitiesWrapper;
    public GameWrapper gameWrapper;
    public GameManager gameManager;
    public RuleManager ruleManager;
    public ScheduleManager scheduleManager;
    public int hasError = 0;
    public AbilityList<Ability> abilities = new AbilityList<>();
    public ArrayList<Class<? extends Event>> registerdEventList = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        hasError = 0;
        ruleManager = new RuleManager();
        gameManager = new GameManager();
        scheduleManager = new ScheduleManager();

        if (!LAbilityMain.instance.getDataFolder().exists()){
            LAbilityMain.instance.getDataFolder().mkdir();
            (new File(LAbilityMain.instance.getDataFolder().toString() + "\\Ability")).mkdir();
        }

        registerdEventList = new ArrayList<>();
        LuaAbilityLoader.LoadLuaRules();
        abilities = LuaAbilityLoader.LoadAllLuaAbilities();

        getCommand("la").setExecutor(new CommandManager(this));
        getCommand("la").setTabCompleter(new TabManager(this));
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        assignAllPlayer();
        if (hasError > 0) Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.2 " + abilities.size() + "개 능력 로드 완료!");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        gameManager.OnGameEnd();

        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v0.2 비활성화 되었습니다.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    public Listener registerEvent(Ability ability, String funcName, Class<? extends Event> event, int cooldown) {
        if (!ability.abilityFunc.contains(funcName)) ability.abilityFunc.add( new Ability.AbilityFunc(funcName, event, cooldown) );
        if (!registerdEventList.contains(event)) addEvent(event);
        return null;
    }

    public Listener registerRuleEvent(Class<? extends Event> event, String funcID) {
        if (!ruleManager.ruleFunc.containsKey(funcID)) ruleManager.ruleFunc.put(funcID, event);
        if (!registerdEventList.contains(event)) addEvent(event);
        return null;
    }

    public void addEvent(Class<? extends Event> event){
        Listener listener = new Listener() {};
        this.getServer().getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                if (gameManager.isGameStarted) {
                    if (event.getClass().isAssignableFrom(Cancellable.class)) {
                        Cancellable temp = (Cancellable)event;
                        if (temp.isCancelled()) return;
                    }

                    gameManager.RunEvent(event);
                }
            }
        }, this, false);
        registerdEventList.add(event);
    }

    public void assignAllPlayer(){
        for (Player p : getServer().getOnlinePlayers()){
            if (!gameManager.players.contains(p)) gameManager.players.add(new LAPlayer(p));
        }
    }
}
