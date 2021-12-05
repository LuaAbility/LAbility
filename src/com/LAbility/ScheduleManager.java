package com.LAbility;

import org.bukkit.Bukkit;

public class ScheduleManager {
    static int time_Prepare = 0, Prepare_Scheduler = 0;
    static int time_Main = 0, Main_Scheduler = 0;

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
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 추첨을 시작합니다.");
                        break;
                    case 2:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 추첨이 완료되었습니다.");
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
                        LAbilityMain.instance.gameManager.AbilityShuffle(true);
                        LAbilityMain.instance.gameManager.AssignAbility();
                        break;
                    case 3:
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
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e능력 결정이 완료되지 않았습니다.");
                            Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e/la check로 능력 확인 후, /la yes 또는 /la no를 통해 능력을 결정해주세요.");
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
                switch (time_Main){
                    case 0:
                        Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임 시작!");
                        LAbilityMain.instance.gameManager.RunAllPassive();
                        for (LAPlayer lap : LAbilityMain.instance.gameManager.players) {
                            lap.player.teleport(LAbilityMain.instance.getServer().getWorlds().get(0).getSpawnLocation());
                        }
                        break;
                    default:
                }
                time_Main++;
            }
        }, 0, Dealy);
    }
}
