package com.LAbility.LuaUtility;

import org.luaj.vm2.LuaError;

public class LuaException extends LuaError {
    public LuaException(String s, int level){
        super(s, level);
    }
}
