package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
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

        set("checkCooldown", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                try {
                    Player player = (Player) vargs.checkuserdata(1, Player.class);
                    Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                    int funcID = vargs.checkint(3);

                    for (LAPlayer players : LAbilityMain.instance.gameManager.players) {
                        if (players.getPlayer().equals(player)) {
                            for (Ability abilities : players.getAbility()) {
                                if (abilities.equals(ability)) {
                                    return CoerceJavaToLua.coerce(abilities.CheckCooldown(funcID));
                                }
                            }
                        }
                    }
                } catch (Exception e){
                    Bukkit.getConsoleSender().sendMessage(e.getMessage());
                }
                return CoerceJavaToLua.coerce(false);
            }
        });
    }
}
