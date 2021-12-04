package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.LuaException;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.logging.Logger;

public class LoggerWrapper extends LuaTable {
    private Logger logger;

    public LoggerWrapper(final Plugin plugin) {
        this.logger = plugin.getLogger();

        set("info", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                logger.info(arg.checkjstring());
                return LuaValue.NIL;
            }
        });

        set("warn", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                logger.warning(arg.checkjstring());
                return LuaValue.NIL;
            }
        });

        set("severe", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                logger.severe(arg.checkjstring());
                return LuaValue.NIL;
            }
        });

        set("debug", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                logger.fine(arg.checkjstring());
                return LuaValue.NIL;
            }
        });
    }
}
