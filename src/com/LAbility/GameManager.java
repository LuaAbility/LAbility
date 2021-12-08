package com.LAbility;

import com.LAbility.LuaUtility.PlayerList;
import joptsimple.util.KeyValuePair;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    public boolean isGameStarted = false;
    public PlayerList<LAPlayer> players = new PlayerList<LAPlayer>();

    public ArrayList<Integer> passiveScheduler = new ArrayList<Integer>();
    public ArrayList<Integer> shuffledAbilityIndex = new ArrayList<Integer>();
    public int currentAbilityIndex = 0;

    public void ResetAll(){
        isGameStarted = false;
        currentAbilityIndex = 0;
        shuffledAbilityIndex = new ArrayList<Integer>();
        StopAllPassive();
        players = new PlayerList<LAPlayer>();
        LAbilityMain.instance.assignAllPlayer();
    }

    public void RunEvent(Ability ability, Event event) {
        if (isGameStarted){
            for (LAPlayer player : players){
                for (Ability ab : player.ability){
                    if (ab.abilityID.equals(ability.abilityID) && ab.eventFunc.contains(event)){
                        ab.UseEventFunc(event);
                        return;
                    }
                }
            }
        }
    }

    public void RunAllPassive() {
        if (isGameStarted) {
            for (LAPlayer player : players) {
                for (Ability ability : player.ability) {
                    for (Ability.PassiveFunc pf : ability.passiveFunc) {
                        Integer temp = 0;
                        temp = LAbilityMain.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.plugin, new Runnable() {
                            public void run() {
                                pf.function.call(CoerceJavaToLua.coerce(player.player));
                            }
                        }, 0, pf.delay);
                        passiveScheduler.add(temp);
                    }
                }
            }
        }
    }

    public void StopAllPassive(){
        for (LAPlayer player : players) player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        for (int i = 0; i < passiveScheduler.size(); i++){
            Bukkit.getScheduler().cancelTask(passiveScheduler.get(i));
        }
        passiveScheduler = new ArrayList<>();
    }

    public void StopAllActiveTimer(){
        for (Ability ability : LAbilityMain.instance.abilities) {
            for (Ability.ActiveFunc af : ability.eventFunc) {
                Bukkit.getScheduler().cancelTask(af.cooldown.currentSchedule);
            }
        }
        passiveScheduler = new ArrayList<>();
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
            int randomIndex = random.nextInt(size - 1);
            final int temp = shuffledAbilityIndex.get(0);
            shuffledAbilityIndex.set(0, shuffledAbilityIndex.get(randomIndex));
            shuffledAbilityIndex.set(randomIndex, temp);
        }
    }

    public void AssignAbility() {
        if (shuffledAbilityIndex.size() < 1) AbilityShuffle(true);
        for (LAPlayer player : players){
            AssignAbility(player);
        }
    }

    public void AssignAbility(LAPlayer player) {
        player.ability.add(new Ability(LAbilityMain.instance.abilities.get(shuffledAbilityIndex.get(0))));
        shuffledAbilityIndex.remove(0);

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
        ArrayList<Ability> tempArray = player.ability;
        if (tempArray.size() > 0) {
            for (Ability a : tempArray) {
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
}
