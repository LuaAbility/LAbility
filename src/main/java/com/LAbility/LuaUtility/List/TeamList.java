package com.LAbility.LuaUtility.List;

import com.LAbility.LAPlayer;
import com.LAbility.LATeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamList<E extends LATeam> extends ArrayList<LATeam> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof LATeam) return super.contains(o);

        if (o instanceof String name) {
            for (LATeam pl : this) {
                if (pl.teamName.equals(name)) return true;
            }
        }
        return false;
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof LATeam) return super.indexOf(o);

        if (o instanceof String name) {
            for (LATeam t : this) {
                if (t.teamName.equals(name)) return super.indexOf(t);
            }
        }
        return -1;
    }

    public LATeam get(Object o) {
        if (o instanceof Integer) return super.get((Integer) o);

        if (o instanceof String name) {
            for (LATeam t : this) {
                if (t.teamName.equals(name)) return t;
            }
        }

        return null;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof LATeam team){
            team.scoreboardTeam.unregister();
            super.remove(o);
            return true;
        }

        if (o instanceof String name) {
            for (LATeam t : this) {
                if (t.teamName.equals(name)) {
                    t.scoreboardTeam.unregister();
                    super.remove(t);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (LATeam t : this) t.scoreboardTeam.unregister();
        super.clear();
    }
}
