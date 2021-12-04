package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.LuaException;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;

public class PluginWrapper extends LuaTable {
    private LAbilityMain plugin = LAbilityMain.instance;

    public PluginWrapper() {
        set("registerEvent", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                String eventName = arg1.checkjstring();
                LuaFunction callback = arg2.checkfunction();
                try {
                    // Try to see if the event is a class path, for custom events
                    Class<?> c = Class.forName(eventName);
                    if (Event.class.isAssignableFrom(c) && c != null) {
                        return CoerceJavaToLua.coerce(plugin.registerEvent((Class<? extends Event>) c, callback));
                    }
                } catch (ClassNotFoundException e) {
                    // Attempt to find the event Bukkit again
                    String[] events = {"block", "enchantment", "entity", "hanging", "inventory", "player", "raid",
                            "server", "vehicle", "weather", "world"};

                    for (String pkg : events) {
                        try {
                            Class<?> c = Class.forName("org.bukkit.event." + pkg + "." + eventName);
                            if (Event.class.isAssignableFrom(c) && c != null) {
                                return CoerceJavaToLua.coerce(plugin.registerEvent((Class<? extends Event>) c, callback));
                            }
                        } catch (ClassNotFoundException ignored) {
                            // This would spam the console anytime an event is registered if we print the stack trace
                        }
                    }
                }

                throw new LuaException("Event " + arg1.tostring() + " Not Found.", 1);
            }
        });

        set("getServer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.getServer());
            }
        });

        set("getPlugin", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin);
            }
        });
    }
}
