package com.LAbility.Event;

import com.LAbility.LAPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTargetEvent extends Event implements Cancellable {
    public static HandlerList handlerlist = new HandlerList();
    public static HandlerList getHandlerList(){
        return handlerlist;
    }
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

    LAPlayer abilityPlayer;
    LAPlayer targetPlayer;

    public PlayerTargetEvent(LAPlayer aPlayer, LAPlayer tPlayer) {
        this.abilityPlayer = aPlayer;
        this.targetPlayer = tPlayer;
    }

    public LAPlayer getUsingPlayer() {
        return abilityPlayer;
    }
    public LAPlayer getTargetPlayer() {
        return targetPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
