package com.LAbility.LuaUtility.List;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;

import java.util.ArrayList;
import java.util.Comparator;

public class AbilityList<E extends Ability> extends ArrayList<Ability> {
    public static class AbilityComparator implements Comparator<Ability> {
        @Override
        public int compare(Ability o1, Ability o2) {
            if(o1.abilityID.compareTo(o2.abilityID) > 0) {
                return 1;
            }
            else if(o1.abilityID.compareTo(o2.abilityID) == 0) {
                return 0;
            }
            else {
                return -1;
            }
        }
    }

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

    public boolean remove(Ability ability, LAPlayer lap) {
        ability.stopActive(lap);
        return super.remove(ability);
    }

    public void clear(LAPlayer lap) {
        for (Ability a : this) a.stopActive(lap);
        super.clear();
    }

    @Override
    public void sort(Comparator<? super Ability> c) {
        super.sort(c);
    }
}