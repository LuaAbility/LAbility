package com.LAbility;

import com.LAbility.LuaUtility.AbilityList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LAPlayer {
    Player player;
    AbilityList<Ability> ability = new AbilityList<>();
    Map<String, String> variable = new HashMap<>();
    boolean isAssign = false;
    public boolean isSurvive = true;

    public LAPlayer(Player p){
        player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public AbilityList<Ability> getAbility() {
        return ability;
    }

    public String getVariable(String key) {
        return variable.getOrDefault(key, "");
    }

    public void setVariable(String key, String value){
        if (variable.containsKey(key)) variable.replace(key, value);
        else addVariable(key, value);
    }

    public void addVariable(String key, String value) {
        if (!variable.containsKey(key)) variable.put(key, value);
        else variable.replace(key, value);
    }

    public void removeVariable(String key) {
        if (variable.containsKey(key)) variable.remove(key);
    }

    public boolean hasAbility(Ability a) {
        for (Ability tempa : ability){
            if (a.abilityID.equals(tempa.abilityID)) return true;
        }
        return false;
    }

    public boolean hasAbility(String a) {
        for (Ability tempa : ability){
            if (a.equals(tempa.abilityID)) return true;
        }
        return false;
    }

    public void changeAbility(ArrayList<Ability> abilities) {
        LAPlayer lap = this;
        for (Ability a : ability)  a.stopActive(lap);

        ability.clear();
        new BukkitRunnable() {
            @Override
            public void run() { ability.addAll(abilities); }
        }.runTaskLater(LAbilityMain.plugin, 5);
    }

    public void CheckAbility(Player pl, int index) {
        if (ability.size() < 1) {
            pl.sendMessage("\2474[\247cLAbility\2474] \247c현재 능력이 없습니다.");
            return;
        }
        if (index < 0){
            if (ability.size() == 1) {
                ability.get(0).ExplainAbility(pl);
            }
            else {
                pl.sendMessage("\2476-------[\247eAbility List\2476]-------");
                int i = 0;
                for (Ability a : ability) {
                    pl.sendMessage("\2476" + (i++) + ". \247e" + a.abilityName);
                }
            }
        }
        else {
            if (index >= ability.size()) index = ability.size() - 1;
            ability.get(index).ExplainAbility(pl);
        }
    }

    public void ResignAbility() {
        LAbilityMain.instance.gameManager.ResignAbility(this, ability.get(ability.size() - 1));
        LAbilityMain.instance.gameManager.AssignAbility(this);
        isAssign = true;
    }
}
