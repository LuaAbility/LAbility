package com.LAbility;

import com.LAbility.LuaUtility.*;
import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.List.RuleList;
import com.LAbility.LuaUtility.Wrapper.GameWrapper;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import com.LAbility.Manager.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
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
    public TeamManager teamManager;
    public ScheduleManager scheduleManager;
    public ResourcePackManager packManager;
    public ResourcePackWebServer webServer;
    public int autoSkipTimer = 30;
    public int hasError = 0;
    public boolean useResourcePack = false;
    public boolean burntBlock = true;
    public boolean explodeBlock = true;
    public AbilityList<Ability> abilities = new AbilityList<>();
    public RuleList<LARule> rules = new RuleList<>();
    public ArrayList<Class<? extends Event>> registeredEventList = new ArrayList<>();
    public Map<String, String> dataPacks = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this.getServer().getPluginManager().getPlugin("LAbility");
        hasError = 0;
        teamManager = new TeamManager();
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
        getServer().getPluginManager().registerEvents(new BlockManager(), this);

        BlockManager.ResetData();
        gameManager.AbilityShuffle(true);

        assignAllPlayer();
        if (dataPacks.size() > 0) {
            try {
                if (webServer.start()) appendResourcePacks();
                else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩을 사용하지 않습니다. 일부 능력의 효과음이 재생되지 않습니다.");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩 생성 오류!");
                Bukkit.getConsoleSender().sendMessage(e.getMessage());
            }
        }

        if (rules.size() > 0) {
            rules.get(0).InitScript();
            Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e룰 [" + rules.get(0).ruleName + "]이(가) 적용되었습니다.");
        }
        else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c룰이 존재하지 않습니다. 게임이 정상적으로 진행되지 않을 수 있습니다.");

        if (hasError > 0) Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + hasError + "개의 능력을 로드하는데 문제가 생겼습니다. 해당 능력들은 로드하지 않습니다.");
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247ev" + instance.getDescription().getVersion() + " " + abilities.size() + "개 능력 로드 완료!");
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e미추첨 능력 개수 : " + (abilities.size() - gameManager.shuffledAbilityIndex.size()) + "개");
        Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(plugin);

        webServer.stopTask();
        gameManager.OnGameEnd(false);

        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \2477v" + instance.getDescription().getVersion() + " 비활성화 되었습니다.");
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
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e능력에 필요한 리소스팩을 다운로드 합니다.");
        String[] urlList = new String[dataPacks.values().size()];
        dataPacks.values().toArray(urlList);
        String[] fileList = packManager.downloadResourcePack(urlList);
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e리소스팩 " + fileList.length + "개 다운로드 완료!" );
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e리소스팩 결합 작업을 진행합니다." );
        packManager.patch(fileList);
        Bukkit.getConsoleSender().sendMessage("\2476[\247eLAbility\2476] \247e리소스팩 결합 완료!");
        useResourcePack = true;
    }
}
