package com.LAbility;

import com.LAbility.LuaUtility.List.FunctionList;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;
import java.util.Map;

import static com.LAbility.LuaUtility.LuaAbilityLoader.setGlobals;

public class LARule {
    public String ruleID;
    public String ruleName;
    public String ruleDesc;
    public String luaScript;
    public Map<String, Class<? extends Event>> ruleFunc = new HashMap();

    Globals globals;
    LuaValue script;
    boolean syncScript = true;

    public void sync(boolean sync){
        syncScript = sync;
    }

    public LARule(String id, String name, String desc, String script) {
        ruleID = id;
        ruleName = name;
        ruleDesc = desc;
        luaScript = script;
    }

    public LARule(LARule r) {
        ruleID = r.ruleID;
        ruleName = r.ruleName;
        ruleDesc = r.ruleDesc;
        luaScript = r.luaScript;
    }

    public void InitScript(){
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(luaScript);
        globals = setGlobals(globals);
        script.call();

        globals.get("Init").call(CoerceJavaToLua.coerce(this));
    }

    public void RunEvent(Event event) {
        if (LAbilityMain.instance.gameManager.isGameStarted){
            for( Map.Entry<String, Class<? extends Event>> func : ruleFunc.entrySet() ){
                if ((func.getValue().equals(event.getClass()) || func.getValue().isInstance(event)) || func.getValue().isAssignableFrom(event.getClass())) {
                    if (syncScript) {
                        globals = JsePlatform.standardGlobals();
                        script = globals.loadfile(luaScript);
                        globals = setGlobals(globals);
                        script.call();
                    }

                    if (!globals.get("onEvent").isnil()) globals.get("onEvent").call(CoerceJavaToLua.coerce(func.getKey()), CoerceJavaToLua.coerce(event));
                }
            }
        }
    }

    public void runPassiveFunc() {
        if (syncScript) {
            globals = JsePlatform.standardGlobals();
            script = globals.loadfile(luaScript);
            globals = setGlobals(globals);
            script.call();
        }

        if (!globals.get("onTimer").isnil()) globals.get("onTimer").call();
    }

    public void runResetFunc() {
        if (syncScript) {
            globals = JsePlatform.standardGlobals();
            script = globals.loadfile(luaScript);
            globals = setGlobals(globals);
            script.call();
        }

        if (!globals.get("Reset").isnil()) globals.get("Reset").call();
    }

    public void ExplainRule(CommandSender player) {
        player.sendMessage("\2476[\247e" + ruleName + "\2476]");
        player.sendMessage("\247a" + ruleDesc);
    }
}
