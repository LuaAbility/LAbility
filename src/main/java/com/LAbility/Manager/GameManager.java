package com.LAbility.Manager;

import com.LAbility.Ability;
import com.LAbility.Event.GameEndEvent;
import com.LAbility.Event.PlayerEliminateEvent;
import com.LAbility.LAPlayer;
import com.LAbility.LATeam;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.List.BanIDList;
import com.LAbility.LuaUtility.List.PlayerList;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameManager {
    public boolean isGameReady = false;
    public boolean isGameStarted = false;
    public boolean isTestMode = false;
    public PlayerList<LAPlayer> players = new PlayerList<LAPlayer>();

    public BukkitTask passiveTask = null;

    public BanIDList<String> banAbilityIDList = new BanIDList<>();
    public ArrayList<String> banAbilityRankList = new ArrayList<>();
    public ArrayList<Integer> shuffledAbilityIndex = new ArrayList<Integer>();
    public int currentAbilityIndex = 0;
    public int currentRuleIndex = 0;

    public int abilityAmount = 1;
    public int abilityRerollCount = 1;
    public int defaultLife = 1;
    public boolean overlapAbility = false;
    public boolean raffleAbility = true;
    public boolean canCheckAbility = true;
    public double cooldownMultiply = 1;
    public Material targetItem = Material.IRON_INGOT;
    public String targetItemString = "철괴";
    public boolean overrideItem = false;
    public boolean skipYesOrNo = false;
    public boolean skipInformation = false;
    public Map<String, Object> variable = new HashMap<>();

    public ArrayList<ShapedRecipe> customRecipe = new ArrayList<>();

    public float maxHealth = 20;

    public Object getVariable(String key) {
        Object obj = variable.getOrDefault(key, null);
        if (!(obj == null)) {
            var data = obj.getClass().cast(obj);
            return data;
        }
        else return null;
    }

    public void setVariable(String key, Object value){
        if (isGameStarted) {
            if (variable.containsKey(key)) variable.replace(key, value);
            else addVariable(key, value);
        }
    }

    public void addVariable(String key, Object value) {
        if (isGameStarted) {
            if (!variable.containsKey(key)) variable.put(key, value);
            else variable.replace(key, value);
        }
    }

    public void setVariableOnReady(String key, Object value){
        if (variable.containsKey(key)) variable.replace(key, value);
        else addVariableOnReady(key, value);
    }

    public void addVariableOnReady(String key, Object value) {
        if (!variable.containsKey(key)) variable.put(key, value);
        else variable.replace(key, value);
    }

    public void removeVariable(String key) {
        if (variable.containsKey(key)) variable.remove(key);
    }

    public void ResetAll(){
        for (LAPlayer player : players){
            for (Ability ab : player.getAbility()) {
                ab.runResetFunc(player);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setDisplayName(ChatColor.RESET + p.getName());
            p.setPlayerListName(ChatColor.RESET + p.getName());
        }

        HashMap<String, Object> defaultValues = new HashMap<>();
        if (variable.containsKey("-spawn")) defaultValues.put("-spawn", variable.get("-spawn"));
        if (variable.containsKey("-item")) defaultValues.put("-item", variable.get("-item"));
        if (variable.containsKey("-equip")) defaultValues.put("-equip", variable.get("-equip"));
        if (variable.containsKey("-startSize")) defaultValues.put("-startSize", variable.get("-startSize"));
        if (variable.containsKey("-endSize")) defaultValues.put("-endSize", variable.get("-endSize"));
        if (variable.containsKey("-startReduct")) defaultValues.put("-startReduct", variable.get("-startReduct"));
        if (variable.containsKey("-duration")) defaultValues.put("-duration", variable.get("-duration"));
        if (variable.containsKey("-godMode")) defaultValues.put("-godMode", variable.get("-godMode"));

        LAbilityMain.instance.teamManager.clearTeam();
        players = new PlayerList<LAPlayer>();
        LAbilityMain.instance.assignAllPlayer();
        isGameStarted = false;
        isGameReady = false;
        isTestMode = false;
        currentAbilityIndex = 0;
        shuffledAbilityIndex = new ArrayList<Integer>();
        customRecipe = new ArrayList<>();
        StopPassive();
        variable = defaultValues;
        BlockManager.ResetData();
        LAbilityMain.instance.scheduleManager.ClearTimer();

        for (Iterator<KeyedBossBar> it = Bukkit.getServer().getBossBars(); it.hasNext(); ) {
            KeyedBossBar bb = it.next();
            if (bb != null && bb.getKey().toString().toLowerCase().contains("lability")) {
                bb.removeAll();
                bb.setVisible(false);
                Bukkit.getServer().removeBossBar(bb.getKey());
            }
        }

        LAbilityMain.plugin.getServer().getWorlds().get(0);
    }

    public void RunEvent(Event event) {
        if (isGameStarted){
            for (LAPlayer player : players){
                if (player.isSurvive && player.getAbility() != null) {
                    int size = player.getAbility().size();
                    for (int i = 0; i < player.getAbility().size(); i++) {
                        if (size != player.getAbility().size()) break;
                        player.getAbility().get(i).runAbilityFunc(player, event);
                    }
                }
            }

            if (LAbilityMain.instance.rules.size() > currentRuleIndex) LAbilityMain.instance.rules.get(currentRuleIndex).RunEvent(event);
        }
    }

    public void RunPassive() {
        if (isGameStarted) {
            passiveTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (LAbilityMain.instance.rules.size() > 0 && !isTestMode)
                        LAbilityMain.instance.rules.get(currentRuleIndex).runPassiveFunc();

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!players.contains(p)) {
                            p.setDisplayName(ChatColor.RESET + p.getName());
                            p.setPlayerListName(ChatColor.RESET + p.getName());
                        }
                    }

                    for (LAPlayer player : players) {
                        player.getPlayer().setScoreboard(TeamManager.scoreboard);
                        player.ShowActionBar();

                        if (player.getTeam() != null) {
                            player.getPlayer().setDisplayName(player.getTeam().color + player.getTeam().teamName + " " + ChatColor.RESET + player.getPlayer().getName());
                            player.getPlayer().setPlayerListName(player.getTeam().color + player.getTeam().teamName + " " + ChatColor.RESET + player.getPlayer().getName());
                        }
                        if (player.isSurvive && (player.getVariable("abilityLock") == null || player.getVariable("abilityLock").equals(false))) {
                            int size = player.getAbility().size();
                            for (int i = 0; i < player.getAbility().size(); i++) {
                                if (size != player.getAbility().size()) break;
                                player.getAbility().get(i).runPassiveFunc(player);
                            }
                        }
                    }
                }
            }.runTaskTimer(LAbilityMain.plugin, 0, 1);

            for (ShapedRecipe r : customRecipe){ if (Bukkit.getServer().getRecipe(r.getKey()) == null) Bukkit.getServer().addRecipe(r); }
        }
    }

    public void StopPassive() {
        if (passiveTask != null) passiveTask.cancel();
    }

    public void StopActive(LAPlayer player) {
         for (Ability ab : player.getAbility()) {
             ab.stopActive(player);
         }
    }

    public void AbilityShuffle(boolean resetShuffleIndex) {
        Random random = new Random();
        int size = 0;

        if (resetShuffleIndex) {
            size = LAbilityMain.instance.abilities.size();
            shuffledAbilityIndex.clear();
            int hiddenCount = 0;
            for (int i = 0; i < size; i++) {
                boolean isHIDDEN = false;
                for (String s : banAbilityIDList) {
                    if (LAbilityMain.instance.abilities.get(i).abilityID.toLowerCase().contains(s.toLowerCase())) {
                        isHIDDEN = true;
                        break;
                    }
                }
                for (String s : banAbilityRankList) {
                    if (LAbilityMain.instance.abilities.get(i).abilityRank.equalsIgnoreCase(s)) {
                        isHIDDEN = true;
                        break;
                    }
                }

                if (!isHIDDEN) shuffledAbilityIndex.add(i);
                else hiddenCount++;
            }
            size -= hiddenCount;
        }
        else {
            size = shuffledAbilityIndex.size();
        }

        if (size < 2) return;
        for (int i = 0 ; i < 10000; i++) {
            int randomIndex = random.nextInt(0, size);
            final int temp = shuffledAbilityIndex.get(0);
            shuffledAbilityIndex.set(0, shuffledAbilityIndex.get(randomIndex));
            shuffledAbilityIndex.set(randomIndex, temp);
        }
    }

    public void AssignAbility() {
        if (shuffledAbilityIndex.size() < 1) AbilityShuffle(true);
        for (LAPlayer player : players) {
            AssignAbility(player);
        }
    }

    public void AssignAbility(LAPlayer player) {
        for (int i = 0; i < abilityAmount; i++) {
            if (overlapAbility) {
                Random random = new Random();
                Ability temp;

                boolean isHIDDEN;
                do {
                    isHIDDEN = false;
                    temp = LAbilityMain.instance.abilities.get(random.nextInt(LAbilityMain.instance.abilities.size()));
                    for (String s : banAbilityIDList) {
                        if (LAbilityMain.instance.abilities.get(i).abilityID.toLowerCase().contains(s.toLowerCase())) {
                            isHIDDEN = true;
                            break;
                        }
                    }
                    for (String s : banAbilityRankList) {
                        if (LAbilityMain.instance.abilities.get(i).abilityRank.equalsIgnoreCase(s)) {
                            isHIDDEN = true;
                            break;
                        }
                    }
                } while (player.getAbility().contains(temp.abilityID) || isHIDDEN);

                Ability a = new Ability(temp);
                player.getAbility().add(a);
                a.InitScript();
            } else {
                if (shuffledAbilityIndex.size() < 1) AbilityShuffle(true);
                Ability a = new Ability(LAbilityMain.instance.abilities.get(shuffledAbilityIndex.get(0)));
                player.getAbility().add(a);
                a.InitScript();
                shuffledAbilityIndex.remove(0);
            }
        }

        player.getPlayer().sendMessage("\2472[\247aLAbility\2472] \247a" + "능력이 무작위 배정되었습니다.");
        player.getPlayer().sendMessage("\2472[\247aLAbility\2472] \247a" + "/la check " + (player.getAbility().size() - 1) + "로 능력을 확인해주세요.");
    }

    public void ResignAbility(LAPlayer player, Ability ability) {
        if (player.getAbility().contains(ability.abilityID)) {
            player.getAbility().remove(ability, player);

            boolean isHIDDEN = false;
            for (String s : banAbilityIDList) {
                if (ability.abilityID.toLowerCase().contains(s.toLowerCase())) {
                    isHIDDEN = true;
                    break;
                }
            }
            for (String s : banAbilityRankList) {
                if (ability.abilityRank.equalsIgnoreCase(s)) {
                    isHIDDEN = true;
                    break;
                }
            }

            if (!isHIDDEN) {
                shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(ability));
                AbilityShuffle(false);
            }
        }
    }

    public void ResignAbility(LAPlayer player) {
        if (player.getAbility().size() > 0) {
            for (Ability a : player.getAbility()) {
                boolean isHIDDEN = false;
                for (String s : banAbilityIDList) {
                    if (a.abilityID.toLowerCase().contains(s.toLowerCase())) {
                        isHIDDEN = true;
                        break;
                    }
                }
                for (String s : banAbilityRankList) {
                    if (a.abilityRank.equalsIgnoreCase(s)) {
                        isHIDDEN = true;
                        break;
                    }
                }

                if (!isHIDDEN) shuffledAbilityIndex.add(LAbilityMain.instance.abilities.indexOf(a));
            }
            AbilityShuffle(false);

            player.getAbility().clear(player);
        }
    }

    public void ResignAbility() {
        for (LAPlayer player : players){
            ResignAbility(player);
        }
    }

    public boolean IsAllAsigned(){
        for (LAPlayer player : players){
            if (player.isAssign > 0) return false;
        }
        return true;
    }

    public void EliminatePlayer(LAPlayer player){
        Bukkit.getPluginManager().callEvent(new PlayerEliminateEvent(player));

        playerAbilityList(player);
        for (Ability a : player.getAbility()) a.stopActive(player);
        player.isSurvive = false;
        player.getAbility().clear();
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.clearMessage();

        String abilityString = "tellraw " + player.getPlayer().getName() + " [\"\"," +
                "{\"text\":\"남은 플레이어의 능력을 확인하려면 \",\"color\":\"yellow\"}," +
                "{\"text\":\"이곳\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la list\"}," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"클릭 시 모든 플레이어의 능력을 확인합니다.\",\"color\":\"green\"}]}}," +
                "{\"text\":\"을 클릭하세요.\",\"color\":\"yellow\"}]";

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);

        CheckGameEnd();
    }

    public void CheckGameEnd() {
        if (!isGameStarted) return;

        Map<LATeam, ArrayList<LAPlayer>> teams = new HashMap<>();

        teams.put(null, new ArrayList<>());
        for (LATeam t : LAbilityMain.instance.teamManager.teams) teams.put(t, new ArrayList<>());
        for (LAPlayer p : getSurvivePlayer()) teams.get(p.getTeam()).add(p);

        int surviveTeams = teams.get(null).size();
        teams.remove(null);

        for (Map.Entry<LATeam, ArrayList<LAPlayer>> data : teams.entrySet()) {
            if (data.getValue().size() > 0) surviveTeams++;
        }

        if (surviveTeams == 1) {
            if (getSurvivePlayer().size() == 1 && getSurvivePlayer().get(0).getTeam() == null) {
                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임이 종료되었습니다.");
                Bukkit.broadcastMessage("\2476[\247eLAbility\2476] " + getSurvivePlayer().get(0).getPlayer().getName() + "\247e 우승!");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("\247e게임 종료!", "\2476" + getSurvivePlayer().get(0).getPlayer().getName() + "\247e 우승!", 10, 60, 10);
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 0.5f, 1f);
                }
                OnGameEnd(true);
                return;
            }

            for (Map.Entry<LATeam, ArrayList<LAPlayer>> data : teams.entrySet()) {
                if (data.getValue().size() > 0) {
                    StringBuilder teamMember = new StringBuilder();
                    ArrayList<LAPlayer> winner = LAbilityMain.instance.teamManager.getTeamMember(data.getKey(), true);

                    for (int i = 0; i < winner.size(); i++) {
                        teamMember.append(winner.get(i).getPlayer().getName());
                        if (i < (winner.size() - 1)) teamMember.append(", ");
                    }

                    Bukkit.broadcastMessage("\2476[\247eLAbility\2476] \247e게임이 종료되었습니다.");
                    Bukkit.broadcastMessage("\2476[\247eLAbility\2476] " + data.getKey().color + data.getKey().teamName + "\247e 팀 \2477(" + teamMember + ") \247e우승!");

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("\247e게임 종료!", data.getKey().color + data.getKey().teamName + " 팀 우승!", 10, 60, 10);
                        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 0.5f, 1f);
                    }
                }
            }

            OnGameEnd(true);
        }
    }

    public void OnGameEnd(boolean isGoodEnd){
        if (isGameReady) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(players, isGoodEnd));

            for (ShapedRecipe r : customRecipe) {  Bukkit.getServer().removeRecipe(r.getKey()); }
            if (isGoodEnd) {
                final ArrayList<LAPlayer> survive = getSurvivePlayer();
                final int[] i = {0};
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        for (LAPlayer lap : survive) summonFirework(lap.getPlayer());
                        if (i[0]++ > 3) {
                            Bukkit.broadcastMessage("\2476LAbility\247e를 사용해 주셔서 감사합니다!");
                            Bukkit.broadcastMessage("\247e플러그인 개선을 위해 현재 설문을 진행 중입니다. 작성해주시면, 더 좋은 플러그인을 만드는데 도움됩니다 :)");
                            Bukkit.broadcastMessage("\247e설문조사 링크 \2476( https://forms.gle/VjWJXKMCYAmbNBrg9 \2476 )");
                            Bukkit.broadcastMessage("\247e개발자 디스코드 : MINUTE#4438");
                            cancel();
                        }
                    }
                }.runTaskTimer(LAbilityMain.plugin, 0, 40);
            }

            if (getSurvivePlayer().size() > 0) {
                for (Entity e : getSurvivePlayer().get(0).getPlayer().getWorld().getEntities()) {
                    if (e.getType().equals(EntityType.PRIMED_TNT)) e.remove();
                    if (e.getType().equals(EntityType.ARROW)) e.remove();
                    if (e.getType().equals(EntityType.ZOMBIE)) e.remove();
                    if (e.getType().equals(EntityType.STRAY)) e.remove();
                    if (e.getType().equals(EntityType.WITHER_SKELETON)) e.remove();
                    if (e.getType().equals(EntityType.FIREBALL)) e.remove();
                    if (e.getType().equals(EntityType.DRAGON_FIREBALL)) e.remove();
                    if (e.getType().equals(EntityType.SMALL_FIREBALL)) e.remove();
                    if (e.getType().equals(EntityType.WOLF)) e.remove();
                    if (e.getType().equals(EntityType.ARMOR_STAND)) e.remove();
                }
            }
            for (LAPlayer lap : getSurvivePlayer()) playerAbilityList(lap);
            if (LAbilityMain.instance.rules.size() > 0) LAbilityMain.instance.rules.get(currentRuleIndex).runResetFunc();


            LAbilityMain.instance.scheduleManager.ClearTimer();
            LAbilityMain.instance.gameManager.ResetAll();
            LAbilityMain.instance.getServer().getScheduler().cancelTasks(LAbilityMain.plugin);
        }
    }

    public PlayerList<LAPlayer> getSurvivePlayer() {
        PlayerList<LAPlayer> survivePlayer = new PlayerList<LAPlayer>();
        for (LAPlayer lap : players){
            if (lap.isSurvive) survivePlayer.add(lap);
        }
        return survivePlayer;
    }

    public void ShowAllAbility(CommandSender sender){
        if (sender instanceof Player) {
            sender.sendMessage("\2476-------[\247eAbility List\2476]-------");
            for (LAPlayer lap : this.players) {
                if (lap.isSurvive) {
                    String abilityString = "tellraw " + sender.getName() + " [\"\", {\"text\":\"" + lap.getPlayer().getName() + "\"}, {\"text\":\" : \",\"color\":\"yellow\"}, ";

                    int index = 0;
                    for (Ability a : lap.getAbility()) {
                        abilityString += "{\"text\":\"" + a.abilityName + "\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la ability " + a.abilityID + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"" + a.abilityName + " \",\"color\":\"aqua\"},{\"text\":\"능력을 확인하려면 클릭하세요.\",\"color\":\"green\"}]}}";
                        if (index++ < (lap.getAbility().size() - 1)) {
                            abilityString += ",{\"text\":\", \",\"color\":\"green\"},";
                        }
                    }
                    if (index == 0) abilityString += "{\"text\":\"없음\",\"color\":\"red\"}]";
                    else abilityString += "]";

                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);
                }
            }
        }
    }

    public void ShowAbility(CommandSender sender, ArrayList<LAPlayer> targetList){
        if (sender instanceof Player) {
            sender.sendMessage("\2476-------[\247eAbility List\2476]-------");
            for (LAPlayer lap : targetList) {
                if (lap.isSurvive) {
                    String abilityString = "tellraw " + sender.getName() + " [\"\", {\"text\":\"" + lap.getPlayer().getName() + "\"}, {\"text\":\" : \",\"color\":\"yellow\"}, ";

                    int index = 0;
                    for (Ability a : lap.getAbility()) {
                        abilityString += "{\"text\":\"" + a.abilityName + "\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la ability " + a.abilityID + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"" + a.abilityName + " \",\"color\":\"aqua\"},{\"text\":\"능력을 확인하려면 클릭하세요.\",\"color\":\"green\"}]}}";
                        if (index++ < (lap.getAbility().size() - 1)) {
                            abilityString += ",{\"text\":\", \",\"color\":\"green\"},";
                        }
                    }
                    if (index == 0) abilityString += "{\"text\":\"없음\",\"color\":\"red\"}]";
                    else abilityString += "]";

                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);
                }
            }
        }
    }

    public void playerAbilityList(LAPlayer player){
        String abilityString = "tellraw @a [\"\"," +
                "{\"text\":\"[\",\"color\":\"gold\"}," +
                "{\"text\":\"LAbility\",\"color\":\"yellow\"}," +
                "{\"text\":\"] " + player.getPlayer().getName() + "\",\"color\":\"gold\"}," +
                "{\"text\":\" 님은 \",\"color\":\"yellow\"},";

        int index = 0;
        for (Ability a : player.getAbility()) {
            abilityString += "{\"text\":\"" + a.abilityName + "\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/la ability " + a.abilityID + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"" + a.abilityName + " \",\"color\":\"aqua\"},{\"text\":\"능력을 확인하려면 클릭하세요.\",\"color\":\"green\"}]}},";
            if (index++ < (player.getAbility().size() - 1)) {
                abilityString += "{\"text\":\", \",\"color\":\"green\"},";
            }
        }
        if (index == 0) abilityString += "{\"text\":\"능력이 없었습니다.\",\"color\":\"red\"}]";
        else abilityString += "{\"text\":\" 능력이었습니다.\",\"color\":\"green\"}]";

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), abilityString);

        player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        player.getPlayer().setGravity(true);
        player.getPlayer().setWalkSpeed(0.2f);
        if (player.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || player.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) player.getPlayer().setFlying(false);
    }

    public void summonFirework(Player player){
        final Firework f = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fm = f.getFireworkMeta();

        fm.addEffect(FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .with(FireworkEffect.Type.STAR)
                .with(FireworkEffect.Type.BALL)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.AQUA)
                .withColor(Color.YELLOW)
                .withColor(Color.RED)
                .withColor(Color.WHITE)
                .build());

        fm.setPower(0);
        f.setFireworkMeta(fm);
    }
}
