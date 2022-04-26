package com.LAbility;

import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.Manager.TeamManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LAPlayer {
    LATeam team;
    Player player;
    AbilityList<Ability> ability = new AbilityList<>();
    Map<String, Object> variable = new HashMap<>();
    Map<String, String> message = new HashMap<>();
    public boolean isAssign = false;
    public boolean isSurvive = true;
    public boolean canTarget = true;

    public LAPlayer(Player p){
        player = p;
    }

    public void setPlayer(Player p) { player = p; }

    public Player getPlayer() {
        return player;
    }

    public void setTeam(LATeam t) {
        if (team != null) team.scoreboardTeam.removeEntry(player.getName());
        if (t != null) t.scoreboardTeam.addEntry(player.getName());
        team = t;
    }

    public LATeam getTeam() {
        return team;
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
        if (LAbilityMain.instance.gameManager.isGameStarted) {
            if (variable.containsKey(key)) variable.replace(key, value);
            else addVariable(key, value);

            if (value instanceof Boolean bool)
                if (key.equals("abilityLock") && bool)
                    message.clear();
        }
    }

    public void addVariable(String key, Object value) {
        if (LAbilityMain.instance.gameManager.isGameStarted) {
            if (!variable.containsKey(key)) variable.put(key, value);
            else variable.replace(key, value);

            if (value instanceof Boolean bool)
                if (key.equals("abilityLock") && bool)
                    message.clear();
        }
    }

    public void removeVariable(String key) {
        if (variable.containsKey(key)) variable.remove(key);
    }

    public void setMessage(String key, String value){
        if (message.containsKey(key)) message.replace(key, value);
        else addMessage(key, value);
    }

    public void addMessage(String key, String value) {
        if (!message.containsKey(key)) message.put(key, value);
        else message.replace(key, value);
    }

    public void removeMessage(String key) {
        if (message.containsKey(key)) message.remove(key);
    }

    public void clearMessage() {
        message.clear();
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

    public void ShowActionBar() {
        int index = 0;
        String messageStr = "";

        for (Map.Entry s : message.entrySet()) {
            messageStr += s.getValue();
            if (index++ < (message.size() - 1)) messageStr += " \247r| ";
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messageStr));
    }

    public void ResignAbility() {
        LAbilityMain.instance.gameManager.ResignAbility(this, ability.get(ability.size() - 1));
        LAbilityMain.instance.gameManager.AssignAbility(this);
        isAssign = true;
    }
}
