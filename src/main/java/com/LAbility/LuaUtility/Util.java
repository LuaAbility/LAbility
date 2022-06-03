package com.LAbility.LuaUtility;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class Util {
    public static <T> ArrayList<T> luaTableToArrayList(LuaTable table) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 1; i <= table.length(); i++) {
            list.add((T) table.get(i).optuserdata(null));
        }
        return list;
    }

    public static LuaTable getTableFromList(Object arg) {
        Object[] list = new Object[]{};

        if (arg instanceof Collection) {
            list = ((Collection<?>) arg).toArray();
        } else if (arg instanceof Stream) {
            list = ((Stream<?>) arg).toArray();
        } else if (arg instanceof ArrayList) {
            list = ((ArrayList<?>) arg).toArray();
        } else if (!(arg instanceof Iterator)) {
            throw new LuaException("util.tableFromList(obj) was passed something other than an instance of Collection or Stream.", 1);
        }

        LuaTable t = new LuaTable();
        if (arg instanceof Iterator<?> iter) {
            int i = 1;
            while (iter.hasNext()) t.set(LuaValue.valueOf(i), CoerceJavaToLua.coerce(iter.next()));
        } else {
            for (int i = 0; i < list.length; i++) {
                t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
            }
        }

        return t;
    }

    public static LuaTable getTableFromArray(Object arg) {
        Object[] list = (Object[]) arg;
        LuaTable t = new LuaTable();
        for (int i = 0; i < list.length; i++) {
            t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
        }

        return t;
    }

    public static LuaTable getTableFromMap(Object arg) {
        Map<?, ?> map;

        if (arg instanceof Map) {
            map = (Map<?, ?>) arg;
        } else {
            throw new LuaException("util.tableFromMap(obj) was passed something other than a implementation of Map.", 1);
        }

        LuaTable t = new LuaTable();
        map.forEach((k, v) -> t.set(CoerceJavaToLua.coerce(k), CoerceJavaToLua.coerce(v)));

        return t;
    }
}
