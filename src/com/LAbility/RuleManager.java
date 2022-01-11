package com.LAbility;

import org.bukkit.event.Event;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;
import java.util.Map;

import static com.LAbility.LuaUtility.LuaAbilityLoader.setGlobals;

public class RuleManager {
    public String ruleLocation;
    public Map<String, Class<? extends Event>> ruleFunc = new HashMap();

    Globals globals;
    LuaValue script;

    public void InitScript(){
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(ruleLocation);
        globals = setGlobals(globals);
        script.call();
        globals.get("Init").call();
    }

    public void RunEvent(Event event) {
        if (LAbilityMain.instance.gameManager.isGameStarted){
            for( Map.Entry<String, Class<? extends Event>> func : ruleFunc.entrySet() ){
                if ((func.getValue().equals(event.getClass()) || func.getValue().isInstance(event)) || func.getValue().isAssignableFrom(event.getClass())) {
                    globals = JsePlatform.standardGlobals();
                    script = globals.loadfile(ruleLocation);
                    globals = setGlobals(globals);

                    script.call();
                    if (!globals.get("onEvent").isnil()) globals.get("onEvent").call(CoerceJavaToLua.coerce(func.getKey()), CoerceJavaToLua.coerce(event));
                }
            }
        }
    }

    public void runPassiveFunc() {
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(ruleLocation);
        globals = setGlobals(globals);

        script.call();
        if (!globals.get("onTimer").isnil()) globals.get("onTimer").call();
    }

    public void runResetFunc() {
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(ruleLocation);
        globals = setGlobals(globals);
        script.call();
        if (!globals.get("Reset").isnil()) globals.get("Reset").call();
    }
}
