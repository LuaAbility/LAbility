package com.LAbility;

import com.LAbility.LuaUtility.FunctionList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
        public BukkitTask currentSchedule;

        public CooldownData(int cooldown){
            maxCooldown = cooldown;
            currentCooldown = cooldown;
            currentSchedule = null;
        }

        public CooldownData(CooldownData cd){
            maxCooldown = cd.maxCooldown;
            currentCooldown = cd.maxCooldown;
            currentSchedule = cd.currentSchedule;
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
        public BukkitTask scheduler;

        public PassiveFunc(int del, LuaFunction func) {
            delay = del;
            function = func;
            scheduler = null;
        }

        public PassiveFunc(PassiveFunc pf) {
            delay = pf.delay;
            function = pf.function;
            scheduler = pf.scheduler;
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

    public void ResetCooldown(Player player, int index, boolean showMessage) {
        eventFunc.get(index).cooldown.currentCooldown = (int)(eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply);
        if (eventFunc.get(index).cooldown.currentSchedule != null) eventFunc.get(index).cooldown.currentSchedule.cancel();
        if (showMessage) player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임이 초기화 되었습니다." );
    }

    public boolean CheckCooldown(Player player, int index, boolean showMessage) {
        LAPlayer lap = LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(player.getName()));
        if (lap.getVariable("abilityLock").equals("true")) return false;

        if ((eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply) <= 0) {
            return true;
        }

        if (eventFunc.get(index).cooldown.currentCooldown >= (eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply)) {
            if (eventFunc.get(index).cooldown.currentSchedule != null) eventFunc.get(index).cooldown.currentSchedule.cancel();
            eventFunc.get(index).cooldown.currentCooldown = 0;
            eventFunc.get(index).cooldown.currentSchedule = new BukkitRunnable() {
                @Override
                public void run() {
                    eventFunc.get(index).cooldown.currentCooldown++;
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, 1);
            if (showMessage) player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b능력을 사용했습니다." );
            return true;
        }
        double cooldown = (((eventFunc.get(index).cooldown.maxCooldown * LAbilityMain.instance.gameManager.cooldownMultiply) - eventFunc.get(index).cooldown.currentCooldown) / 20.0);
        if (showMessage) player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임 입니다. (" + cooldown + "s)" );

        return false;
    }

    public void ExplainAbility(Player player) {
        player.sendMessage("\2476[\247e" + abilityName + "\2476]");
        player.sendMessage("\247eRank : \247a" + abilityRank);
        player.sendMessage("\247eType : \247a" + abilityType);
        player.sendMessage("\247a" + abilityDesc);
    }
}
