package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;

public class GameManager {
    public boolean isGameStarted = false;
    public ArrayList<LAPlayer> players = new ArrayList<LAPlayer>();
    public int passiveSchedular = 0;

    public void RunEvent(Ability ability, LuaFunction func, Event event) {
        if (isGameStarted) {
            func.call(CoerceJavaToLua.coerce(ability), CoerceJavaToLua.coerce(event));
        }
    }

    public void RunAllPassive(){
        passiveSchedular = LAbilityMain.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.instance, new Runnable() {
            public void run() {
                if (isGameStarted) {
                    for (LAPlayer lap : players) {
                        for (Ability a : lap.ability) {
                            if (a.abilityFunc != null) {
                                a.abilityFunc.call(CoerceJavaToLua.coerce(lap.player));
                            }
                        }
                    }
                }
            }
        }, 0, 1);
    }
}
