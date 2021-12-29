package com.LAbility;

import com.LAbility.LuaUtility.AbilityList;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LAPlayer {
    Player player;
    AbilityList<Ability> ability = new AbilityList<>();
    ArrayList<String> variable = new ArrayList<String>();
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

    public ArrayList<String> getVariable() {
        return variable;
    }

    public void addVariable(String string) {
        variable.add(string);
    }

    public void removeVariable(String string) {
        variable.remove(string);
    }

    public void removeVariable(int index) {
        variable.remove(index);
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
