package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.luaj.vm2.LuaFunction;

import java.util.ArrayList;

public class FunctionList<E extends Ability.ActiveFunc> extends ArrayList<Ability.ActiveFunc> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof Ability.ActiveFunc) return super.contains(o);
        if (o instanceof Event event) {
            for (Ability.ActiveFunc af : this) {
                if (af.event.getName().equals(event.getClass().getName())) return true;
            }
        }
        else if (o instanceof Ability.CooldownData cooldown) {
            for (Ability.ActiveFunc af : this) {
                if (af.cooldown.equals(cooldown)) return true;
            }
        }
        else if (o instanceof LuaFunction func) {
            for (Ability.ActiveFunc af : this) {
                if (af.function.equals(func)) return true;
            }
        }
        return false;
    }
}
