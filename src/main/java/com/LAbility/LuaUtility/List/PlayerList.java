package com.LAbility.LuaUtility.List;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerList<E extends LAPlayer> extends ArrayList<LAPlayer> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof LAPlayer){
            return super.contains(o);
        }

        if (o instanceof Player player) {
            for (LAPlayer pl : this) {
                if (pl.getPlayer().equals(player)) return true;
            }
        }

        if (o instanceof String player) {
            for (LAPlayer pl : this) {
                if (pl.getPlayer().getName().equals(player)) return true;
            }
        }
        return false;
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof LAPlayer) return super.indexOf(o);

        if (o instanceof Player aID) {
            for (LAPlayer a : this) {
                if (a.getPlayer().equals(aID)){
                    return super.indexOf(a);
                }
            }
        }

        if (o instanceof String aID) {
            for (LAPlayer a : this) {
                if (a.getPlayer().getName().equals(aID)){
                    return super.indexOf(a);
                }
            }
        }
        return -1;
    }

    public LAPlayer get(Object o) {
        if (o instanceof Integer) return super.get((int)o);

        if (o instanceof Player aID) {
            for (LAPlayer a : this) {
                if (a.getPlayer().equals(aID)){
                    return a;
                }
            }
        }

        if (o instanceof String aID) {
            for (LAPlayer a : this) {
                if (a.getPlayer().getName().equals(aID)){
                    return a;
                }
            }
        }

        return null;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof LAPlayer){
            super.remove(o);
            return true;
        }

        if (o instanceof Player player) {
            for (LAPlayer pl : this) {
                if (pl.getPlayer().equals(player)) {
                    super.remove(pl);
                    return true;
                }
            }
        }

        if (o instanceof String player) {
            for (LAPlayer pl : this) {
                if (pl.getPlayer().getName().equals(player)) {
                    super.remove(pl);
                    return true;
                }
            }
        }
        return false;
    }
}
