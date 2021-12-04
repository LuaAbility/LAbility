package com.LAbility;

import com.LAbility.LuaUtility.PlayerList;
import joptsimple.util.KeyValuePair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;

public class GameManager {
    public boolean isGameStarted = false;
    public PlayerList<LAPlayer> players = new PlayerList<LAPlayer>();

    public ArrayList<Integer> passiveScheduler = new ArrayList<Integer>();

    public void RunEvent(Ability ability, Event event) {
        if (isGameStarted){
            for (LAPlayer player : players){
                for (Ability ab : player.ability){
                    if (ab.equals(ability) && ab.eventFunc.contains(event)){
                        ab.UseEventFunc(event);
                        return;
                    }
                }
            }
        }
    }

    public void RunAllPassive(){
        if (isGameStarted){
            for (LAPlayer player : players){
                for (Ability ability : player.ability){
                    for (Ability.PassiveFunc pf : ability.passiveFunc) {
                        Integer temp = 0;
                        temp = LAbilityMain.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.plugin, new Runnable() {
                            public void run() {
                                pf.function.call(CoerceJavaToLua.coerce(player.player));
                            }
                        }, 0, pf.delay);
                        passiveScheduler.add(temp);
                    };
                }
            }
        }
    }

    public void StopAllPassive(){
        for (int schedule : passiveScheduler){
            schedule = 0;
        }
        passiveScheduler = new ArrayList<>();
    }
}
