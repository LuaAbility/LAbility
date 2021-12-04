package com.LAbility;

import org.bukkit.Bukkit;
import org.luaj.vm2.LuaValue;

public class Ability {
    public int abilityID;
    public int abilityCooldown;
    public boolean isPassive;
    public String abilityName;
    public String abilityRank;
    public String abilityDesc;
    public LuaValue luaScript;

    public Ability(int id, int cooldown, boolean passive, String name, String rank, String desc, LuaValue lua) {
        abilityID = id;
        abilityCooldown = cooldown;
        isPassive = passive;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
        luaScript = lua;
        RunScript();
    }

    public void RunScript(){
        luaScript.call();
    }
}
