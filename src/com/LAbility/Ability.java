package com.LAbility;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

public class Ability {
    public int abilityCooldown;
    public boolean isPassive;
    public String abilityID;
    public String abilityName;
    public String abilityRank;
    public String abilityDesc;
    public LuaFunction abilityFunc;

    public Ability(String id, int cooldown, boolean passive, String name, String rank, String desc) {
        abilityID = id;
        abilityCooldown = cooldown;
        isPassive = passive;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
    }
}
