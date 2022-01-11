package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.LuaException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class UtilitiesWrapper extends LuaTable {
    public UtilitiesWrapper(LAbilityMain plugin) {
        set("getTableFromList", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Object[] list = new Object[]{};

                if (arg.checkuserdata() instanceof Collection) {
                    list = ((Collection<?>) arg.touserdata()).toArray();
                } else if (arg.touserdata() instanceof Stream) {
                    list = ((Stream<?>) arg.touserdata()).toArray();
                } else if (arg.touserdata() instanceof ArrayList) {
                    list = ((ArrayList<?>) arg.touserdata()).toArray();
                } else if (!(arg.touserdata() instanceof Iterator)) {
                    throw new LuaException("util.tableFromList(obj) was passed something other than an instance of Collection or Stream.", 1);
                }

                LuaTable t = new LuaTable();
                if (arg.touserdata() instanceof Iterator){
                    Iterator<?> iter = (Iterator<?>) arg.touserdata(Iterator.class);
                    int i = 1;
                    while (iter.hasNext()) t.set(LuaValue.valueOf(i), CoerceJavaToLua.coerce(iter.next()));
                }
                else {
                    for (int i = 0; i < list.length; i++) {
                        t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
                    }
                }

                return t;
            }
        });

        set("getTableFromArray", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Object[] list = (Object[]) arg.touserdata();
                LuaTable t = new LuaTable();
                for (int i = 0; i < list.length; i++) {
                    t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
                }

                return t;
            }
        });

        set("getTableFromMap", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Map<?, ?> map;

                if (arg.checkuserdata() instanceof Map) {
                    map = (Map<?, ?>) arg.touserdata();
                } else {
                    throw new LuaException("util.tableFromMap(obj) was passed something other than a implementation of Map.", 1);
                }

                LuaTable t = new LuaTable();
                map.forEach((k, v) -> t.set(CoerceJavaToLua.coerce(k), CoerceJavaToLua.coerce(v)));

                return t;
            }
        });

        set("runLater", new TwoArgFunction() {
            // Delay is in milliseconds.
            @Override
            public LuaValue call(LuaValue function, LuaValue time) {
                var task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        function.checkfunction().call();
                    }
                }.runTaskLater(LAbilityMain.plugin, time.checklong());
                int taskID = task.getTaskId();
                return CoerceJavaToLua.coerce(taskID);
            }
        });

        set("cancelRunLater", new OneArgFunction() {
            // Delay is in milliseconds.
            @Override
            public LuaValue call(LuaValue id) {
                Bukkit.getScheduler().cancelTask(id.checkint());
                return NIL;
            }
        });

        set("getClass", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                try {
                    return CoerceJavaToLua.coerce(Class.forName(path.checkjstring()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return NIL;
            }
        });

        // Temporary method, fixed in v3
        set("cast", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue userdata, LuaValue clazz) {
                String className = clazz.checkjstring();
                Object obj = userdata.checkuserdata();

                try {
                    Class<?> caster = Class.forName(className);
                    return userdataOf(caster.cast(obj));
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Could not find class " + className);
                } catch (ClassCastException e) {
                    plugin.getLogger().warning("Provided userdata cannot be casted to " + className);
                } catch (LinkageError e) {
                    plugin.getLogger().warning("There was an unknown issue casting the object to " + className);
                    e.printStackTrace();
                }

                return NIL;
            }
        });

        set("hasClass", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue userdata, LuaValue clazz) {
                String className = clazz.checkjstring();
                Object obj = userdata.checkuserdata();

                if (className.startsWith("$"))
                    className = "org.bukkit" + className.substring(1);

                try {
                    Class<?> caster = Class.forName(className);
                    if (caster.isInstance(obj) ) return CoerceJavaToLua.coerce(true);
                    else return CoerceJavaToLua.coerce(false);
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Could not find class " + className);
                } catch (ClassCastException e) {
                    plugin.getLogger().warning("Provided userdata cannot be casted to " + className);
                } catch (LinkageError e) {
                    plugin.getLogger().warning("There was an unknown issue casting the object to " + className);
                    e.printStackTrace();
                }

                return CoerceJavaToLua.coerce(false);
            }
        });
    }
}
