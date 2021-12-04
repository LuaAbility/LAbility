package com.LAbility;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Ability {
    public static class CooldownData {
        public int maxCooldown;
        public int currentCooldown;
        public int currentSchedule;

        public CooldownData(int cooldown){
            maxCooldown = cooldown;
            currentCooldown = cooldown;
            currentSchedule = 0;
        }
    }

    public static class ActiveFunc {
        public Class<? extends Event> event;
        public CooldownData cooldown;
        public LuaFunction function;
        public ActiveFunc(Class<? extends Event> eve, int cooldowns, LuaFunction func) {
            event = eve;
            cooldown = new CooldownData(cooldowns);
            function = func;
        }
    }

    public static class PassiveFunc {
        public int delay;
        public LuaFunction function;

        public PassiveFunc(int del, LuaFunction func) {
            delay = del;
            function = func;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ability ability = (Ability) o;
        return Objects.equals(abilityID, ability.abilityID) && Objects.equals(abilityType, ability.abilityType) && Objects.equals(abilityName, ability.abilityName) && Objects.equals(abilityRank, ability.abilityRank) && Objects.equals(abilityDesc, ability.abilityDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abilityID, abilityType, abilityName, abilityRank, abilityDesc);
    }

    public String abilityID;
    public String abilityType;
    public String abilityName;
    public String abilityRank;
    public String abilityDesc;
    public ArrayList<ActiveFunc> eventFunc = new ArrayList<ActiveFunc>() {
        @Override
        public boolean contains(Object o) {
            if (o instanceof Event) {
                Event event = (Event) o;
                for (ActiveFunc af : this) {
                    if (af.event.equals(event.getClass())) return true;
                }
            }
            else if (o instanceof CooldownData) {
                CooldownData cooldown = (CooldownData) o;
                for (ActiveFunc af : this) {
                    if (af.cooldown.equals(cooldown)) return true;
                }
            }
            else if (o instanceof LuaFunction) {
                LuaFunction func = (LuaFunction) o;
                for (ActiveFunc af : this) {
                    if (af.function.equals(func)) return true;
                }
            }
            return false;
        }
    };
    public ArrayList<PassiveFunc> passiveFunc = new ArrayList<PassiveFunc>();

    public Ability(String id, String type, String name, String rank, String desc) {
        abilityID = id;
        abilityType = type;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
    }

    public void UseEventFunc(Event event){
        for( ActiveFunc af : eventFunc ){
            if (af.event.equals(event.getClass())) {
                af.function.call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(event));
            }
        }
    }

    public boolean CheckCooldown(int index) {
        if (eventFunc.get(index).cooldown.maxCooldown <= 0) return true;

        if (eventFunc.get(index).cooldown.currentCooldown >= eventFunc.get(index).cooldown.maxCooldown) {
            LAbilityMain.plugin.getServer().getScheduler().cancelTask(eventFunc.get(index).cooldown.currentSchedule);
            eventFunc.get(index).cooldown.currentCooldown = 0;
            eventFunc.get(index).cooldown.currentSchedule = LAbilityMain.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.plugin, new Runnable() {
                @Override
                public void run() {
                    eventFunc.get(index).cooldown.currentCooldown++;
                }
            }, 0, 1);
            return true;
        }

        return false;
    }

    public void ExplainAbility(Player player) {
        player.sendMessage("\2476[\247e" + abilityName + "\2476]");
        player.sendMessage("\247eRank : \247a" + abilityRank);
        player.sendMessage("\247eType : \247a" + abilityType);
        player.sendMessage("\247a" + abilityDesc);
    }
}
