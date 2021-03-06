package com.LAbility;

import com.LAbility.LuaUtility.*;
import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.List.RuleList;
import com.LAbility.LuaUtility.Wrapper.GameWrapper;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import com.LAbility.Manager.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LAbilityMain extends JavaPlugin implements Listener {
    public static LAbilityMain instance;
    public static Plugin plugin;
    public UtilitiesWrapper utilitiesWrapper;
    public GameWrapper gameWrapper;
    public GameManager gameManager;
    public ScheduleManager scheduleManager;
    public ResourcePackManager packManager;
    public ResourcePackWebServer webServer;
    public int hasError = 0;
    public AbilityList<Ability> abilities = new AbilityList<>();
    public RuleList<LARule> rules = new RuleList<>();
    public ArrayList<Class<? extends Event>> registeredEventList = new ArrayList<>();
    public Map<String, String> dataPacks = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        hasError = 0;
        gameManager = new GameManager();
        scheduleManager = new ScheduleManager();
        dataPacks = new HashMap<>();
        packManager = new ResourcePackManager();
        webServer = new ResourcePackWebServer();

        if (!LAbilityMain.instance.getDataFolder().exists()){
            LAbilityMain.instance.getDataFolder().mkdir();
            (new File(LAbilityMain.instance.getDataFolder().toString() + "\\Ability")).mkdir();
        }

        registeredEventList = new ArrayList<>();
        rules = LuaAbilityLoader.LoadLuaRules();
        abilities = LuaAbilityLoader.LoadAllLuaAbilities();

        getCommand("la").setExecutor(new CommandManager(this));
        getCommand("la").setTabCompleter(new TabManager(this));
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        assignAllPlayer();
        if (dataPacks.size() > 0) {
            try {
                appendResourcePacks();
                webServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (rules.size() > 0) {
            rules.get(0).InitScript();
            Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e??? [" + rules.get(0).ruleName + "]???(???) ?????????????????????.");
        }
        else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c?????? ???????????? ????????????. ????????? ??????????????? ???????????? ?????? ??? ????????????.");

        if (hasError > 0) Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + hasError + "?????? ????????? ??????????????? ????????? ???????????????. ?????? ???????????? ???????????? ????????????.");
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247ev" + instance.getDescription().getVersion() + " " + abilities.size() + "??? ?????? ?????? ??????!");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        webServer.stopTask();
        gameManager.OnGameEnd(false);

        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v" + instance.getDescription().getVersion() + " ???????????? ???????????????.");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    public Listener registerEvent(Ability ability, String funcName, Class<? extends Event> event, int cooldown) {
        if (!ability.abilityFunc.contains(funcName)) ability.abilityFunc.add( new Ability.AbilityFunc(funcName, event, cooldown) );
        if (!registeredEventList.contains(event)) addEvent(event);
        return null;
    }

    public Listener registerRuleEvent(Class<? extends Event> event, String funcID) {
        if (!rules.get(gameManager.currentRuleIndex).ruleFunc.containsKey(funcID)) rules.get(gameManager.currentRuleIndex).ruleFunc.put(funcID, event);
        if (!registeredEventList.contains(event)) addEvent(event);
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
        registeredEventList.add(event);
    }

    public void assignAllPlayer(){
        for (Player p : getServer().getOnlinePlayers()){
            if (!gameManager.players.contains(p)) gameManager.players.add(new LAPlayer(p));
        }
    }

    public void appendResourcePacks() throws Exception {
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e????????? ????????? ??????????????? ???????????? ?????????.");
        String[] urlList = new String[dataPacks.values().size()];
        dataPacks.values().toArray(urlList);
        String[] fileList = packManager.downloadResourcePack(urlList);
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e???????????? " + fileList.length + "??? ???????????? ??????!" );
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e???????????? ?????? ????????? ???????????????." );
        packManager.patch(fileList);
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e???????????? ?????? ??????!");
    }
}
