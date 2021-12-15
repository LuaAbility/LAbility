package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.Ability;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.LuaException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;

public class PluginWrapper extends LuaTable {
    private LAbilityMain plugin = LAbilityMain.instance;

    public PluginWrapper() {
        set("registerEvent", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                Ability ability = (Ability) vargs.checkuserdata(1, Ability.class);
                String eventName = vargs.checkjstring(2);
                int cooldown = vargs.checkint(3);
                LuaFunction callback = vargs.checkfunction(4);

                // Attempt to find the event Bukkit again
                String[] events = {"block", "enchantment", "entity", "hanging", "inventory", "player", "raid",
                        "server", "vehicle", "weather", "world"};

                for (String pkg : events) {
                    try {
                        Class<?> c = Class.forName("org.bukkit.event." + pkg + "." + eventName);
                        if (Event.class.isAssignableFrom(c) && c != null) {
                            return CoerceJavaToLua.coerce(plugin.registerEvent(ability, (Class<? extends Event>) c, cooldown, callback));
                        }
                    } catch (ClassNotFoundException ignored) {
                        // This would spam the console anytime an event is registered if we print the stack trace
                    }
                }

                throw new LuaException("Event " + eventName + " Not Found.", 1);
            }
        });

        set("registerRuleEvent", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String eventName = vargs.checkjstring(1);
                LuaFunction callback = vargs.checkfunction(2);

                // Attempt to find the event Bukkit again
                String[] events = {"block", "enchantment", "entity", "hanging", "inventory", "player", "raid",
                        "server", "vehicle", "weather", "world"};

                for (String pkg : events) {
                    try {
                        Class<?> c = Class.forName("org.bukkit.event." + pkg + "." + eventName);
                        if (Event.class.isAssignableFrom(c) && c != null) {
                            return CoerceJavaToLua.coerce(plugin.registerRuleEvent((Class<? extends Event>) c, callback));
                        }
                    } catch (ClassNotFoundException ignored) {
                        // This would spam the console anytime an event is registered if we print the stack trace
                    }
                }

                throw new LuaException("Event " + eventName + " Not Found.", 1);
            }
        });

        set("registerRuleTimer", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                long delay = vargs.checklong(1);
                LuaFunction callback = vargs.checkfunction(2);

                return CoerceJavaToLua.coerce(plugin.registerRuleTimer(delay, callback));
            }
        });

        set("registerRuleLoopTimer", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                long delay = vargs.checklong(1);
                boolean runOnZeroTick = vargs.checkboolean(2);
                LuaFunction callback = vargs.checkfunction(3);

                return CoerceJavaToLua.coerce(plugin.registerRuleLoopTimer(delay, runOnZeroTick, callback));
            }
        });

        set("addPassiveScript", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                Ability ability = (Ability)arg1.checkuserdata(Ability.class);
                int tick = arg2.checkint();
                LuaFunction callback = arg3.checkfunction();

                return CoerceJavaToLua.coerce(plugin.addPassiveScript(ability, tick, callback));
            }
        });

        set("abilityAmountOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                int amount = vargs.checkint(1);
                boolean overlapAbility  = vargs.checkboolean(2);

                plugin.gameManager.abilityAmount = amount;
                plugin.gameManager.overlapAbility = overlapAbility;
                return NIL;
            }
        });

        set("abilityCheckOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                boolean canCheckAbility  = vargs.checkboolean(1);

                plugin.gameManager.canCheckAbility = canCheckAbility;
                return NIL;
            }
        });

        set("abilityItemOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                boolean overrideItem = vargs.checkboolean(1);
                Material targetItem = (Material)vargs.checkuserdata(2, Material.class);

                plugin.gameWrapper.overrideItem = overrideItem;
                plugin.gameWrapper.targetItem = targetItem;
                return NIL;
            }
        });

        set("raffleAbilityOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                boolean raffleAbility = vargs.checkboolean(1);

                plugin.gameManager.raffleAbility = raffleAbility;
                return NIL;
            }
        });

        set("cooldownMultiplyOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                double cooldownMultiply = vargs.checkdouble(1);

                plugin.gameManager.cooldownMultiply = cooldownMultiply;
                return NIL;
            }
        });

        set("onPlayerEnd", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1,LuaValue arg2) {
                Ability ability = (Ability)arg1.checkuserdata(Ability.class);
                LuaFunction func = arg2.checkfunction();
                return CoerceJavaToLua.coerce(plugin.addResetScript(ability, func));
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
