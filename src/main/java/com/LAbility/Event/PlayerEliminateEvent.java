package com.LAbility.Event;

import com.LAbility.LAPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEliminateEvent extends Event {
    public static HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlerlist;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

    LAPlayer player;

    public PlayerEliminateEvent(LAPlayer player) {
        this.player = player;
    }

    public LAPlayer getPlayer() {
        return player;
    }
}
