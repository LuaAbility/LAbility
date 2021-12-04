package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.Wrapper.LoggerWrapper;
import com.LAbility.LuaUtility.Wrapper.PluginWrapper;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class LuaAbilityLoader {

    public static ArrayList<Ability> LoadAllLuaAbilities(){
        ArrayList<Ability> luaAbilities = new ArrayList<Ability>();
        Globals globals = JsePlatform.standardGlobals();

        File dataFolder = new File(LAbilityMain.instance.getDataFolder() + "/Ability");
        File[] files = dataFolder.listFiles();
        int idCount = 0;
        if (!dataFolder.exists()){
            return luaAbilities;
        }
        for (File file : files) {
            if (file.isDirectory() /*&& !file.toString().toLowerCase().contains("examplefolder")*/) {
                int abilityID = 0;
                int abilityCooldown = 0;
                boolean isPassive = false;
                String abilityName = "";
                String abilityRank = "";
                String abilityDesc = "";
                LuaValue luaScript = null;

                File[] files2 = file.listFiles();
                for (File file2 : files2) {
                    if (file2.toString().toLowerCase().contains("main.lua")) {
                        luaScript = globals.loadfile(file2.toString());
                        globals.set("plugin", new PluginWrapper());
                        globals.set("logger", new LoggerWrapper(LAbilityMain.plugin));
                        // use a member as its internal threadpool needs to be shutdown upon disabling the plugin
                        LAbilityMain.instance.utilitiesWrapper = new UtilitiesWrapper(LAbilityMain.instance);
                        globals.set("util", LAbilityMain.instance.utilitiesWrapper);
                    }
                    else if (file2.toString().toLowerCase().contains("data.yml")) {
                        try {
                            Map<String, Object> abilityData = new Yaml().load(new FileReader(file2));
                            abilityID = idCount++;
                            abilityCooldown = (int)abilityData.get("cooldown");
                            isPassive = (boolean)abilityData.get("passive");
                            abilityName = abilityData.get("name").toString();
                            abilityRank = abilityData.get("rank").toString();
                            abilityDesc = abilityData.get("description").toString();
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

                if (!luaScript.equals(null) && !abilityName.equals("")) {
                    luaAbilities.add(new Ability(abilityID, abilityCooldown, isPassive, abilityName, abilityRank, abilityDesc, luaScript));
                }
            }
        }

        return luaAbilities;
    }
}

