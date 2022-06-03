package com.LAbility.Manager;

import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager  implements Listener {
    private static class ChangedBlockData {
        public Material blockType;
        public BlockData blockData;
        public BlockState blockState;

        public ChangedBlockData(Material type, BlockData data, BlockState state){
            blockType = type;
            if (data != null) blockData = data.clone();
            if (state != null) blockState = state;
            else blockData = null;
        }
    }
    private static Map<Location, ChangedBlockData> changedBlocks = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(BlockPlaceEvent event) {
        event.setCancelled(LAbilityMain.instance.cancelBreakOnReady && !LAbilityMain.instance.gameManager.isGameStarted);
        if (!event.isCancelled()) {
            Block block = event.getBlockPlaced();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(Material.AIR, null, block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockInteract(PlayerInteractEvent event) {
        if ((event.useInteractedBlock() == Event.Result.ALLOW || event.useInteractedBlock() == Event.Result.DEFAULT) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockBroken(BlockBreakEvent event) {
        event.setCancelled(LAbilityMain.instance.cancelBreakOnReady && !LAbilityMain.instance.gameManager.isGameStarted);

        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);


            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockBurnt(BlockBurnEvent event) {
        event.setCancelled(!LAbilityMain.instance.burntBlock);

        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockLand(EntityChangeBlockEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity() instanceof FallingBlock fallingBlock) {
                Block block = event.getBlock();
                if (!changedBlocks.containsKey(block.getLocation()) && block.getType() != fallingBlock.getBlockData().getMaterial()) {
                    ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                    changedBlocks.put(block.getLocation(), data);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockExplode(BlockExplodeEvent event) {
        if (!LAbilityMain.instance.explodeBlock) event.blockList().clear();

        if (!event.isCancelled()) {
            for (Block b : event.blockList()) {
                if (!changedBlocks.containsKey(b.getLocation())) {
                    ChangedBlockData data = new ChangedBlockData(b.getType(), b.getBlockData(), b.getState());
                    changedBlocks.put(b.getLocation(), data);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public static void onEntityExplode(EntityExplodeEvent event) {
        if (!LAbilityMain.instance.explodeBlock) event.blockList().clear();

        if (!event.isCancelled()) {
            for (Block b : event.blockList()) {
                if (!changedBlocks.containsKey(b.getLocation())) {
                    ChangedBlockData data = new ChangedBlockData(b.getType(), b.getBlockData(), b.getState());
                    changedBlocks.put(b.getLocation(), data);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(LAbilityMain.instance.cancelBreakOnReady && !LAbilityMain.instance.gameManager.isGameStarted);

        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(LAbilityMain.instance.cancelBreakOnReady && !LAbilityMain.instance.gameManager.isGameStarted);

        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCobbleStoneCreated(BlockFromToEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getToBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onTreeGrowCreated(StructureGrowEvent event) {
        if (!event.isCancelled()) {
            List<BlockState> blocks = event.getBlocks();
            for (BlockState b : blocks) {
                Bukkit.broadcastMessage(b.getLocation().toVector().toString());
                if (!changedBlocks.containsKey(b.getLocation())) {
                    ChangedBlockData data = new ChangedBlockData(Material.AIR, null, null);
                    changedBlocks.put(b.getLocation(), data);
                }
            }
        }
    }


    public static void ResetChangedBlock() {
        for (Map.Entry<Location, ChangedBlockData> data : changedBlocks.entrySet()) {
            Location loc = data.getKey();
            loc.getBlock().setType(data.getValue().blockType);
            if (data.getValue().blockData != null) {
                if (data.getValue().blockData instanceof Waterlogged) {
                    ((Waterlogged) data.getValue().blockData).setWaterlogged(false);
                }
                loc.getBlock().setBlockData(data.getValue().blockData);
            }
            if (data.getValue().blockState != null) {
                try {
                    BlockState blockState = loc.getBlock().getState();

                    if (loc.getBlock().getType().toString().contains("BANNER") && data.getValue().blockState instanceof Banner banner) {
                        ((Banner) blockState).setBaseColor(banner.getBaseColor());
                        ((Banner) blockState).setPatterns(banner.getPatterns());
                    } else if (loc.getBlock().getType().toString().contains("CAMP") && data.getValue().blockState instanceof Campfire campfire) {
                        for (int i = 0; i < campfire.getSize(); i++) {
                            ((Campfire) blockState).setCookTime(i, campfire.getCookTime(i));
                            ((Campfire) blockState).setCookTimeTotal(i, campfire.getCookTimeTotal(i));
                            ((Campfire) blockState).setItem(i, campfire.getItem(i));
                        }
                    } else if (loc.getBlock().getType().toString().contains("COMMAND") && data.getValue().blockState instanceof CommandBlock commandBlock) {
                        ((CommandBlock) blockState).setCommand(commandBlock.getCommand());
                        ((CommandBlock) blockState).setName(commandBlock.getName());
                    } else if (loc.getBlock().getType().toString().contains("CHEST") && data.getValue().blockState instanceof Chest container) {
                        ((Container) blockState).getInventory().clear();
                        if (container.getInventory().getContents().length > 0) {
                            ItemStack[] items = container.getInventory().getContents().clone();
                            for (int i = 0; i < items.length; i++) {
                                if (items[i] == null) items[i] = new ItemStack(Material.AIR);
                                ((Container) blockState).getInventory().setItem(i, items[i]);
                            }
                        }

                        ((Container) blockState).getSnapshotInventory().clear();
                        if (container.getSnapshotInventory().getContents().length > 0) {
                            ItemStack[] items = container.getSnapshotInventory().getContents().clone();
                            for (int i = 0; i < items.length; i++) {
                                if (items[i] == null) items[i] = new ItemStack(Material.AIR);
                                ((Container) blockState).getSnapshotInventory().setItem(i, items[i]);
                            }
                        }
                    } else if (loc.getBlock().getType().toString().contains("SPAWNER") && data.getValue().blockState instanceof CreatureSpawner creatureSpawner) {
                        ((CreatureSpawner) blockState).setDelay(creatureSpawner.getDelay());
                        ((CreatureSpawner) blockState).setMaxNearbyEntities(creatureSpawner.getMaxNearbyEntities());
                        ((CreatureSpawner) blockState).setMaxSpawnDelay(creatureSpawner.getMaxSpawnDelay());
                        ((CreatureSpawner) blockState).setMinSpawnDelay(creatureSpawner.getMinSpawnDelay());
                        ((CreatureSpawner) blockState).setRequiredPlayerRange(creatureSpawner.getRequiredPlayerRange());
                        ((CreatureSpawner) blockState).setSpawnCount(creatureSpawner.getSpawnCount());
                        ((CreatureSpawner) blockState).setSpawnedType(creatureSpawner.getSpawnedType());
                        ((CreatureSpawner) blockState).setSpawnRange(creatureSpawner.getSpawnRange());
                    } else if (loc.getBlock().getType().toString().contains("SIGN") && data.getValue().blockState instanceof Sign sign) {
                        String[] lines = sign.getLines();
                        for (int i = 0; i < lines.length; i++) ((Sign) blockState).setLine(i, lines[i]);
                    } else if (data.getValue().blockState instanceof Skull skull && skull.hasOwner()) {
                        ((Skull) blockState).setOwnerProfile(skull.getOwnerProfile());
                    }

                    blockState.update(true);
                } catch (Exception e){

                }
            }
        }
        ResetData();
    }

    public static void AddData(Block block) {
        if (!changedBlocks.containsKey(block.getLocation())) {
            ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData(), block.getState());
            changedBlocks.put(block.getLocation(), data);
        }
    }
    public static void ResetData() { changedBlocks.clear(); }
}
