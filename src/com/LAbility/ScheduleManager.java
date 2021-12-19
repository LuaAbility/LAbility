package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.luaj.vm2.LuaFunction;

import java.util.HashMap;
import java.util.Map;

public class ScheduleManager {
    static int time_Prepare = 0, Prepare_Scheduler = 0;
    static int time_Main = 0, Main_Scheduler = 0;
    static Map<LuaFunction, Long> timerFunc = new HashMap<>();
    static Map<LuaFunction, Long> loopTimerFunc = new HashMap<>();

    public static void ClearTimer(){
        Bukkit.getScheduler().cancelTask(Prepare_Scheduler);
        Bukkit.getScheduler().cancelTask(Main_Scheduler);
        time_Prepare = 0; Prepare_Scheduler = 0;
        time_Main = 0; Main_Scheduler = 0;
    }

    public static void PrepareTimer() {
        int Dealy = 40;

        Prepare_Scheduler = LAbilityMain.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.instance, new Runnable() {
            public void run() {
                switch (time_Prepare) {
                    case 0:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임을 시작합니다.");
                        break;
                    case 1:
                        Bukkit.broadcastMessage("\2476-------[\247eGame Settings\2476]-------");
                        Bukkit.broadcastMessage("\247b능력 추첨 여부 \247f: \247a" + LAbilityMain.instance.gameManager.raffleAbility);
                        if (LAbilityMain.instance.gameManager.raffleAbility) {
                            Bukkit.broadcastMessage("\247b능력 추첨 개수 \247f: \247a" + LAbilityMain.instance.gameManager.abilityAmount);
                            Bukkit.broadcastMessage("\247b능력 중복 여부 \247f: \247a" + LAbilityMain.instance.gameManager.overlapAbility);
                        }
                        Bukkit.broadcastMessage("\247b능력 시전 아이템 통일 \247f: \247a" + LAbilityMain.instance.gameManager.overrideItem);
                        if (LAbilityMain.instance.gameManager.overrideItem)
                            Bukkit.broadcastMessage("\247b통일된 아이템의 종류 \247f: \247a" + LAbilityMain.instance.gameManager.targetItem.toString());
                        Bukkit.broadcastMessage("\247b능력 쿨타임 배율 \247f: \247ax" + LAbilityMain.instance.gameManager.cooldownMultiply);
                        if (!LAbilityMain.instance.gameManager.raffleAbility) time_Prepare = 26;
                        break;
                    case 2:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 추첨을 시작합니다.");
                        break;
                    case 3:
                        LAbilityMain.instance.gameManager.isGameReady = true;
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 추첨이 완료되었습니다.");
                        LAbilityMain.instance.gameManager.AbilityShuffle(true);
                        LAbilityMain.instance.gameManager.AssignAbility();
                        if (!LAbilityMain.instance.gameManager.overlapAbility && ((LAbilityMain.instance.gameManager.abilityAmount * LAbilityMain.instance.gameManager.players.size()) == LAbilityMain.instance.abilities.size())) {
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 확인해주세요.");
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e플레이 인원이 너무 많아 능력 변경은 진행되지 않습니다.");
                            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = true;
                            time_Prepare = 26;
                        }
                        else {
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
                        }
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        if (LAbilityMain.instance.gameManager.IsAllAsigned()) time_Prepare = 23;
                        break;
                    case 23:
                        if (!LAbilityMain.instance.gameManager.IsAllAsigned()){
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력이 결정되지 않은 플레이어가 있습니다. 능력을 결정해주세요.");
                            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) if (!lap.isAssign) {
                                lap.player.sendMessage("\2476[\247eLAbility\2476] \247e능력 결정이 완료되지 않았습니다.");
                                lap.player.sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
                            }
                            time_Prepare = 3;
                        }
                        break;
                    case 24:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e모든 플레이어가 능력 결정을 완료했습니다.");
                        break;
                    case 25:
                    case 26:
                        break;
                    case 27:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e잠시 후, 게임을 시작합니다.");
                        break;
                    case 28:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e5");
                        break;
                    case 29:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e4");
                        break;
                    case 30:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \24763");
                        break;
                    case 31:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247c2");
                        break;
                    case 32:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \24741");
                        break;
                    default:
                        MainTimer();
                }
                time_Prepare++;
            }
        }, 0, Dealy);
    }

    public static void MainTimer() {
        int Dealy = 1;

        Main_Scheduler = LAbilityMain.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LAbilityMain.instance, new Runnable() {
            public void run() {
                if (time_Main == 0) {
                    Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 시작!");
                    LAbilityMain.instance.gameManager.isGameStarted = true;
                    for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
                        lap.isSurvive = true;
                        lap.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lap.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
                        lap.player.setWalkSpeed(0.2f);
                    }
                    LAbilityMain.instance.gameManager.RunAllPassive();
                }

                else {
                    for (Map.Entry<LuaFunction, Long> func : loopTimerFunc.entrySet()) {
                        if (time_Main % func.getValue() == 0) func.getKey().call();
                    }
                }

                for (Map.Entry<LuaFunction, Long> func : timerFunc.entrySet()) {
                    if (time_Main == func.getValue()) func.getKey().call();
                }
                time_Main++;
            }
        }, 0, Dealy);
    }
}
