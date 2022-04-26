package com.LAbility.LuaUtility.List;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;

import java.util.ArrayList;
import java.util.Comparator;

public class BanIDList<E extends String> extends ArrayList<String> {
    public BanIDList() {
        this.add("HIDDEN");
    }

    @Override
    public void clear() {
        super.clear();
        this.add("HIDDEN");
    }
}