package com.LAbility.LuaUtility.Wrapper;

import com.LAbility.Ability;
import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import com.LAbility.LuaUtility.LuaException;
import com.LAbility.Manager.BlockManager;
import com.LAbility.Manager.GUIManager;
import com.LAbility.Manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class UtilitiesWrapper extends LuaTable {
    public UtilitiesWrapper(LAbilityMain plugin) {
        set("getDummyAbility", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return CoerceJavaToLua.coerce(new Ability("", "", "", "", "", "", ""));
            }
        });

        set("getTableFromList", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Object[] list = new Object[]{};

                if (arg.checkuserdata() instanceof Collection) {
                    list = ((Collection<?>) arg.touserdata()).toArray();
                } else if (arg.touserdata() instanceof Stream) {
                    list = ((Stream<?>) arg.touserdata()).toArray();
                } else if (arg.touserdata() instanceof ArrayList) {
                    list = ((ArrayList<?>) arg.touserdata()).toArray();
                } else if (!(arg.touserdata() instanceof Iterator)) {
                    throw new LuaException("util.tableFromList(obj) was passed something other than an instance of Collection or Stream.", 1);
                }

                LuaTable t = new LuaTable();
                if (arg.touserdata() instanceof Iterator){
                    Iterator<?> iter = (Iterator<?>) arg.touserdata(Iterator.class);
                    int i = 1;
                    while (iter.hasNext()) t.set(LuaValue.valueOf(i), CoerceJavaToLua.coerce(iter.next()));
                }
                else {
                    for (int i = 0; i < list.length; i++) {
                        t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
                    }
                }

                return t;
            }
        });

        set("getTableFromArray", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Object[] list = (Object[]) arg.touserdata();
                LuaTable t = new LuaTable();
                for (int i = 0; i < list.length; i++) {
                    t.set(LuaValue.valueOf(i + 1), CoerceJavaToLua.coerce(list[i]));
                }

                return t;
            }
        });

        set("getTableFromMap", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Map<?, ?> map;

                if (arg.checkuserdata() instanceof Map) {
                    map = (Map<?, ?>) arg.touserdata();
                } else {
                    throw new LuaException("util.tableFromMap(obj) was passed something other than a implementation of Map.", 1);
                }

                LuaTable t = new LuaTable();
                map.forEach((k, v) -> t.set(CoerceJavaToLua.coerce(k), CoerceJavaToLua.coerce(v)));

                return t;
            }
        });

        set("runLater", new TwoArgFunction() {
            // Delay is in milliseconds.
            @Override
            public LuaValue call(LuaValue function, LuaValue time) {
                var task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        function.checkfunction().call();
                    }
                }.runTaskLater(LAbilityMain.plugin, time.checklong());
                int taskID = task.getTaskId();
                return CoerceJavaToLua.coerce(taskID);
            }
        });

        set("cancelRunLater", new OneArgFunction() {
            // Delay is in milliseconds.
            @Override
            public LuaValue call(LuaValue id) {
                Bukkit.getScheduler().cancelTask(id.checkint());
                return NIL;
            }
        });

        set("getClass", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                try {
                    return CoerceJavaToLua.coerce(Class.forName(path.checkjstring()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return NIL;
            }
        });

        // Temporary method, fixed in v3
        set("cast", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue userdata, LuaValue clazz) {
                String className = clazz.checkjstring();
                Object obj = userdata.checkuserdata();

                if (className.startsWith("$"))
                    className = "org.bukkit" + className.substring(1);
                if (className.startsWith("@"))
                    className = "com.LAbility" + className.substring(1);

                try {
                    Class<?> caster = Class.forName(className);
                    return userdataOf(caster.cast(obj));
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Could not find class " + className);
                } catch (ClassCastException e) {
                    plugin.getLogger().warning("Provided userdata cannot be casted to " + className);
                } catch (LinkageError e) {
                    plugin.getLogger().warning("There was an unknown issue casting the object to " + className);
                    e.printStackTrace();
                }


                return NIL;
            }
        });

        set("hasClass", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue userdata, LuaValue clazz) {
                String className = clazz.checkjstring();
                Object obj = userdata.checkuserdata();

                if (className.startsWith("$"))
                    className = "org.bukkit" + className.substring(1);
                if (className.startsWith("@"))
                    className = "com.LAbility" + className.substring(1);

                try {
                    Class<?> caster = Class.forName(className);
                    if (caster.isInstance(obj) ) return CoerceJavaToLua.coerce(true);
                    else return CoerceJavaToLua.coerce(false);
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Could not find class " + className);
                } catch (ClassCastException e) {
                    plugin.getLogger().warning("Provided userdata cannot be casted to " + className);
                } catch (LinkageError e) {
                    plugin.getLogger().warning("There was an unknown issue casting the object to " + className);
                    e.printStackTrace();
                }

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("setBlockType", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue block, LuaValue mat) {
                Block targetBlock = (Block) block.checkuserdata(Block.class);
                Material material = (Material) mat.checkuserdata(Material.class);

                BlockManager.AddData(targetBlock);
                targetBlock.setType(material);

                return CoerceJavaToLua.coerce(false);
            }
        });

        set("random", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue num1, LuaValue num2) {
                Random random = new Random();

                if (num1.isnil() && num2.isnil()) return CoerceJavaToLua.coerce(random.nextDouble());
                else if (num2.isnil()) return CoerceJavaToLua.coerce(random.nextInt(1, num1.checkint() + 1));
                else return CoerceJavaToLua.coerce(random.nextInt(num1.checkint(), num2.checkint() + 1));
            }
        });

        set("executeCommand", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue targetCommand, LuaValue targetType, LuaValue targetPlayer) {
                String command = targetCommand.checkjstring();
                int type = targetType.isnil() ? 0 : targetType.checkint();
                Player player = targetPlayer.isnil() ? null : (Player) targetPlayer.checkuserdata(Player.class);

                if (player == null) type = 0;
                switch (type) {
                    case 1:
                        boolean isOP = player.isOp();
                        player.setOp(true);
                        boolean didWork = player.performCommand(command);
                        player.setOp(isOP);
                        return CoerceJavaToLua.coerce(didWork);
                    case 2:
                        return CoerceJavaToLua.coerce(player.performCommand(command));
                    default:
                        return CoerceJavaToLua.coerce(Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
                }
            }
        });

        set("getRealDamager", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entity) {
                Entity damager = entity.isnil() ? null : (Entity) entity.checkuserdata(Entity.class);
                if (damager instanceof Projectile proj){
                    ProjectileSource source = proj.getShooter();
                    if (source instanceof BlockProjectileSource) return NIL;
                    else damager = (Entity)source;
                }

                if (damager instanceof TNTPrimed tnt){
                    damager = tnt.getSource();
                }

                return CoerceJavaToLua.coerce(damager);
            }
        });

        set("getNMS", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (LAbilityMain.instance.nms == null){
                    Bukkit.getConsoleSender().sendMessage("\2478[\2477LAbility\2478] \2477NMS 미지원 버전이므로, 일부 기능이 작동하지 않습니다.");
                }
                return CoerceJavaToLua.coerce(LAbilityMain.instance.nms);
            }
        });

        set("createGUIMethod", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue click, LuaValue close) {
                GUIManager.GUIMethod method = new GUIManager.GUIMethod() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        click.checkfunction().invoke(CoerceJavaToLua.coerce(event));
                    }

                    @Override
                    public void onClose(InventoryCloseEvent event) {
                        close.checkfunction().invoke(CoerceJavaToLua.coerce(event));
                    }
                };

                return CoerceJavaToLua.coerce(method);
            }
        });

        set("addRecipe", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue click) {
                ShapedRecipe recipe = (ShapedRecipe) click.checkuserdata(ShapedRecipe.class);
                LAbilityMain.instance.gameManager.customRecipe.add(recipe);
                return NIL;
            }
        });

        set("setRecipeElement", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue _recipe, LuaValue _string, LuaValue _mat) {
                ShapedRecipe recipe = (ShapedRecipe) _recipe.checkuserdata(ShapedRecipe.class);
                String string = _string.toString();
                Material mat = (Material) _mat.checkuserdata(Material.class);

                recipe.setIngredient(string.charAt(0), mat);
                LAbilityMain.instance.gameManager.customRecipe.add(recipe);
                return NIL;
            }
        });
    }
}
