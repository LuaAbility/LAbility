package com.LAbility.Event;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AbilityConfirmEvent extends Event implements Cancellable {
    public static HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlerlist;
    }

    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

    LAPlayer player;
    Ability ability;
    String funcID;

    public AbilityConfirmEvent(LAPlayer player, Ability ability, String funcID){
        this.player = player;
        this.ability = ability;
        this.funcID = funcID;
    }

    public LAPlayer getPlayer(){
        return player;
    }

    public Ability getAbility(){
        return ability;
    }

    public String getFunctionID(){
        return funcID;
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
