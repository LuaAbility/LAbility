package com.LAbility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventManager implements Listener {
    private static Map<String, BukkitTask> playerList = new HashMap<>();
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (playerList.containsKey(p.getName())){
            p.sendMessage("\2476[\247eLAbility\2476] \247e돌아오신 것을 환영합니다!");
            p.sendMessage("\2476[\247eLAbility\2476] \247e게임을 계속 진행해주세요.");
            if (playerList.get(p.getName()) != null) playerList.get(p.getName()).cancel();
            playerList.remove(p.getName());
        }

        int index = LAbilityMain.instance.gameManager.players.indexOf(p.getName());
        if (index >= 0) LAbilityMain.instance.gameManager.players.get(index).player = p;

        if (!LAbilityMain.instance.gameManager.isGameReady) {
            if (index < 0) LAbilityMain.instance.gameManager.players.add(new LAPlayer(p));

            if ((p.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a") || p.getUniqueId().toString().equals("e9943c23-71ca-3c13-8fbe-37bb88c0f864")) && p.getName().equals("One_Minute_")) {
                LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247eLAbility의 제작자, \247bMINUTE. (One_Minute_)\247e님이 입장했습니다!");
            }
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        playerList.remove(event.getPlayer());

        if (!LAbilityMain.instance.gameManager.players.contains(p)) return;
        if (!LAbilityMain.instance.gameManager.isGameReady) LAbilityMain.instance.gameManager.players.remove(p);
        else if (!LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(p)).isSurvive) LAbilityMain.instance.gameManager.players.remove(p);
        else {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (LAbilityMain.instance.gameManager.isGameReady) {
                        LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247e" + p.getName() + "님은 게임 중 장기 미접속으로 인해 탈락처리되었습니다.");
                        LAbilityMain.instance.gameManager.players.remove(p);
                        playerList.remove(p.getName());
                        if (LAbilityMain.instance.gameManager.players.size() == 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e" + LAbilityMain.instance.gameManager.players.get(0).getPlayer().getName() + "님이 우승하셨습니다!");
                            ScheduleManager.ClearTimer();
                            LAbilityMain.instance.gameManager.ResetAll();
                            LAbilityMain.instance.getServer().getScheduler().cancelTasks(LAbilityMain.plugin);
                        } else if (LAbilityMain.instance.gameManager.players.size() < 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e우승자가 없습니다.");
                            ScheduleManager.ClearTimer();
                            LAbilityMain.instance.gameManager.ResetAll();
                            LAbilityMain.instance.getServer().getScheduler().cancelTasks(LAbilityMain.plugin);
                        }
                    }
                }
            }.runTaskLater(LAbilityMain.plugin,600);

            playerList.put(p.getName(), task);
        }
    }
}
