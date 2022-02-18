package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.*;
import com.LAbility.Event.AbilityConfirmEvent;
import com.LAbility.LuaUtility.List.PlayerList;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class GameWrapper extends LuaTable {
    public GameWrapper(LAbilityMain plugin) {
        set("getAbilityList", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.abilities);
            }
        });

        set("getPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.gameManager.getSurvivePlayer());
            }
        });

        set("getAllPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(plugin.gameManager.players);
            }
        });

        set("getPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1) {
                Player player = (Player) arg1.checkuserdata(Player.class);
                if (plugin.gameManager.players.indexOf(player.getName()) >= 0)
                    return CoerceJavaToLua.coerce(plugin.gameManager.players.get(plugin.gameManager.players.indexOf(player.getName())));
                else return NIL;
            }
        });

        set("checkCooldown", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1) || vargs.isnil(2)) return CoerceJavaToLua.coerce(false);
                LAPlayer abilityPlayer = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(2, LAPlayer.class);
                Ability ability = (Ability) vargs.checkuserdata(3, Ability.class);
                String funcID = vargs.checkjstring(4);
                boolean showMessage = vargs.isnil(5) || vargs.checkboolean(5);
                boolean callEvent = vargs.isnil(6) || vargs.checkboolean(6);

                if (abilityPlayer.getPlayer().getName().equals(player.getPlayer().getName())) {
                    if (player.getAbility().contains(ability.abilityID)) {
                        boolean check = ability.CheckCooldown(player, funcID, showMessage);
                        if (callEvent && check) Bukkit.getPluginManager().callEvent(new AbilityConfirmEvent(player, ability, funcID));
                        return CoerceJavaToLua.coerce(check);
                    }
                    return CoerceJavaToLua.coerce(false);
                }
                return CoerceJavaToLua.coerce(false);
            }
        });

        set("changeAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                String abilityID = vargs.checkjstring(3);
                boolean deleteAll = vargs.isnil(4) || vargs.checkboolean(4);

                Ability newAbility;
                int aindex = LAbilityMain.instance.abilities.indexOf(abilityID);
                if (aindex >= 0) newAbility = new Ability(LAbilityMain.instance.abilities.get(aindex));
                else return CoerceJavaToLua.coerce(false);


                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e자신의 능력이 변경되었습니다.");
                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 재 확인 해주세요.");

                if (deleteAll) {
                    for (Ability a : player.getAbility()) a.stopActive(player);
                    player.getAbility().clear();
                }
                else {
                    int abilityIndex = player.getAbility().indexOf(ability.abilityID);
                    for (Ability abilities : player.getAbility()) {
                        if (abilities.equals(ability)) {
                            abilities.stopActive(player);
                        }
                    }
                    player.getAbility().remove(abilityIndex);
                }

                player.getAbility().add(newAbility);
                newAbility.InitScript();
                return CoerceJavaToLua.coerce(false);
            }
        });

        set("removeAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                Ability ability =  vargs.isnil(2) ? null : (Ability) vargs.checkuserdata(2, Ability.class);
                boolean deleteAll = vargs.isnil(3) || vargs.checkboolean(3);

                player.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c자신의 능력이 제거됩니다.");
                player.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c/la check로 능력을 재 확인 해주세요.");

                if (deleteAll) {
                    for (Ability a : player.getAbility()) a.stopActive(player);
                    player.getAbility().clear();
                }
                else {
                    int abilityIndex = player.getAbility().indexOf(ability.abilityID);
                    if (abilityIndex >= 0) {
                        player.getAbility().get(abilityIndex).stopActive(player);
                        player.getAbility().remove(abilityIndex);
                    }
                    else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + player.getPlayer().getName() + " 플레이어는 " + ability + " 능력을 가지고 있지 않습니다.");
                }

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("removeAbilityAsID", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                String ability = vargs.checkjstring(2);

                player.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c자신의 능력이 제거됩니다.");
                player.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c/la check로 능력을 재 확인 해주세요.");

                int abilityIndex = player.getAbility().indexOf(ability);
                if (abilityIndex >= 0) {
                    player.getAbility().get(abilityIndex).stopActive(player);
                    player.getAbility().remove(abilityIndex);
                }
                else Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c" + player.getPlayer().getName() + " 플레이어는 " + ability + " 능력을 가지고 있지 않습니다.");

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("addAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                String abilityID = vargs.checkjstring(2);

                Ability newAbility;
                int aindex = LAbilityMain.instance.abilities.indexOf(abilityID);
                if (aindex >= 0) newAbility = new Ability(LAbilityMain.instance.abilities.get(aindex));
                else return CoerceJavaToLua.coerce(false);


                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e자신의 능력이 추가되었습니다.");
                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 재 확인 해주세요.");

                player.getAbility().add(newAbility);
                newAbility.InitScript();

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("hasAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                String abilityID = vargs.checkjstring(2);

                return CoerceJavaToLua.coerce(player.hasAbility(abilityID));
            }
        });

        set("getPlayerAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return new LuaTable();
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                return CoerceJavaToLua.coerce(player.getAbility());
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

        set("sendActionBarMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                Player player = (Player) arg1.checkuserdata(Player.class);
                String message = arg2.checkjstring();

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                return CoerceJavaToLua.coerce(true);
            }
        });

        set("sendActionBarMessageToAll", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1) {
                String message = arg1.checkjstring();

                for (LAPlayer lap : plugin.gameManager.players){
                    if (lap.isSurvive) lap.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                }

                return CoerceJavaToLua.coerce(true);
            }
        });

        set("isAbilityItem", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                if (vargs.isnil(1)) return CoerceJavaToLua.coerce(false);
                ItemStack item = (ItemStack) vargs.checkuserdata(1, ItemStack.class);
                String targetItems = vargs.checkjstring(2);

                if (LAbilityMain.instance.gameManager.overrideItem) return CoerceJavaToLua.coerce(item.getType().equals(LAbilityMain.instance.gameManager.targetItem));
                else return CoerceJavaToLua.coerce(item.getType().toString().contains(targetItems));
            }
        });

        set("eliminatePlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isnil()) return CoerceJavaToLua.coerce(false);
                LAPlayer player = (LAPlayer) arg.checkuserdata(LAPlayer.class);

                LAbilityMain.instance.gameManager.EliminatePlayer(player);
                return NIL;
            }
        });

        set("endGame", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                boolean isGoodEnd = arg.isnil() || arg.checkboolean();

                LAbilityMain.instance.gameManager.OnGameEnd(isGoodEnd);
                return NIL;
            }
        });
    }
}
