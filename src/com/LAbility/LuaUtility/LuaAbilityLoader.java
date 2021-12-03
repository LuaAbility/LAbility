package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import com.LAbility.LAbilityMain;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;
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
        for (File file : files) {
            if (!file.toString().contains(".") && !file.toString().contains("exampleFolder")) {
                int abilityID = 0;
                String abilityName = "";
                String abilityRank = "";
                String abilityDesc = "";
                LuaValue luaScript = null;

                String[] files2 = file.list();
                for (String file2 : files2) {
                    if (file2.toLowerCase().contains("main.lua")) {
                        luaScript = globals.loadfile(file2);
                    }
                    else if (file2.toLowerCase().contains("data.yml")) {
                        try {
                            Map<String, Object> abilityData = new Yaml().load(new FileReader(file2));
                            abilityID = idCount++;
                            abilityName = abilityData.get("name").toString();
                            abilityRank = abilityData.get("rank").toString();
                            abilityDesc = abilityData.get("description").toString();
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

                if (luaScript != null && abilityName != "") {
                    luaAbilities.add(new Ability(abilityID, abilityName, abilityRank, abilityDesc, luaScript));
                }
            }
        }

        return luaAbilities;
    }
}

