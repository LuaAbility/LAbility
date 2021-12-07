package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class GameWrapper extends LuaTable {
    public GameWrapper(LAbilityMain plugin) {
        set("getPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.gameManager.players);
            }
        });

        set("getPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1) {
                Player player = (Player) arg1.checkuserdata(Player.class);
                if (plugin.gameManager.players.indexOf(player) >= 0)
                    return CoerceJavaToLua.coerce(plugin.gameManager.players.get(plugin.gameManager.players.indexOf(player)));
                else return CoerceJavaToLua.coerce(false);
            }
        });

        set("checkCooldown", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                Player player = (Player) vargs.checkuserdata(1, Player.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                int funcID = vargs.checkint(3);

                for (LAPlayer players : LAbilityMain.instance.gameManager.players) {
                    if (players.getPlayer().equals(player)) {
                        for (Ability abilities : players.getAbility()) {
                            if (abilities.equals(ability)) {
                                return CoerceJavaToLua.coerce(abilities.CheckCooldown(player, funcID));
                            }
                        }
                    }
                }
                return CoerceJavaToLua.coerce(false);
            }
        });

        set("changeAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                Player player = (Player) vargs.checkuserdata(1, Player.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                String abilityID = vargs.checkjstring(3);
                boolean deleteAll = vargs.checkboolean(4);

                Ability newAbility;
                int aindex = LAbilityMain.instance.abilities.indexOf(abilityID);
                if (aindex >= 0) newAbility = LAbilityMain.instance.abilities.get(aindex);
                else return CoerceJavaToLua.coerce(false);

                for (LAPlayer players : LAbilityMain.instance.gameManager.players) {
                    if (players.getPlayer().equals(player)) {
                        players.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e능력의 영향으로 자신의 능력이 변경됩니다.");
                        players.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 재 확인 해주세요.");

                        if (deleteAll) players.getAbility().clear();
                        else players.getAbility().removeIf(abilities -> abilities.equals(ability));
                        players.getAbility().add(newAbility);

                        LAbilityMain.instance.gameManager.StopAllPassive();
                        LAbilityMain.instance.gameManager.RunAllPassive();
                    }
                }

                return CoerceJavaToLua.coerce(false);
            }
        });


        set("sendMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                Player player = (Player) arg1.checkuserdata(Player.class);
                String message = arg2.checkjstring();
                player.sendMessage(message);
                return CoerceJavaToLua.coerce(true);
            }
        });

        set("broadcastMessage", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.checkjstring();
                Bukkit.broadcastMessage(message);
                return CoerceJavaToLua.coerce(true);
            }
        });
    }
}
/*


 */
