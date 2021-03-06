package com.LAbility;

import com.LAbility.LuaUtility.List.AbilityList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LAPlayer {
    Player player;
    AbilityList<Ability> ability = new AbilityList<>();
    Map<String, Object> variable = new HashMap<>();
    public boolean isAssign = false;
    public boolean isSurvive = true;

    public LAPlayer(Player p){
        player = p;
    }

    public void setPlayer(Player p) { player = p; }

    public Player getPlayer() {
        return player;
    }

    public AbilityList<Ability> getAbility() {
        return ability;
    }

    public Map<String, Object> getVariableMap(){ return variable; }

    public Object getVariable(String key) {
        Object obj = variable.getOrDefault(key, null);
        if (!(obj == null)) {
            var data = obj.getClass().cast(obj);
            return data;
        }
        else return null;
    }

    public void setVariable(String key, Object value){
        if (variable.containsKey(key)) variable.replace(key, value);
        else addVariable(key, value);
    }

    public void addVariable(String key, Object value) {
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

    public void CheckAbility(CommandSender pl, int index) {
        if (ability.size() < 1) {
            pl.sendMessage("\2474[\247cLAbility\2474] \247c?????? ????????? ????????????.");
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
