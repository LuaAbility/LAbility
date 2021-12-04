package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class GameWrapper extends LuaTable {
    public GameWrapper(LAbilityMain plugin) {
        set("getPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.gameManager.players);
            }
        });

        set("checkCooldown", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
                Player player = (Player) arg1.checkuserdata(Player.class);
                Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
                Ability ability = (Ability) arg2.checkuserdata(Ability.class);
                Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");
                int funcID = arg3.checkint();
                Bukkit.getConsoleSender().sendMessage("Made by MINUTE.");

                for (LAPlayer players : LAbilityMain.instance.gameManager.players){
                    if (players.getPlayer().equals(player)) {
                        for (Ability abilities : players.getAbility()){
                            if (abilities.equals(ability)) {
                                return CoerceJavaToLua.coerce(abilities.CheckCooldown(funcID));
                            }
                        }
                    }
                }

                return CoerceJavaToLua.coerce(false);
            }
        });
    }
}
