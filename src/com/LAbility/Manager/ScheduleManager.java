package com.LAbility.Manager;

import com.LAbility.Event.GameStartEvent;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ScheduleManager {
    static BukkitTask Prepare_Scheduler;
    static int time_Main = 0, time_Prepare = 0;

    public void ClearTimer(){
        if (Prepare_Scheduler != null) Prepare_Scheduler.cancel();
        time_Main = 0; time_Prepare = 0;
    }

    public void PrepareTimer() {
        int Delay = 40;

        if (LAbilityMain.instance.gameManager.skipInformation) {
            LAbilityMain.instance.gameManager.isGameReady = true;
            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = true;
            MainTimer();
        }

        else {
            Prepare_Scheduler = new BukkitRunnable() {
                public void run() {
                    switch (time_Prepare) {
                        case 0:
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임을 시작합니다.");
                            break;
                        case 1:
                            Bukkit.broadcastMessage("\2476-------[\247eGame Settings\2476]-------");
                            Bukkit.broadcastMessage("\247b적용된 룰 \247f: \247a" + LAbilityMain.instance.rules.get(LAbilityMain.instance.gameManager.currentRuleIndex).ruleName);
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
                            LAbilityMain.instance.gameManager.AbilityShuffle(true);
                            LAbilityMain.instance.gameManager.AssignAbility();
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 추첨이 완료되었습니다.");
                            if ((!LAbilityMain.instance.gameManager.overlapAbility && ((LAbilityMain.instance.gameManager.abilityAmount * LAbilityMain.instance.gameManager.players.size()) == LAbilityMain.instance.abilities.size())) ||
                                    (LAbilityMain.instance.gameManager.overlapAbility && LAbilityMain.instance.gameManager.abilityAmount == LAbilityMain.instance.abilities.size()) ||
                                    LAbilityMain.instance.gameManager.skipYesOrNo) {
                                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e/la check로 능력을 확인해주세요.");
                                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 설정으로 인해 능력 변경은 진행되지 않습니다.");
                                for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = true;
                                time_Prepare = 26;
                            } else {
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
                            if (!LAbilityMain.instance.gameManager.IsAllAsigned()) {
                                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력이 결정되지 않은 플레이어가 있습니다. 능력을 결정해주세요.");
                                for (LAPlayer lap : LAbilityMain.instance.gameManager.players)
                                    if (!lap.isAssign) {
                                        lap.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e능력 결정이 완료되지 않았습니다.");
                                        lap.getPlayer().sendMessage("\2476[\247eLAbility\2476] \247e/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
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
                        case 33:
                            MainTimer();
                    }
                    time_Prepare++;
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, Delay);
        }
    }

    public void MainTimer() {
        Bukkit.getPluginManager().callEvent(new GameStartEvent());
        if (!LAbilityMain.instance.gameManager.skipInformation) Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 시작!");
        LAbilityMain.instance.gameManager.isGameStarted = true;
        for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
            lap.isSurvive = true;
            lap.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lap.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            lap.getPlayer().setWalkSpeed(0.2f);
        }
        LAbilityMain.instance.gameManager.RunPassive();
    }
}
