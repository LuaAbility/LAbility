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
        Player p = event.getPlayer();
        if (LAbilityMain.instance.gameManager.players.contains(p)) return;
        LAbilityMain.instance.gameManager.players.add(new LAPlayer(p));

        if (p.getUniqueId().toString().equals("5f828718-5da7-4819-a470-302fff83b37a") || p.getUniqueId().toString().equals("e9943c23-71ca-3c13-8fbe-37bb88c0f864") ) {
            LAbilityMain.instance.getServer().broadcastMessage("\2476[\247eLAbility\2476] \247eLAbility의 제작자, \247bMINUTE. (One_Minute_)\247e님이 입장했습니다!");
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        if (LAbilityMain.instance.gameManager.players.contains(p)){
            LAbilityMain.instance.gameManager.players.remove(p);
        }
    }
}
