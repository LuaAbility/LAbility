package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class GameManager {
    public boolean isGameStarted = false;


    public void RunEvent(LuaFunction func, Event event) {
        Bukkit.getConsoleSender().sendMessage("BBB");
        if (isGameStarted) {
            Bukkit.getConsoleSender().sendMessage("CCC");
            func.call(CoerceJavaToLua.coerce(event));
        }
    }
}
