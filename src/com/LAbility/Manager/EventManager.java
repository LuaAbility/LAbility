package com.LAbility.Manager;

import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class EventManager implements Listener {
    private static Map<String, BukkitTask> playerList = new HashMap<>();
    @EventHandler ()
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (playerList.containsKey(p.getName())){
            p.sendMessage("\2476[\247eLAbility\2476] \247e돌아오신 것을 환영합니다!");
            p.sendMessage("\2476[\247eLAbility\2476] \247e게임을 계속 진행해주세요.");
            if (playerList.get(p.getName()) != null) playerList.get(p.getName()).cancel();
            playerList.remove(p.getName());
        }

        int index = LAbilityMain.instance.gameManager.players.indexOf(p.getName());
        if (index >= 0) LAbilityMain.instance.gameManager.players.get(index).setPlayer(p);

        if (!LAbilityMain.instance.gameManager.isGameReady) {
            if (index < 0) LAbilityMain.instance.gameManager.players.add(new LAPlayer(p));

            if (p.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a") && p.getName().equals("One_Minute_")) {
                LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247eLAbility의 제작자, \247bMINUTE. (One_Minute_)\247e님이 입장했습니다!");
            }
        }
        else if (LAbilityMain.instance.dataPacks.size() > 0 && LAbilityMain.instance.useResourcePack) {
            try {
                String url = LAbilityMain.instance.webServer.getWebIp() + p.getUniqueId();
                p.setResourcePack(url, null, false);
            }
            catch (Exception e){
                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩 오류!");
                Bukkit.getConsoleSender().sendMessage(e.getMessage());
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
                        LAbilityMain.instance.gameManager.EliminatePlayer(LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(p)));
                        playerList.remove(p.getName());

                        if (LAbilityMain.instance.gameManager.getSurvivePlayer().size() == 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e" + LAbilityMain.instance.gameManager.getSurvivePlayer().get(0).getPlayer().getName() + "님이 우승하셨습니다!");
                            LAbilityMain.instance.gameManager.OnGameEnd(true);
                        } else if (LAbilityMain.instance.gameManager.getSurvivePlayer().size() < 1) {
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e게임이 종료되었습니다.");
                            LAbilityMain.instance.getServer().broadcastMessage("§6[§eLAbility§6] §e우승자가 없습니다.");
                            LAbilityMain.instance.gameManager.OnGameEnd(true);
                        }
                    }
                }
            }.runTaskLater(LAbilityMain.plugin,1200);
            playerList.put(p.getName(), task);
        }
    }
}
