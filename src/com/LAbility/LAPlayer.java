package com.LAbility;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LAPlayer {
    Player player;
    ArrayList<Ability> ability = new ArrayList<>();

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Ability> getAbility() {
        return ability;
    }

    public boolean hasAbility(Ability a) {
        for (Ability tempa : ability){
            if (a.equals(tempa)) return true;
        }
        return false;
    }
}
