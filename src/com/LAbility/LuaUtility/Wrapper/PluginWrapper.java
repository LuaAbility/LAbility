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
                String funcName = vargs.checkjstring(2);
                String eventName = vargs.checkjstring(3);
                int cooldown = vargs.checkint(4);

                // Attempt to find the event Bukkit again
                String[] events = {"block", "enchantment", "entity", "hanging", "inventory", "player", "raid",
                        "server", "vehicle", "weather", "world"};

                for (String pkg : events) {
                    try {
                        Class<?> c = Class.forName("org.bukkit.event." + pkg + "." + eventName);
                        if (Event.class.isAssignableFrom(c) && c != null) {
                            return CoerceJavaToLua.coerce(plugin.registerEvent(ability, funcName, (Class<? extends Event>) c, cooldown));
                        }
                    } catch (ClassNotFoundException ignored) {
                        // This would spam the console anytime an event is registered if we print the stack trace
                    }
                }

                try {
                    Class<?> c = Class.forName("com.LAbility.Event." + eventName);
                    if (Event.class.isAssignableFrom(c) && c != null) {
                        return CoerceJavaToLua.coerce(plugin.registerEvent(ability, funcName, (Class<? extends Event>) c, cooldown));
                    }
                } catch (ClassNotFoundException ignored) {
                    // This would spam the console anytime an event is registered if we print the stack trace
                }

                throw new LuaException("Event " + eventName + " Not Found.", 1);
            }
        });

        set("registerRuleEvent", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String eventName = vargs.checkjstring(1);
                String funcName = vargs.checkjstring(2);

                // Attempt to find the event Bukkit again
                String[] events = {"block", "enchantment", "entity", "hanging", "inventory", "player", "raid",
                        "server", "vehicle", "weather", "world"};

                for (String pkg : events) {
                    try {
                        Class<?> c = Class.forName("org.bukkit.event." + pkg + "." + eventName);
                        if (Event.class.isAssignableFrom(c) && c != null) {
                            return CoerceJavaToLua.coerce(plugin.registerRuleEvent((Class<? extends Event>) c, funcName));
                        }
                    } catch (ClassNotFoundException ignored) {
                        // This would spam the console anytime an event is registered if we print the stack trace
                    }
                }

                try {
                    Class<?> c = Class.forName("com.LAbility.Event." + eventName);
                    if (Event.class.isAssignableFrom(c) && c != null) {
                        return CoerceJavaToLua.coerce(plugin.registerRuleEvent((Class<? extends Event>) c, funcName));
                    }
                } catch (ClassNotFoundException ignored) {
                    // This would spam the console anytime an event is registered if we print the stack trace
                }

                throw new LuaException("Event " + eventName + " Not Found.", 1);
            }
        });

        set("requireDataPack", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String dataPackName = vargs.checkjstring(1);
                String url = vargs.checkjstring(2);

                if (!plugin.dataPacks.containsKey(dataPackName)) plugin.dataPacks.put(dataPackName, url);
                return NIL;
            }
        });

        set("setResourcePackPort", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                int port = vargs.checkint(1);
                LAbilityMain.instance.webServer.port = port;
                return NIL;
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

                plugin.gameManager.overrideItem = overrideItem;
                plugin.gameManager.targetItem = targetItem;
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

        set("skipInformationOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                boolean skipInformation = vargs.checkboolean(1);

                plugin.gameManager.skipInformation = skipInformation;
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

        set("skipYesOrNoOption", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                boolean skipYesOrNo = vargs.checkboolean(1);

                plugin.gameManager.skipYesOrNo = skipYesOrNo;
                return NIL;
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
