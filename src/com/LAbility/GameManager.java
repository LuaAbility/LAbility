package com.LAbility;

import com.LAbility.ScheduleManager;
import com.LAbility.LuaUtility.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameManager {
    public boolean isGameReady = false;
    public boolean isGameStarted = false;
    public boolean isTestMode = false;
    public PlayerList<LAPlayer> players = new PlayerList<LAPlayer>();

    public BukkitTask passiveTask = null;

    public ArrayList<Integer> shuffledAbilityIndex = new ArrayList<Integer>();
    public int currentAbilityIndex = 0;

    public int abilityAmount = 1;
    public boolean overlapAbility = false;
    public boolean raffleAbility = true;
    public boolean canCheckAbility = true;
    public double cooldownMultiply = 1;
    public Material targetItem = Material.IRON_INGOT;
    public boolean overrideItem = false;
    public boolean skipYesOrNo = false;
    public boolean skipInformation = false;
    public Map<String, Object> variable = new HashMap<>();

    public Object getVariable(String key) {
        Object obj = variable.getOrDefault(key, null);
        if (!(obj == null)) {
            var data = obj.getClass().cast(obj);
            return data;
        }
        else return null;
    }

    public void setVariable(String key, Object value){
        if (variable.containsKey(key)) variable.replace(key, value);
        else addVariable(key, value);
    }

    public void addVariable(String key, Object value) {
        if (!variable.containsKey(key)) variable.put(key, value);
        else variable.replace(key, value);
    }

    public void removeVariable(String key) {
        if (variable.containsKey(key)) variable.remove(key);
    }

    public void ResetAll(){
        for (LAPlayer player : players){
            for (Ability ab : player.ability) {
                ab.runResetFunc(player);
            }
        }
        isGameStarted = false;
        isGameReady = false;
        isTestMode = false;
        currentAbilityIndex = 0;
        shuffledAbilityIndex = new ArrayList<Integer>();
        StopPassive();
        players = new PlayerList<LAPlayer>();
        LAbilityMain.instance.assignAllPlayer();
    }

    public void RunEvent(Event event) {
        if (isGameStarted){
            for (LAPlayer player : players){
                if (player.isSurvive) {
                    for (Ability ab : player.ability) {
                        ab.runAbilityFunc(player, event);
                    }
                }
            }

            LAbilityMain.instance.ruleManager.RunEvent(event);
        }
    }

    public void RunPassive() {
        if (isGameStarted){
            passiveTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!isTestMode) LAbilityMain.instance.ruleManager.runPassiveFunc();
                    for (LAPlayer player : players){
                        if (player.isSurvive) {
                            for (Ability ab : player.ability) {
                                ab.runPassiveFunc(player);
                            }
                        }
                    }
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, 1);
        }
    }

    public void StopPassive() {
        if (passiveTask != null) passiveTask.cancel();
    }

    public void StopActive(LAPlayer player) {
         for (Ability ab : player.ability) {
             ab.stopActive(player);
         }
    }

    public void AbilityShuffle(boolean resetShuffleIndex) {
        Random random = new Random();
        int size = 0;

        if (resetShuffleIndex) {
            size = LAbilityMain.instance.abilities.size();
            for (int i = 0; i < size; i++) {
                if (!LAbilityMain.instance.abilities.get(i).abilityID.contains("HIDDEN")) shuffledAbilityIndex.add(i);
            }
        }
        else {
            size = shuffledAbilityIndex.size();
        }

        if (size < 2) return;
        for (int i = 0 ; i < 1000; i++) {
            int randomIndex = random.nextInt(size);
            final int temp = shuffledAbilityIndex.get(0);
            shuffledAbilityIndex.set(0, shuffledAbilityIndex.get(randomIndex));
            shuffledAbilityIndex.set(randomIndex, temp);
        }
    }

    public void AssignAbility() {
        if (shuffledAbilityIndex.size() < 1) AbilityShuffle(true);
        for (LAPlayer player : players) {
            AssignAbility(player);
        }
    }

    public void AssignAbility(LAPlayer player) {
        for (int i = 0; i < abilityAmount; i++) {
            if (overlapAbility) {
                Random random = new Random();
                Ability temp = LAbilityMain.instance.abilities.get(random.nextInt(LAbilityMain.instance.abilities.size()));
                while (player.ability.contains(temp.abilityID) || temp.abilityID.contains("HIDDEN")){
                    Bukkit.getConsoleSender().sendMessage(player.player.getName() + " / " + temp.abilityID);
                    temp = LAbilityMain.instance.abilities.get(random.nextInt(LAbilityMain.instance.abilities.size()));
                }
                Ability a = new Ability(temp);
                player.ability.add(a);
                a.InitScript();
            } else {
                Ability a = new Ability(LAbilityMain.instance.abilities.get(shuffledAbilityIndex.get(0)));
                player.ability.add(a);
                a.InitScript();
                shuffledAbilityIndex.remove(0);
            }
        }

        player.player.sendMessage("\2472[\247aLAbility\2472] \247a" + "능력이 무작위 배정되었습니다.");
        player.player.sendMessage("\2472[\247aLAbility\2472] \247a" + "/la check " + (player.ability.size() - 1) + "로 능력을 확인해주세요.");
    }

    public void ResignAbility(LAPlayer player, Ability ability) {
        if (player.ability.contains(ability.abilityID)) {
            player.ability.remove(ability);
            if (!ability.abilityID.contains("HIDDEN")) shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(ability));
            AbilityShuffle(false);
        }
    }

    public void ResignAbility(LAPlayer player) {
        if (player.ability.size() > 0) {
            for (Ability a : player.ability) {
                if (!a.abilityID.contains("HIDDEN")) shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(a.abilityID));
            }
            AbilityShuffle(false);

            player.ability.clear();
        }
    }

    public void ResignAbility() {
        for (LAPlayer player : players){
            ResignAbility(player);
        }
    }

    public boolean IsAllAsigned(){
        for (LAPlayer player : players){
            if (player.isAssign == false) return false;
        }
        return true;
    }

    public void OnGameEnd(){

        LAbilityMain.instance.scheduleManager.ClearTimer();
        LAbilityMain.instance.ruleManager.runResetFunc();
        LAbilityMain.instance.gameManager.ResetAll();
        LAbilityMain.instance.getServer().getScheduler().cancelTasks(LAbilityMain.plugin);
    }
}
