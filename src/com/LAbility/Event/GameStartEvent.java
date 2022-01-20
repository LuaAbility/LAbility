package com.LAbility.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
    public static HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlerlist;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

    public GameStartEvent(){ }
}
