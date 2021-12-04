package com.LAbility.LuaUtility;

import com.LAbility.LAPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerList<E extends LAPlayer> extends ArrayList<LAPlayer> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof LAPlayer){
            super.contains(o);
            return true;
        }

        if (o instanceof Player) {
            Player player = (Player) o;
            for (LAPlayer pl : this) {
                if (pl.getPlayer().equals(player)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof LAPlayer){
            super.remove(o);
            return true;
        }

        if (o instanceof Player) {
            Player player = (Player) o;
            for (LAPlayer pl : this) {
                if (pl.getPlayer().equals(player)) {
                    super.remove(pl);
                    return true;
                }
            }
        }
        return false;
    }
}
