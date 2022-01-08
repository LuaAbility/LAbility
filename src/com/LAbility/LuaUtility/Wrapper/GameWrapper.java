package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.*;
import com.LAbility.LuaUtility.PlayerList;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaFunction;
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
                PlayerList<LAPlayer> survivePlayer = new PlayerList<LAPlayer>();
                for (LAPlayer lap : plugin.gameManager.players){
                    if (lap.isSurvive) survivePlayer.add(lap);
                }
                return CoerceJavaToLua.coerce(survivePlayer);
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
                else return CoerceJavaToLua.coerce(false);
            }
        });

        set("checkCooldown", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                String funcID = vargs.checkjstring(3);
                boolean showMessage = vargs.isnil(4) || vargs.checkboolean(4);

                for (Ability abilities : player.getAbility()) {
                    if (abilities.equals(ability)) {
                        return CoerceJavaToLua.coerce(abilities.CheckCooldown(player, funcID, showMessage));
                    }
                }
                return CoerceJavaToLua.coerce(false);
            }
        });

        set("changeAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                String abilityID = vargs.checkjstring(3);
                boolean deleteAll = vargs.checkboolean(4);

                Ability newAbility;
                int aindex = LAbilityMain.instance.abilities.indexOf(abilityID);
                if (aindex >= 0) newAbility = LAbilityMain.instance.abilities.get(aindex);
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
                return CoerceJavaToLua.coerce(false);
            }
        });

        set("removeAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                Ability ability = (Ability) vargs.checkuserdata(2, Ability.class);
                boolean deleteAll = vargs.checkboolean(3);

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
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                String abilityID = vargs.checkjstring(2);

                Ability newAbility;
                int aindex = LAbilityMain.instance.abilities.indexOf(abilityID);
                if (aindex >= 0) newAbility = LAbilityMain.instance.abilities.get(aindex);
                else return CoerceJavaToLua.coerce(false);


                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e자신의 능력이 추가되었습니다.");
                player.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 재 확인 해주세요.");

                player.getAbility().add(newAbility);

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("hasAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                LAPlayer player = (LAPlayer) vargs.checkuserdata(1, LAPlayer.class);
                String abilityID = vargs.checkjstring(2);

                return CoerceJavaToLua.coerce(player.hasAbility(abilityID));
            }
        });

        set("getPlayerAbility", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
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
                ItemStack item = (ItemStack) vargs.checkuserdata(1, ItemStack.class);
                String targetItems = vargs.checkjstring(2);

                if (LAbilityMain.instance.gameManager.overrideItem) return CoerceJavaToLua.coerce(item.getType().equals(LAbilityMain.instance.gameManager.targetItem));
                else return CoerceJavaToLua.coerce(item.getType().toString().contains(targetItems));
            }
        });

        set("eliminatePlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Player player = (Player) arg.checkuserdata(Player.class);
                int playerIndex = plugin.gameManager.players.indexOf(player.getName());
                if (playerIndex >= 0){
                    LAPlayer lap = plugin.gameManager.players.get(playerIndex);
                    for (Ability a : lap.getAbility()) a.stopActive(lap);
                    lap.isSurvive = false;
                    lap.getAbility().clear();
                    player.setGameMode(GameMode.SPECTATOR);
                }
                else {
                    Bukkit.getConsoleSender().sendMessage("Error!");
                }
                return NIL;
            }
        });

        set("endGame", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LAbilityMain.instance.gameManager.OnGameEnd();
                return NIL;
            }
        });

        set("setOnGameEndFunction", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaFunction func = arg.checkfunction();
                LAbilityMain.instance.gameManager.onGameEnd = func;
                return NIL;
            }
        });

        set("addGameVariable", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String key = vargs.checkjstring(1);
                String value = vargs.checkjstring(2);

                if (!LAbilityMain.instance.gameManager.variable.containsKey(key)) LAbilityMain.instance.gameManager.variable.put(key, value);
                else LAbilityMain.instance.gameManager.variable.replace(key, value);
                return NIL;
            }
        });

        set("getGameVariable", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String key = vargs.checkjstring(1);

                return CoerceJavaToLua.coerce(LAbilityMain.instance.gameManager.variable.getOrDefault(key, ""));
            }
        });

        set("setGameVariable", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String key = vargs.checkjstring(1);
                String value = vargs.checkjstring(2);

                if (LAbilityMain.instance.gameManager.variable.containsKey(key)) LAbilityMain.instance.gameManager.variable.replace(key, value);
                else LAbilityMain.instance.gameManager.variable.put(key, value);
                return NIL;
            }
        });

        set("removeGameVariable", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String key = vargs.checkjstring(1);
                if (LAbilityMain.instance.gameManager.variable.containsKey(key)) LAbilityMain.instance.gameManager.variable.remove(key);
                return NIL;
            }
        });

        set("resetGameVariable", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                LAbilityMain.instance.gameManager.variable.clear();
                return NIL;
            }
        });
    }
}
