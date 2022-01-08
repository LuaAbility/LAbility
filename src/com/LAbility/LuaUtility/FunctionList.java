package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;

import java.util.ArrayList;

public class FunctionList<E extends Ability.AbilityFunc> extends ArrayList<Ability.AbilityFunc> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof Ability.AbilityFunc) return super.contains(o);
        if (o instanceof Event event) {
            for (Ability.AbilityFunc af : this) {
                if (af.funcEvent.isAssignableFrom(event.getClass())) return true;
                if (af.funcEvent.equals(event.getClass())) return true;
                if (af.funcEvent.isInstance(event)) return true;
            }
        }
        if (o instanceof String ID) {
            int i = 0;
            for (Ability.AbilityFunc af : this) {
                if (af.funcID.equals(ID)) return true;
            }
        }
        return false;
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Ability.AbilityFunc) return super.indexOf(o);
        if (o instanceof String ID) {
            int i = 0;
            for (Ability.AbilityFunc af : this) {
                if (af.funcID.equals(ID)) return i;
            }
        }

        return -1;
    }
}
