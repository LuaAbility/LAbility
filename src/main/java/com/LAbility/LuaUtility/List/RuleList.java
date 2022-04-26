package com.LAbility.LuaUtility.List;

import com.LAbility.Ability;
import com.LAbility.LARule;

import java.util.ArrayList;
import java.util.Comparator;

public class RuleList<E extends LARule> extends ArrayList<LARule> {
    public static class RuleComparator implements Comparator<LARule> {
        @Override
        public int compare(LARule o1, LARule o2) {
            if(o1.ruleID.compareTo(o2.ruleID) > 0) {
                return 1;
            }
            else if(o1.ruleID.compareTo(o2.ruleID) == 0) {
                return 0;
            }
            else {
                return -1;
            }
        }
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof LARule) return super.indexOf(o);
        if (o instanceof String rID) {
            for (LARule r : this) {
                if (r.ruleID.equals(rID)){
                    return super.indexOf(r);
                }
            }
        }
        return -1;
    }

    @Override
    public void sort(Comparator<? super LARule> c) {
        super.sort(c);
    }
}