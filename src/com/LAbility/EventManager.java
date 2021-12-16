package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        if (!LAbilityMain.instance.gameManager.isGameReady) {
            Player p = event.getPlayer();
            if (LAbilityMain.instance.gameManager.players.contains(p)) return;
            LAbilityMain.instance.gameManager.players.add(new LAPlayer(p));

            if ((p.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a") || p.getUniqueId().toString().equals("e9943c23-71ca-3c13-8fbe-37bb88c0f864")) && p.getName().equals("One_Minute_")) {
                LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247eLAbility의 제작자, \247bMINUTE. (One_Minute_)\247e님이 입장했습니다!");
            }
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();

        if (!LAbilityMain.instance.gameManager.isGameReady) LAbilityMain.instance.gameManager.players.remove(p);
        else {
            LAbilityMain.instance.getServer().getScheduler().runTaskLater(LAbilityMain.plugin, new Runnable() {
                @Override
                public void run() {
                    if (!LAbilityMain.instance.getServer().getOnlinePlayers().contains(p)) {
                        LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247e" + p.getName() + "님은 게임 중 장기 미접속으로 인해 탈락처리되었습니다.");
                        LAbilityMain.instance.gameManager.players.remove(p);
                        if (LAbilityMain.instance.gameManager.players.size() == 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e" + LAbilityMain.instance.gameManager.players.get(0).getPlayer().getName() + "님이 우승하셨습니다!");
                            ScheduleManager.ClearTimer();
                            LAbilityMain.instance.gameManager.ResetAll();
                            Bukkit.getScheduler().cancelTasks(LAbilityMain.plugin);
                        } else if (LAbilityMain.instance.gameManager.players.size() < 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e우승자가 없습니다.");
                            ScheduleManager.ClearTimer();
                            LAbilityMain.instance.gameManager.ResetAll();
                            Bukkit.getScheduler().cancelTasks(LAbilityMain.plugin);
                        }
                    }
                }
            }, 6000);
        }
    }
}
