package com.LAbility.Manager;

import com.LAbility.LAPlayer;
import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class BlockManager  implements Listener {
    private static class ChangedBlockData {
        public Material blockType;
        public BlockData blockData;

        public ChangedBlockData(Material type, BlockData data){
            blockType = type;
            if (data != null) blockData = data.clone();
            else blockData = null;
        }
    }
    private static Map<Location, ChangedBlockData> changedBlocks = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlockPlaced();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(Material.AIR, null);
                changedBlocks.put(block.getLocation(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockBroken(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (!changedBlocks.containsKey(block.getLocation())) {
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData());
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
                ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData());
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
                    ChangedBlockData data = new ChangedBlockData(block.getType(), block.getBlockData());
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
                    ChangedBlockData data = new ChangedBlockData(b.getType(), b.getBlockData());
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
                    ChangedBlockData data = new ChangedBlockData(b.getType(), b.getBlockData());
                    changedBlocks.put(b.getLocation(), data);
                }
            }
        }
    }

    public static void ResetChangedBlock() {
        for (Map.Entry<Location, ChangedBlockData> data : changedBlocks.entrySet()) {
            Location loc = data.getKey();
            loc.getBlock().setType(data.getValue().blockType);
            if (data.getValue().blockData != null) loc.getBlock().setBlockData(data.getValue().blockData);
        }
        ResetData();
    }

    public static void ResetData() { changedBlocks.clear(); }
}
