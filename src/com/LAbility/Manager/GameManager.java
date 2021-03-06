package com.LAbility.Manager;

import com.LAbility.Ability;
import com.LAbility.Event.GameEndEvent;
import com.LAbility.Event.PlayerEliminateEvent;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.List.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    public int currentRuleIndex = 0;

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
            for (Ability ab : player.getAbility()) {
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
        variable = new HashMap<>();
    }

    public void RunEvent(Event event) {
        if (isGameStarted){
            for (LAPlayer player : players){
                if (player.isSurvive) {
                    for (Ability ab : player.getAbility()) {
                        ab.runAbilityFunc(player, event);
                    }
                }
            }

            if (LAbilityMain.instance.rules.size() > 0) LAbilityMain.instance.rules.get(currentRuleIndex).RunEvent(event);
        }
    }

    public void RunPassive() {
        if (isGameStarted){
            if (LAbilityMain.instance.dataPacks.size() > 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        String url = LAbilityMain.instance.webServer.getWebIp() + player.getUniqueId();
                        player.setResourcePack(url, null, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            passiveTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (LAbilityMain.instance.rules.size() > 0 && !isTestMode) LAbilityMain.instance.rules.get(currentRuleIndex).runPassiveFunc();
                    for (LAPlayer player : players){
                        if (player.isSurvive && (player.getVariable("abilityLock") == null || player.getVariable("abilityLock").equals(false))) {
                            for (Ability ab : player.getAbility()) {
                                ab.runPassiveFunc(player);
                            }
                        }
                    }
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, 2);
        }
    }

    public void StopPassive() {
        if (passiveTask != null) passiveTask.cancel();
    }

    public void StopActive(LAPlayer player) {
         for (Ability ab : player.getAbility()) {
             ab.stopActive(player);
         }
    }

    public void AbilityShuffle(boolean resetShuffleIndex) {
        Random random = new Random();
        int size = 0;

        if (resetShuffleIndex) {
            size = LAbilityMain.instance.abilities.size();
            int hiddenCount = 0;
            for (int i = 0; i < size; i++) {
                if (!LAbilityMain.instance.abilities.get(i).abilityID.contains("HIDDEN")) {
                    shuffledAbilityIndex.add(i);
                }
                else hiddenCount++;
            }
            size -= hiddenCount;
        }
        else {
            size = shuffledAbilityIndex.size();
        }

        if (size < 2) return;
        for (int i = 0 ; i < 1000; i++) {
            int randomIndex = random.nextInt(0, size);
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
                while (player.getAbility().contains(temp.abilityID) || temp.abilityID.contains("HIDDEN")){
                    Bukkit.getConsoleSender().sendMessage(player.getPlayer().getName() + " / " + temp.abilityID);
                    temp = LAbilityMain.instance.abilities.get(random.nextInt(LAbilityMain.instance.abilities.size()));
                }
                Ability a = new Ability(temp);
                player.getAbility().add(a);
                a.InitScript();
            } else {
                Ability a = new Ability(LAbilityMain.instance.abilities.get(shuffledAbilityIndex.get(0)));
                player.getAbility().add(a);
                a.InitScript();
                shuffledAbilityIndex.remove(0);
            }
        }

        player.getPlayer().sendMessage("\2472[\247aLAbility\2472] \247a" + "????????? ????????? ?????????????????????.");
        player.getPlayer().sendMessage("\2472[\247aLAbility\2472] \247a" + "/la check " + (player.getAbility().size() - 1) + "??? ????????? ??????????????????.");
    }

    public void ResignAbility(LAPlayer player, Ability ability) {
        if (player.getAbility().contains(ability.abilityID)) {
            player.getAbility().remove(ability);
            if (!ability.abilityID.contains("HIDDEN")) shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(ability));
            AbilityShuffle(false);
        }
    }

    public void ResignAbility(LAPlayer player) {
        if (player.getAbility().size() > 0) {
            for (Ability a : player.getAbility()) {
                if (!a.abilityID.contains("HIDDEN")) shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(a.abilityID));
            }
            AbilityShuffle(false);

            player.getAbility().clear();
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

    public void EliminatePlayer(LAPlayer player){
        Bukkit.getPluginManager().callEvent(new PlayerEliminateEvent(player));

        for (Ability a : player.getAbility()) a.stopActive(player);
        player.isSurvive = false;
        player.getAbility().clear();
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
    }
    public void OnGameEnd(boolean isGoodEnd){
        if (isGameReady) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(players, isGoodEnd));

            Bukkit.broadcastMessage("\2476LAbility\247e??? ????????? ????????? ???????????????!");
            Bukkit.broadcastMessage("\247e???????????? ????????? ?????? ?????? ????????? ?????? ????????????. ??????????????????, ??? ?????? ??????????????? ???????????? ??????????????? :)");
            Bukkit.broadcastMessage("\247eMade by MINUTE. \2476( \247nhttps://forms.gle/G9hxtEv2U1ody2yKA\247r\2476 )");

            LAbilityMain.instance.scheduleManager.ClearTimer();
            if (LAbilityMain.instance.rules.size() > 0) LAbilityMain.instance.rules.get(currentRuleIndex).runResetFunc();
            LAbilityMain.instance.gameManager.ResetAll();
            LAbilityMain.instance.getServer().getScheduler().cancelTasks(LAbilityMain.plugin);
        }
    }

    public PlayerList<LAPlayer> getSurvivePlayer() {
        PlayerList<LAPlayer> survivePlayer = new PlayerList<LAPlayer>();
        for (LAPlayer lap : players){
            if (lap.isSurvive) survivePlayer.add(lap);
        }
        return survivePlayer;
    }
}
