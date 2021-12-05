package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.ArrayList;

public class AbilityList<E extends Ability> extends ArrayList<Ability> {
    @Override
    public int indexOf(Object o) {
        if (o instanceof Ability) return super.indexOf(o);
        if (o instanceof String aID) {
            for (Ability a : this) {
                if (a.abilityID.equals(aID)){
                    return super.indexOf(a);
                }
            }
        }
        return -1;
    }
}