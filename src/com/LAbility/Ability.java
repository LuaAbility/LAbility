package com.LAbility;

import org.luaj.vm2.LuaValue;

public class Ability {
    public int abilityID;
    public String abilityName;
    public String abilityRank;
    public String abilityDesc;
    public LuaValue luaScript;

    public Ability(int id, String name, String rank, String desc, LuaValue lua) {
        abilityID = id;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
        luaScript = lua;
    }
}
