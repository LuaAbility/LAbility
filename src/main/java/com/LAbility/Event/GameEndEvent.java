package com.LAbility.Event;

import com.LAbility.LAPlayer;
import com.LAbility.LuaUtility.List.PlayerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {
    public static HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlerlist;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

    PlayerList<LAPlayer> players;
    boolean isSuccessfullyEnd;

    public GameEndEvent(PlayerList<LAPlayer> players, boolean isSuccessfullyEnd){
        this.players = (PlayerList<LAPlayer>) players.clone();
        this.isSuccessfullyEnd = isSuccessfullyEnd;
    }

    public Player getWinner() {
        PlayerList<LAPlayer> tempPlayer = new PlayerList<>();
        for (LAPlayer p : players){
            if (p.isSurvive) tempPlayer.add(p);
        }
        if (tempPlayer.size() > 1) return null;
        else return tempPlayer.get(0).getPlayer();
    }

    public boolean isWinningEnd() {
        return isSuccessfullyEnd;
    }
}
