package com.LAbility;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LAPlayer {
    Player player;
    ArrayList<Ability> ability = new ArrayList<>();

    public LAPlayer(Player p){
        player = p;
    }

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

    public void CheckAbility(Player player, int index) {
        if (ability.size() < 1) {
            player.sendMessage("\2474[\247cLAbility\2474] \247c현재 능력이 없습니다.");
            return;
        }
        if (index < 0){
            if (ability.size() == 1) {
                ability.get(0).ExplainAbility(player);
            }
            else {
                player.sendMessage("\2476-------[\247eAbility List\2476]-------");
                int i = 0;
                for (Ability a : ability) {
                    player.sendMessage("\2476" + (i++) + ". \247e" + a.abilityName);
                }
            }
        }
        else {
            if (index > ability.size()) index = ability.size() - 1;
            ability.get(index).ExplainAbility(player);
        }
    }
}
