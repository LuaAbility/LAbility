package com.LAbility;

import com.LAbility.LuaUtility.FunctionList;
import org.bukkit.Bukkit;
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

        public CooldownData(CooldownData cd){
            maxCooldown = cd.maxCooldown;
            currentCooldown = cd.maxCooldown;
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

        public ActiveFunc(ActiveFunc af) {
            event = af.event;
            cooldown = new CooldownData(af.cooldown);
            function = af.function;
        }
    }

    public static class PassiveFunc {
        public int delay;
        public LuaFunction function;
        public int scheduler;

        public PassiveFunc(int del, LuaFunction func) {
            delay = del;
            function = func;
            scheduler = 0;
        }

        public PassiveFunc(PassiveFunc pf) {
            delay = pf.delay;
            function = pf.function;
            scheduler = 0;
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
    public FunctionList<ActiveFunc> eventFunc = new FunctionList<ActiveFunc>() {
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
    public ArrayList<LuaFunction> resetFunc = new ArrayList<LuaFunction>();

    public Ability(String id, String type, String name, String rank, String desc) {
        abilityID = id;
        abilityType = type;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
    }

    public Ability(Ability a) {
        abilityID = a.abilityID;
        abilityType = a.abilityType;
        abilityName = a.abilityName;
        abilityRank = a.abilityRank;
        abilityDesc = a.abilityDesc;

        eventFunc = new FunctionList<ActiveFunc>();
        for (ActiveFunc af : a.eventFunc){
            ActiveFunc afs = new ActiveFunc(af);
            eventFunc.add(afs);
        }

        passiveFunc = new ArrayList<PassiveFunc>();
        for (PassiveFunc pf : a.passiveFunc){
            passiveFunc.add(new PassiveFunc(pf));
        }

        resetFunc = new ArrayList<LuaFunction>();
        for (LuaFunction pf : a.resetFunc){
            resetFunc.add(pf);
        }
    }

    public void UseEventFunc(Event event){
        for( ActiveFunc af : eventFunc ){
            if (af.event.isAssignableFrom(event.getClass()) || af.event.equals(event.getClass()) || af.event.isInstance(event)) {
                af.function.call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(event));
            }
        }
    }

    public boolean CheckCooldown(Player player, int index) {
        if ((eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply) <= 0) {
            return true;
        }

        if (eventFunc.get(index).cooldown.currentCooldown >= (eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply)) {
            LAbilityMain.plugin.getServer().getScheduler().cancelTask(eventFunc.get(index).cooldown.currentSchedule);
            eventFunc.get(index).cooldown.currentCooldown = 0;
            eventFunc.get(index).cooldown.currentSchedule = LAbilityMain.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.plugin, new Runnable() {
                @Override
                public void run() {
                    eventFunc.get(index).cooldown.currentCooldown++;
                }
            }, 0, 1);
            player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b능력을 사용했습니다." );
            return true;
        }
        double cooldown = (((eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply) - eventFunc.get(index).cooldown.currentCooldown) / 20.0);
        player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임 입니다. (" + cooldown + "s)" );

        return false;
    }

    public void ExplainAbility(Player player) {
        player.sendMessage("\2476[\247e" + abilityName + "\2476]");
        player.sendMessage("\247eRank : \247a" + abilityRank);
        player.sendMessage("\247eType : \247a" + abilityType);
        player.sendMessage("\247a" + abilityDesc);
    }
}
