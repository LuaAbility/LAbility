package com.LAbility.Manager;

import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.List.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class EventManager implements Listener {
    private static Map<String, BukkitTask> playerList = new HashMap<>();
    public static int enableDisconnectOutTick = 600;

    @EventHandler ()
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a") && p.getName().equals("One_Minute_")) {
            LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247eLAbility의 제작자, \247bMINUTE. (One_Minute_)\247e님이 입장했습니다!");
        }

        if (enableDisconnectOutTick >= 0 && playerList.containsKey(p.getName())){
            p.sendMessage("\2476[\247eLAbility\2476] \247e돌아오신 것을 환영합니다!");
            p.sendMessage("\2476[\247eLAbility\2476] \247e게임을 계속 진행해주세요.");
            if (playerList.get(p.getName()) != null) playerList.get(p.getName()).cancel();
            playerList.remove(p.getName());
        }

        int index = LAbilityMain.instance.gameManager.players.indexOf(p.getName());
        if (index >= 0) LAbilityMain.instance.gameManager.players.get(index).setPlayer(p);

        if (!LAbilityMain.instance.gameManager.isGameReady) {
            if (index < 0) LAbilityMain.instance.gameManager.players.add(new LAPlayer(p));
        }
        //else if (enableDisconnectOutTick >= 0 && LAbilityMain.instance.dataPacks.size() > 0 && LAbilityMain.instance.useResourcePack) {
        //    try {
        //        String url = LAbilityMain.instance.webServer.getWebIp() + p.getUniqueId();
        //        p.setResourcePack(url, (byte[]) null, false);
        //    }
        //    catch (Exception e){
        //        Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c리소스팩 오류!");
        //        Bukkit.getConsoleSender().sendMessage(e.getMessage());
        //    }
        //}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void checkPlayerTeam(EntityDamageByEntityEvent event) {
        Entity damagee = event.getEntity();
        Entity damager = event.getDamager();

        if (damagee instanceof Player) {
            if (damager instanceof Projectile proj) {
                if (proj.getShooter() instanceof Player player) damager = player;
                else return;
            }

            LAPlayer lap1 = LAbilityMain.instance.gameManager.players.get(damagee);
            LAPlayer lap2 = LAbilityMain.instance.gameManager.players.get(damager);

            if ((lap1 != null && lap2 != null)) {
                if (lap1.getTeam() == null) return;
                if (lap2.getTeam() == null) return;
                if (lap2.getTeam().canTeamAttack) return;

                if (lap1.getTeam().equals(lap2.getTeam())) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent event) {
        if (LAbilityMain.instance.autoRespawn) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().spigot().respawn();
                }
            }.runTaskLater(LAbilityMain.plugin, 60);
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        playerList.remove(event.getPlayer());

        if (enableDisconnectOutTick < 0) return;
        if (!LAbilityMain.instance.gameManager.players.contains(p)) return;
        if (!LAbilityMain.instance.gameManager.isGameReady) LAbilityMain.instance.gameManager.players.remove(p);
        else if (!LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(p)).isSurvive) LAbilityMain.instance.gameManager.players.remove(p);
        else {
            if (enableDisconnectOutTick == 0) out(p);
            else {
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() { out(p); }
                }.runTaskLater(LAbilityMain.plugin, enableDisconnectOutTick);
                playerList.put(p.getName(), task);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onChat(AsyncPlayerChatEvent event){
        if (!event.getMessage().startsWith("!")) return;

        String targetMsg = event.getMessage().substring(1);
        while (targetMsg.startsWith(" ")) targetMsg = targetMsg.substring(1);

        Player p = event.getPlayer();
        if (!LAbilityMain.instance.gameManager.players.contains(p)) return;

        LAPlayer lap = LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(p));
        PlayerList<LAPlayer> teams = LAbilityMain.instance.teamManager.getMyTeam(lap, true);

        if (teams.size() > 1) {
            event.setCancelled(true);
            String msg = "<" + lap.getTeam().color + p.getName() + " (팀 채팅)" + ChatColor.RESET + "> " + targetMsg;

            for (LAPlayer tm : teams) tm.getPlayer().sendMessage(msg);
        }
    }

    private static void out(Player p){
        if (LAbilityMain.instance.gameManager.isGameReady) {
            LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247e" + p.getName() + "님은 게임 중 장기 미접속으로 인해 탈락처리되었습니다.");
            LAbilityMain.instance.gameManager.EliminatePlayer(LAbilityMain.instance.gameManager.players.get(LAbilityMain.instance.gameManager.players.indexOf(p)));
            playerList.remove(p.getName());
        }
    }
}
