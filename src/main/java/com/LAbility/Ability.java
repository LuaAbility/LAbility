package com.LAbility;

import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.List.FunctionList;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.LAbility.LuaUtility.LuaAbilityLoader.setGlobals;

public class Ability {
    public static class AbilityFunc{
        public String funcID;
        public Class<? extends Event> funcEvent;
        public int cooldown;
        public int currentTime;
        public BukkitTask currentTask = null;

        public AbilityFunc(String ID, Class<? extends Event> event, int cool){
            funcID = ID;
            funcEvent = event;
            cooldown = cool;
            currentTime = (int) (cool * LAbilityMain.instance.gameManager.cooldownMultiply);
        }

        public AbilityFunc(AbilityFunc af){
            funcID = af.funcID;
            funcEvent = af.funcEvent;
            cooldown = af.cooldown;
            currentTime = (int) (af.cooldown * LAbilityMain.instance.gameManager.cooldownMultiply);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ability ability = (Ability) o;
        return Objects.equals(abilityID, ability.abilityID) && Objects.equals(abilityType, ability.abilityType) && Objects.equals(abilityName, ability.abilityName) && Objects.equals(abilityRank, ability.abilityRank) && Objects.equals(abilityDesc, ability.abilityDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abilityID, abilityType, abilityName, abilityRank, abilityDesc);
    }

    public String abilityID;
    public String abilityType;
    public String abilityName;
    public String abilityRank;
    public String abilityDesc;
    public String abilityFlavor;
    public String luaScript;
    public FunctionList<AbilityFunc> abilityFunc = new FunctionList<>();

    Globals globals;
    LuaValue script;
    boolean syncScript = true;

    public Ability(String id, String type, String name, String rank, String desc, String flavor, String script) {
        abilityID = id;
        abilityType = type;
        abilityName = name;
        abilityRank = rank;
        abilityDesc = desc;
        abilityFlavor = flavor;
        luaScript = script;
    }

    public Ability(Ability a) {
        abilityID = a.abilityID;
        abilityType = a.abilityType;
        abilityName = a.abilityName;
        abilityRank = a.abilityRank;
        abilityDesc = a.abilityDesc;
        abilityFlavor = a.abilityFlavor;
        luaScript = a.luaScript;
        abilityFunc = new FunctionList<>();
        for (Ability.AbilityFunc af : a.abilityFunc) {
            abilityFunc.add(new AbilityFunc(af));
        }
    }

    public void sync(boolean sync){
        syncScript = sync;
    }

    public void InitScript(){
        globals = JsePlatform.standardGlobals();
        script = globals.loadfile(luaScript);
        globals = setGlobals(globals);
        script.call();

        globals.get("Init").call(CoerceJavaToLua.coerce(this));
    }

    public void runAbilityFunc(LAPlayer lap, Event event) {
        if (lap.getVariable("abilityLock") != null && lap.getVariable("abilityLock").equals(true)) return;
        if (abilityFunc.contains(event)) {
            for (Ability.AbilityFunc af : abilityFunc) {
                if ((af.funcEvent.isAssignableFrom(event.getClass()) || af.funcEvent.isInstance(event)) || af.funcEvent.equals(event.getClass())) {
                    //if (!syncScript) {
                    //    globals = JsePlatform.standardGlobals();
                    //    script = globals.loadfile(luaScript);
                    //    globals = setGlobals(globals);
                    //    script.call();
                    //}

                    LuaTable table = new LuaTable();
                    table.insert(1, CoerceJavaToLua.coerce(af.funcID));
                    table.insert(2, CoerceJavaToLua.coerce(event));
                    table.insert(3, CoerceJavaToLua.coerce(lap));
                    table.insert(4, CoerceJavaToLua.coerce(this));

                    if (event.getClass().isAssignableFrom(Cancellable.class)) {
                        Cancellable temp = (Cancellable)event;
                        if (temp.isCancelled()) return;
                    }

                    if (!globals.get("onEvent").isnil()) globals.get("onEvent").call(table);
                    return;
                }
            }
        }
    }

    public void runPassiveFunc(LAPlayer lap) {
        //if (!syncScript) {
        //    globals = JsePlatform.standardGlobals();
        //    script = globals.loadfile(luaScript);
        //    globals = setGlobals(globals);
        //    script.call();
        //}

        if (!globals.get("onTimer").isnil()) globals.get("onTimer").call(CoerceJavaToLua.coerce(lap), CoerceJavaToLua.coerce(this));
    }

    public boolean CheckCooldown(LAPlayer lap, String ID, boolean showMessage) {
        if (lap.getVariable("abilityLock") != null && lap.getVariable("abilityLock").equals(true)) return false;
        if (!abilityFunc.contains(ID)) return false;

        int index = abilityFunc.indexOf(ID);

        if ((abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) <= 0) return true;

        long maxCooldown = Math.round(abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply);

        if (abilityFunc.get(index).currentTime >= maxCooldown) {
            if (abilityFunc.get(index).currentTask != null) abilityFunc.get(index).currentTask.cancel();
            abilityFunc.get(index).currentTime = 0;
            abilityFunc.get(index).currentTask = new BukkitRunnable() {
                @Override
                public void run() {
                    abilityFunc.get(index).currentTime++;
                    double cooldown = ((abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) - abilityFunc.get(index).currentTime) / 20.0;

                    if (showMessage && maxCooldown >= 100) {
                        if (abilityFunc.get(index).currentTime == maxCooldown) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임이 종료되었습니다. (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2F);
                            return;
                        } else if (abilityFunc.get(index).currentTime == maxCooldown - 20) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b남은 시간 : 1초 (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2F);
                        } else if (abilityFunc.get(index).currentTime == maxCooldown - 40) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b남은 시간 : 2초 (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2F);
                        } else if (abilityFunc.get(index).currentTime == maxCooldown - 60) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b남은 시간 : 3초 (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2F);
                        } else if (abilityFunc.get(index).currentTime == maxCooldown - 80) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b남은 시간 : 4초 (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2F);
                        } else if (abilityFunc.get(index).currentTime == maxCooldown - 100) {
                            lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b남은 시간 : 5초 (" + abilityFunc.get(index).funcID + ")");
                            lap.player.playSound(lap.player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2F);
                        }

                        if (abilityFunc.get(index).currentTime < maxCooldown) lap.addMessage(abilityID + ID, "\247b" + ID + " (쿨타임) \247f: \247a" + (int) Math.ceil(cooldown) + "초");
                        else lap.removeMessage(abilityID + ID);
                    }
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, 1);
            return true;
        }

        double cooldown = ((abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) - abilityFunc.get(index).currentTime) / 20.0;
        if (showMessage) lap.player.sendMessage("\2471[\247b" + abilityName + "\2471] \247b쿨타임 입니다. (" + cooldown + "초 / " + abilityFunc.get(index).funcID + ")" );
        return false;
    }

    public void runResetFunc(LAPlayer lap) {
        //if (!syncScript) {
        //    globals = JsePlatform.standardGlobals();
        //    script = globals.loadfile(luaScript);
        //    globals = setGlobals(globals);
        //    script.call();
        //}

        if (!globals.get("Reset").isnil()) globals.get("Reset").call(CoerceJavaToLua.coerce(lap), CoerceJavaToLua.coerce(this));

        String[] splitID = abilityID.split("-");
        if (splitID.length >= 3) {
            String targetID = splitID[1] + splitID[2];

            ArrayList<String> removeIDList = new ArrayList<>();
            for (Map.Entry<String, Object> variable : lap.variable.entrySet()) {
                if (variable.getKey().contains(targetID)) {
                    removeIDList.add(variable.getKey());
                }
            }

            for (String i : removeIDList) {
                lap.variable.remove(i);
            }
        }

        lap.clearMessage();
    }

    public void resetCooldown() {
        for (Ability.AbilityFunc af : abilityFunc) resetCooldown(af.funcID);
    }

    public void resetCooldown(String ID) {
        if (!abilityFunc.contains(ID)) return;
        int index = abilityFunc.indexOf(ID);
        abilityFunc.get(index).currentTime = (int) (abilityFunc.get(index).cooldown * LAbilityMain.instance.gameManager.cooldownMultiply) + 100;
    }

    public void setTime(String ID, int reset) {
        if (!abilityFunc.contains(ID)) return;
        int index = abilityFunc.indexOf(ID);
        abilityFunc.get(index).currentTime = reset;
    }

    public void stopActive(LAPlayer lap) {
        for (Ability.AbilityFunc af : abilityFunc) {
            if (af.currentTask != null) af.currentTask.cancel();
        }

        runResetFunc(lap);
    }

    public void ExplainAbility(CommandSender player) {
        player.sendMessage("");
        if (LAbilityMain.instance.gameManager.canCheckAbility) {
            player.sendMessage("\2476===============[\247e " + abilityName + " \2476]===============");
            player.sendMessage("\247eID : \247a" + abilityID + " \247e/ \247eRank : \247a" + abilityRank + " \247e/ Type : \247a" + abilityType);
            player.sendMessage("\247f" + FilterAbilityDescription(abilityDesc));

            AbilityList<Ability> relatedAbility = new AbilityList<>();
            for (Ability a : LAbilityMain.instance.abilities) {
                if (a.abilityID.contains(abilityID + "-")) {
                    relatedAbility.add(a);
                }
            }

            if (relatedAbility.size() > 0) {
                String abilityString = "tellraw " + player.getName() + " [\"\"," +
                        "{\"text\":\"이 능력과 관련된 히든 능력 : \",\"color\":\"yellow\"},";

                int index = 0;
                for (Ability a : relatedAbility) {
                    abilityString += "{\"text\":\"" + a.abilityName + "\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la ability " + a.abilityID + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"" + a.abilityName + " \",\"color\":\"aqua\"},{\"text\":\"능력을 확인하려면 클릭하세요.\",\"color\":\"green\"}]}}";
                    if (index++ < (relatedAbility.size() - 1)) {
                        abilityString += ",{\"text\":\", \",\"color\":\"yellow\"},";
                    } else abilityString += "]";
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);
            }
        } else {
            player.sendMessage("\2476===============[\247e ??? \2476]===============");
            player.sendMessage("\247eID : \247a??? \247e/ \247eRank : \247a??? \247e/ Type : \247a???");
            player.sendMessage("\247f" + FilterAbilityDescription(abilityDesc));
        }
        player.sendMessage("");
    }

    public void ShowFlavor(CommandSender player){
        String flavor = abilityFlavor.length() > 0 ? abilityFlavor : "이 능력은 플레이버 텍스트가 없습니다.";
        if (LAbilityMain.instance.gameManager.canCheckAbility) player.sendMessage("\2477\247o\"" + flavor + "\"");
        else player.sendMessage("\2477\247o\"\247k" + FilterAbilityDescription(flavor) + "\247r\2477\247o\"");
        player.sendMessage("");
    }

    public String FilterAbilityDescription(String originTxt) {
        if (!LAbilityMain.instance.gameManager.overrideItem) return originTxt;

        String reg = "\247e(?:.*?)\247";

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(originTxt);

        while (matcher.find()) {
            String targetString = matcher.group(0);
            targetString = targetString.substring(2, targetString.length() - 1);
            originTxt = originTxt.replace(targetString, LAbilityMain.instance.gameManager.targetItemString);
        }

        if (!LAbilityMain.instance.gameManager.canCheckAbility){
            List<Character> list = new ArrayList<>();
            for(char c : originTxt.toCharArray()) {
                list.add(c);
            }
            Collections.shuffle(list);
            StringBuilder builder = new StringBuilder();
            for(char c : list) {
                builder.append(c);
            }
            originTxt = builder.toString();
        }

        return originTxt;
    }
}
