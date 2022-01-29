package com.LAbility;

import com.LAbility.LuaUtility.List.FunctionList;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.LAbility.LuaUtility.LuaAbilityLoader.setGlobals;

public class Ability {
    public static class AbilityFunc{
        public String funcID;
        public Class<? extends Event> funcEvent;
        public int cooldown;
        public int currentTime;
        public BukkitTask currentTask = null;

        public AbilityFunc(String ID, Class<? extends Event> event, int cool){
            funcID = ID;
            funcEvent = event;
            cooldown = cool;
            currentTime = (int) (cool * LAbilityMain.instance.gameManager.cooldownMultiply);
        }

        public AbilityFunc(AbilityFunc af){
            funcID = af.funcID;
            funcEvent = af.funcEvent;
            cooldown = af.cooldown;
            currentTime = (int) (af.cooldown * LAbilityMain.instance.gameManager.cooldownMultiply);
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
    public String luaScript;
    public FunctionList<AbilityFunc> abilityFunc = new FunctionList<>();

    Globals globals;
    LuaValue script;
    boolean syncScript = true;

    public Ability(String id, String type, String name, String rank, String desc, String script) {
        abilityID = id;
        abilityType = type;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
        luaScript = script;
    }

    public Ability(Ability a) {
        abilityID = a.abilityID;
        abilityType = a.abilityType;
        abilityName = a.abilityName;
        abilityRank = a.abilityRank;
        abilityDesc = a.abilityDesc;
        luaScript = a.luaScript;
        abilityFunc = new FunctionList<>();
        for (Ability.AbilityFunc af : a.abilityFunc) {
            abilityFunc.add(new AbilityFunc(af));
        }
    }

    public void sync(boolean sync){
        syncScript = sync;
    }

    public void InitScript(){
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(luaScript);
        globals = setGlobals(globals);
        script.call();

        globals.get("Init").call(CoerceJavaToLua.coerce(this));
    }

    public void runAbilityFunc(LAPlayer lap, Event event) {
        if (abilityFunc.contains(event)) {
            for (Ability.AbilityFunc af : abilityFunc) {
                if ((af.funcEvent.isAssignableFrom(event.getClass()) || af.funcEvent.isInstance(event)) || af.funcEvent.equals(event.getClass())) {
                    if (syncScript) {
                        globals = JsePlatform.standardGlobals();
                        script = globals.loadfile(luaScript);
                        globals = setGlobals(globals);
                        script.call();
                    }

                    LuaTable table = new LuaTable();
                    table.insert(1, CoerceJavaToLua.coerce(af.funcID));
                    table.insert(2, CoerceJavaToLua.coerce(event));
                    table.insert(3, CoerceJavaToLua.coerce(lap));
                    table.insert(4, CoerceJavaToLua.coerce(this));

                    if (!globals.get("onEvent").isnil()) globals.get("onEvent").call(table);
                }
            }
        }
    }

    public void runPassiveFunc(LAPlayer lap) {
        if (syncScript) {
            globals = JsePlatform.standardGlobals();
            script = globals.loadfile(luaScript);
            globals = setGlobals(globals);
            script.call();
        }

        if (!globals.get("onTimer").isnil()) globals.get("onTimer").call(CoerceJavaToLua.coerce(lap), CoerceJavaToLua.coerce(this));
    }

    public boolean CheckCooldown(LAPlayer lap, String ID, boolean showMessage) {
        if (lap.getVariable("abilityLock") != null && lap.getVariable("abilityLock").equals(true)) return false;
        if (!abilityFunc.contains(ID)) return false;

        int index = abilityFunc.indexOf(ID);

        if ((abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) <= 0) return true;

        if (abilityFunc.get(index).currentTime >= (abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply)) {
            if (abilityFunc.get(index).currentTask != null) abilityFunc.get(index).currentTask.cancel();
            abilityFunc.get(index).currentTime = 0;
            abilityFunc.get(index).currentTask = new BukkitRunnable() {
                @Override
                public void run() { abilityFunc.get(index).currentTime++; }
            }.runTaskTimer(LAbilityMain.plugin, 0, 1);

            if (showMessage) lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b능력을 사용했습니다." );
            return true;
        }

        double cooldown = ((abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) - abilityFunc.get(index).currentTime) / 20.0;
        if (showMessage) lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임 입니다. (" + cooldown + "s)" );
        return false;
    }

    public void runResetFunc(LAPlayer lap) {
        if (syncScript) {
            globals = JsePlatform.standardGlobals();
            script = globals.loadfile(luaScript);
            globals = setGlobals(globals);
        }
        script.call();

        if (!globals.get("Reset").isnil()) globals.get("Reset").call(CoerceJavaToLua.coerce(lap), CoerceJavaToLua.coerce(this));

        String[] splitID = abilityID.split("-");
        if (splitID.length >= 3) {
            String targetID = splitID[1] + splitID[2];

            ArrayList<String> removeIDList = new ArrayList<>();
            for (Map.Entry<String, Object> variable : lap.variable.entrySet()) {
                if (variable.getKey().contains(targetID)) {
                    removeIDList.add(variable.getKey());
                }
            }

            for (String i : removeIDList) {
                lap.variable.remove(i);
            }
        }
    }

    public void resetCooldown() {
        for (Ability.AbilityFunc af : abilityFunc) resetCooldown(af.funcID);
    }

    public void resetCooldown(String ID) {
        if (!abilityFunc.contains(ID)) return;
        int index = abilityFunc.indexOf(ID);
        abilityFunc.get(index).currentTime = (int) (abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) + 100;
    }

    public void setTime(String ID, int reset) {
        if (!abilityFunc.contains(ID)) return;
        int index = abilityFunc.indexOf(ID);
        abilityFunc.get(index).currentTime = reset;
    }

    public void stopActive(LAPlayer lap) {
        for (Ability.AbilityFunc af : abilityFunc) {
            if (af.currentTask != null) af.currentTask.cancel();
        }


        runResetFunc(lap);
    }

    public void ExplainAbility(CommandSender player) {
        player.sendMessage("\2476[\247e" + abilityName + "\2476]");
        player.sendMessage("\247eRank : \247a" + abilityRank);
        player.sendMessage("\247eType : \247a" + abilityType);
        player.sendMessage("\247a" + abilityDesc);
    }
}
