package com.LAbility;

import org.bukkit.ChatColor;

public class LATeam {
    public ChatColor color;
    public String teamName;
    public boolean canTeamAttack = false;

    public LATeam(ChatColor color, String teamName, boolean canTeamAttack) {
        this.color = color;
        this.teamName = teamName;
        this.canTeamAttack = canTeamAttack;
    }
}
