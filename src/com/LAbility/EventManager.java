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
