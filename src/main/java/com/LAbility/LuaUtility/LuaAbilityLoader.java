package com.LAbility.LuaUtility;

import com.LAbility.Ability;
import com.LAbility.LARule;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.List.AbilityList;
import com.LAbility.LuaUtility.List.RuleList;
import com.LAbility.LuaUtility.Wrapper.GameWrapper;
import com.LAbility.LuaUtility.Wrapper.LoggerWrapper;
import com.LAbility.LuaUtility.Wrapper.PluginWrapper;
import com.LAbility.LuaUtility.Wrapper.UtilitiesWrapper;
import org.bukkit.Bukkit;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.mozilla.universalchardet.UniversalDetector;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

public class LuaAbilityLoader {
    public static AbilityList<Ability> LoadAllLuaAbilities(){
        AbilityList<Ability> luaAbilities = new AbilityList<Ability>();
        Globals globals = JsePlatform.standardGlobals();

        File dataFolder = new File(LAbilityMain.instance.getDataFolder() + "/Ability");
        File[] files = dataFolder.listFiles();
        int idCount = 0;
        if (!dataFolder.exists()){
            return luaAbilities;
        }
        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    String abilityID = "";
                    String abilityType = "";
                    String abilityName = "";
                    String abilityRank = "";
                    String abilityDesc = "";
                    String luaScriptLoc = "";
                    LuaValue luaScript = null;

                    File[] files2 = file.listFiles();
                    for (File file2 : files2) {
                        if (file2.toString().toLowerCase().contains("main.lua")) {
                            luaScriptLoc = file2.toString();
                        } else if (file2.toString().toLowerCase().contains("data.yml")) {
                            try {
                                String charsetStr = findFileEncoding(file2);
                                if (charsetStr == null) {
                                    Bukkit.getConsoleSender().sendMessage("\2474[\247cEasySoundQuiz\2474] \247c파일을 로드하는데 문제가 생겼습니다. (위치 : " + file2.toString() + ")");
                                    continue;
                                }
                                Charset charset = Charset.forName(charsetStr);
                                Map<String, Object> abilityData = new Yaml().load(new FileReader(file2, charset));
                                abilityID = abilityData.get("id").toString();
                                abilityType = abilityData.get("type").toString();
                                abilityName = abilityData.get("name").toString();
                                abilityRank = abilityData.get("rank").toString();
                                abilityDesc = abilityData.get("description").toString();
                            } catch (FileNotFoundException e) {
                                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247cdata.yml 파일을 불러올 수 없습니다. 해당 능력은 불러오지 않습니다.");
                            }
                        }
                    }

                    if (!luaScriptLoc.equals("") && !abilityName.equals("")) {
                        Ability a = new Ability(abilityID, abilityType, abilityName, abilityRank, abilityDesc, luaScriptLoc);
                        a.InitScript();
                        luaAbilities.add(a);
                    }
                }
            } catch (Exception e) {
                LAbilityMain.instance.hasError++;
                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c능력을 로드하는데 문제가 생겼습니다. 해당 능력은 로드하지 않습니다.");
                Bukkit.getConsoleSender().sendMessage(e.getMessage());
            }
        }

        luaAbilities.sort(new AbilityList.AbilityComparator());

        return luaAbilities;
    }

    public static RuleList<LARule> LoadLuaRules() {
        RuleList<LARule> luaRules = new RuleList<LARule>();
        Globals globals = JsePlatform.standardGlobals();

        File dataFolder = new File(LAbilityMain.instance.getDataFolder() + "/Rule");
        File[] files = dataFolder.listFiles();
        if (!dataFolder.exists()){
            return luaRules;
        }
        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    String ruleID = "";
                    String ruleName = "";
                    String ruleDescription = "";
                    String luaScriptLoc = "";
                    LuaValue luaScript = null;

                    File[] files2 = file.listFiles();
                    for (File file2 : files2) {
                        if (file2.toString().toLowerCase().contains("rule.lua")) {
                            luaScriptLoc = file2.toString();
                        } else if (file2.toString().toLowerCase().contains("data.yml")) {
                            try {
                                String charsetStr = findFileEncoding(file2);
                                if (charsetStr == null) {
                                    Bukkit.getConsoleSender().sendMessage("\2474[\247cEasySoundQuiz\2474] \247c파일을 로드하는데 문제가 생겼습니다. (위치 : " + file2.toString() + ")");
                                    continue;
                                }
                                Charset charset = Charset.forName(charsetStr);
                                Map<String, Object> ruleData = new Yaml().load(new FileReader(file2, charset));
                                ruleID = ruleData.get("id").toString();
                                ruleName = ruleData.get("name").toString();
                                ruleDescription = ruleData.get("description").toString();
                            } catch (FileNotFoundException e) {
                                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247cdata.yml 파일을 불러올 수 없습니다. 해당 룰은 불러오지 않습니다.");
                            }
                        }
                    }

                    if (!luaScriptLoc.equals("") && !ruleName.equals("")) {
                        LARule rule = new LARule(ruleID, ruleName, ruleDescription, luaScriptLoc);
                        luaRules.add(rule);
                    }
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("\2474[\247cLAbility\2474] \247c능력을 로드하는데 문제가 생겼습니다. 해당 룰은 로드하지 않습니다.");
                Bukkit.getConsoleSender().sendMessage(e.getMessage());
            }
        }

        luaRules.sort(new RuleList.RuleComparator());
        return luaRules;
    }

    public static boolean isClassPathValid(String classPath) {
        try {
            Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static Globals setGlobals(Globals globals){
        OneArgFunction oldRequire = (OneArgFunction) globals.get("require");
        globals.set("plugin", new PluginWrapper());
        globals.set("logger", new LoggerWrapper(LAbilityMain.plugin));
        LAbilityMain.instance.utilitiesWrapper = new UtilitiesWrapper(LAbilityMain.instance);
        globals.set("util", LAbilityMain.instance.utilitiesWrapper);
        LAbilityMain.instance.gameWrapper = new GameWrapper(LAbilityMain.instance);
        globals.set("game", LAbilityMain.instance.gameWrapper);
        globals.set("require", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                String path = luaValue.checkjstring();
                if (!path.endsWith(".lua"))
                    path += ".lua";

                path = path.replaceAll("\\.(?=[^.]*\\.)", "/");

                InputStream resource = LAbilityMain.plugin.getResource(path);

                if (resource == null) {
                    return oldRequire.call(luaValue);
                }

                try {
                    return globals.load(new InputStreamReader(resource, "UTF-8"), luaValue.checkjstring()).call();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return NIL;
            }
        });
        globals.set("import", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                try {
                    String path = luaValue.checkjstring();
                    if (path.startsWith("$"))
                        path = "org.bukkit" + path.substring(1);
                    return CoerceJavaToLua.coerce(Class.forName(path));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return NIL;
            }
        });
        globals.set("newInstance", new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs vargs) {
                String classPath = vargs.checkjstring(1);
                LuaValue args = vargs.optvalue(2, LuaValue.NIL);

                if (classPath.startsWith("$"))
                    classPath = "org.bukkit" + classPath.substring(1);

                if (!isClassPathValid(classPath)) {
                    LuaException classPathException = new LuaException("An invalid classpath \"" + classPath + "\" was provided to the \"newInstance\" method", 1);
                    throw classPathException;
                }

                LuaString classPathValue = LuaValue.valueOf(classPath);
                LuaValue newInstanceMethod = globals.get("luajava").get("newInstance");

                switch (args.type()) {
                    case LuaValue.TNIL:
                        return newInstanceMethod.invoke(classPathValue).checkvalue(1);
                    case LuaValue.TTABLE:
                        LuaTable argTable = args.checktable();
                        LuaValue[] varargArray = new LuaValue[argTable.length() + 1];
                        varargArray[0] = classPathValue;

                        for (int iKey = 1; iKey < varargArray.length; iKey++) {
                            varargArray[iKey] = argTable.get(iKey);
                        }

                        return newInstanceMethod.invoke(varargArray).checkvalue(1);
                    default:
                        LuaException exception = new LuaException("Second argument of newInstance " + "must be of type table, not " + args.typename(), 1);
                        throw exception;
                }
            }
        });
        return globals;
    }

    public static String findFileEncoding(File file) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new java.io.FileInputStream(file);

        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }
}

