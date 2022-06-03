package com.LAbility.Manager;

import com.LAbility.Event.GameStartEvent;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ScheduleManager {
    static BukkitTask Prepare_Scheduler, autoSkip;
    static int time_Skip = 0, time_Prepare = 0;
    static KeyedBossBar skipBossBar = null;

    public void ClearTimer() {
        if (Prepare_Scheduler != null) Prepare_Scheduler.cancel();
        if (autoSkip != null) autoSkip.cancel();
        if (skipBossBar != null) {
            skipBossBar.removeAll();
            Bukkit.getServer().removeBossBar(skipBossBar.getKey());
            skipBossBar = null;
        }
        time_Skip = 0;
        time_Prepare = 0;

    }

    public void PrepareTimer() {
        int Delay = 20;

        if (LAbilityMain.instance.gameManager.skipInformation) {
            LAbilityMain.instance.gameManager.isGameReady = true;
            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
                lap.isAssign = 0;
                lap.lifeCount = LAbilityMain.instance.gameManager.defaultLife;
            }
            MainTimer();
        } else {
            Prepare_Scheduler = new BukkitRunnable() {
                public void run() {
                    switch (time_Prepare) {
                        case 0:
                            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
                                lap.isAssign = LAbilityMain.instance.gameManager.abilityRerollCount;
                                lap.lifeCount = LAbilityMain.instance.gameManager.defaultLife;
                            }
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
                                Bukkit.broadcastMessage("\247b통일된 아이템의 종류 \247f: \247a" + LAbilityMain.instance.gameManager.targetItemString);
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
                            if (LAbilityMain.instance.gameManager.skipYesOrNo) {
                                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 설정으로 인해 능력 변경은 진행되지 않습니다.");
                                for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = 0;
                                time_Prepare = 26;
                            } else {
                                if (LAbilityMain.instance.autoSkipTimer > 0) autoSkip();
                                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e채팅창의 선택 버튼 또는 \2476/la [yes/no] \247e명령어로 능력을 결정해주세요.");
                            }

                            for (LAPlayer lap : LAbilityMain.instance.gameManager.players)
                                lap.CheckAbility(lap.getPlayer(), -1);
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
                                    if (lap.isAssign > 0) {
                                        lap.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c능력 결정이 완료되지 않았습니다.");
                                        lap.getPlayer().sendMessage("\2474[\247cLAbility\2474] \247c/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
                                    }
                                time_Prepare = 3;
                            }
                            break;
                        case 24:
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e모든 플레이어가 능력 결정을 완료했습니다.");
                            if (autoSkip != null) autoSkip.cancel();
                            if (skipBossBar != null) {
                                skipBossBar.removeAll();
                                Bukkit.getServer().removeBossBar(skipBossBar.getKey());
                                skipBossBar = null;
                            }
                            break;
                        case 25:
                        case 26:
                            break;
                        case 27:
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e잠시 후, 게임을 시작합니다.");
                            LAbilityMain.instance.gameManager.isGameReady = true;
                            for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = 0;
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
        if (!LAbilityMain.instance.gameManager.skipInformation)
            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 시작!");
        LAbilityMain.instance.gameManager.isGameStarted = true;
        for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
            lap.isSurvive = true;
            lap.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(LAbilityMain.instance.gameManager.maxHealth);
            lap.getPlayer().setHealth(LAbilityMain.instance.gameManager.maxHealth);
            lap.getPlayer().setWalkSpeed(0.2f);

            if (LAbilityMain.instance.teamManager.getMyTeam(lap, false).size() > 1) {
                String abilityString = "tellraw " + lap.getPlayer().getName() + " [\"\"," +
                        "{\"text\":\"팀원의 능력을 확인하려면 \",\"color\":\"yellow\"}," +
                        "{\"text\":\"이곳\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la list def\"}," +
                        "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"클릭 시 팀원의 능력을 확인합니다.\",\"color\":\"green\"}]}}," +
                        "{\"text\":\"을 클릭하세요.\",\"color\":\"yellow\"}]";

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);
                lap.getPlayer().sendMessage("\2472[\247a!<할 말>\2472]\247a로 팀 채팅을 할 수 있습니다.");
            }
        }
        LAbilityMain.instance.gameManager.RunPassive();
    }

    public void autoSkip() {
        time_Skip = 0;
        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e원활한 진행을 위해, " + LAbilityMain.instance.autoSkipTimer + "초 뒤 능력을 강제로 확정합니다.");

        autoSkip = new BukkitRunnable() {
            @Override
            public void run() {
                ++time_Skip;

                if (skipBossBar == null) {
                    skipBossBar = Bukkit.getServer().createBossBar(new NamespacedKey(LAbilityMain.plugin, "skipBossBar"), "\2476[\247e능력 결정\2476]", BarColor.GREEN, BarStyle.SEGMENTED_20);
                    for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
                        skipBossBar.addPlayer(lap.getPlayer());
                    }
                }
                skipBossBar.setTitle("\2476[\247e능력 결정\2476] \247a스킵까지 \247e" + (LAbilityMain.instance.autoSkipTimer - time_Skip) + "초");
                skipBossBar.setProgress(time_Skip / (float) LAbilityMain.instance.autoSkipTimer);

                if (time_Skip >= LAbilityMain.instance.autoSkipTimer) {
                    for (LAPlayer lap : LAbilityMain.instance.gameManager.players) lap.isAssign = 0;
                    time_Prepare = 24;
                    Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력을 강제로 확정합니다.");
                }
            }
        }.runTaskTimer(LAbilityMain.plugin, 0, 20);
    }
}
