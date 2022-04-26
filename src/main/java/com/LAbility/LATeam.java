package com.LAbility;

import com.LAbility.Manager.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class LATeam {
    public ChatColor color;
    public String teamName;
    public boolean canTeamAttack = false;
    public Team scoreboardTeam;

    public LATeam(ChatColor color, String teamName, boolean canTeamAttack) {
        this.color = color;
        this.teamName = teamName;
        this.canTeamAttack = canTeamAttack;

        scoreboardTeam = TeamManager.scoreboard.registerNewTeam(teamName);
        scoreboardTeam.setColor(color);
        scoreboardTeam.setAllowFriendlyFire(canTeamAttack);
    }
}
