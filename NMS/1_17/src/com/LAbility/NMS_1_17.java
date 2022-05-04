package com.LAbility;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class NMS_1_17 implements NMS {
    private final IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().getCustomRegistry().b(IRegistry.aO);
    private HashMap<Player, ArrayList<Block>> changedBlock = new HashMap<>();

    public BiomeBase getBiomeBase() {
        ResourceKey<BiomeBase> oldKey = ResourceKey.a(IRegistry.aO, new MinecraftKey("minecraft", "plains"));
        BiomeBase forestbiome = biomeRegistry.a(oldKey);
        return forestbiome;
    }

    @Override
    public BiomeColors getBiomeColors() {
        try {
            BiomeFog biomeFog = (BiomeFog) ReflectionUtil.getPrivateObject(getBiomeBase(), "q");
            assert biomeFog != null;
            return new BiomeColors()
                    .setGrassColor(ReflectionUtil.getPrivateOptionalInteger(biomeFog, "g"))
                    .setFoliageColor(ReflectionUtil.getPrivateOptionalInteger(biomeFog, "f"))
                    .setWaterColor(ReflectionUtil.getPrivateInteger(biomeFog, "c"))
                    .setWaterFogColor(ReflectionUtil.getPrivateInteger(biomeFog, "d"))
                    .setSkyColor(ReflectionUtil.getPrivateInteger(biomeFog, "e"))
                    .setFogColor(ReflectionUtil.getPrivateInteger(biomeFog, "b"));
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public BiomeBase cloneWithDifferentColors(BiomeBase biome, BiomeColors newColors) {
        ResourceKey<BiomeBase> customBiomeKey = ResourceKey.a(IRegistry.aO, new MinecraftKey("lability", String.valueOf(new Random().nextLong())));
        BiomeBase.a customBiomeBuilder = new BiomeBase.a();

        customBiomeBuilder.a(biome.t());
        customBiomeBuilder.a(biome.c());
        try {
            Field biomeSettingMobsField = BiomeBase.class.getDeclaredField("m");
            biomeSettingMobsField.setAccessible(true);
            BiomeSettingsMobs biomeSettingMobs = (BiomeSettingsMobs) biomeSettingMobsField.get(biome);
            customBiomeBuilder.a(biomeSettingMobs);

            Field biomeSettingGenField = BiomeBase.class.getDeclaredField("l");
            biomeSettingGenField.setAccessible(true);
            BiomeSettingsGeneration biomeSettingGen = (BiomeSettingsGeneration) biomeSettingGenField.get(biome);
            customBiomeBuilder.a(biomeSettingGen);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        customBiomeBuilder.a(0.2F);
        customBiomeBuilder.b(0.05F);
        customBiomeBuilder.c(0.7F);
        customBiomeBuilder.d(0.8F);
        customBiomeBuilder.a(BiomeBase.TemperatureModifier.a);

        BiomeFog.a customBiomeColors = new BiomeFog.a();
        customBiomeColors.a(BiomeFog.GrassColor.a);

        if (newColors.getGrassColor() != 0) {
            customBiomeColors.f(newColors.getGrassColor());
        }
        if (newColors.getFoliageColor() != 0) {
            customBiomeColors.e(newColors.getFoliageColor());
        }
        customBiomeColors.b(newColors.getWaterColor());
        customBiomeColors.c(newColors.getWaterFogColor());
        customBiomeColors.d(newColors.getSkyColor());
        customBiomeColors.a(newColors.getFogColor());

        customBiomeBuilder.a(customBiomeColors.a());
        BiomeBase customBiome = customBiomeBuilder.a();

        biomeRegistry.a(OptionalInt.of(63), customBiomeKey, customBiome, Lifecycle.stable());

        return customBiome;
    }

    @Override
    public void changeBiomeColor(Player player, Block[] blocks, BiomeColors colors) {
        BlockPosition blockPosition;
        WorldServer nmsWorld = null;
        ArrayList<net.minecraft.world.level.chunk.Chunk> chunkList = new ArrayList<>();

        if (!changedBlock.containsKey(player)) changedBlock.put(player, new ArrayList<>());

        for (Block block : blocks) {
            BiomeBase newBiome = cloneWithDifferentColors(getBiomeBase(), colors);

            blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

            net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.getChunkAtWorldCoords(blockPosition);
            if (chunk != null && chunk.getBiomeIndex() != null) {
                chunk.getBiomeIndex().setBiome(
                        blockPosition.getX() >> 2,
                        blockPosition.getY() >> 2,
                        blockPosition.getZ() >> 2,
                        newBiome);
                chunk.markDirty();
                if (!chunkList.contains(chunk)) chunkList.add(chunk);
            }
        }
    }

    @Override
    public void resetBiomeColor(Player player) {
        BlockPosition blockPosition;
        WorldServer nmsWorld = null;
        ArrayList<net.minecraft.world.level.chunk.Chunk> chunkList = new ArrayList<>();

        if (changedBlock.containsKey(player)) {
            for (Block block : changedBlock.get(player)) {
                BiomeBase newBiome = getBiomeBase();

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.getChunkAtWorldCoords(blockPosition);
                if (chunk != null && chunk.getBiomeIndex() != null) {
                    chunk.getBiomeIndex().setBiome(
                            blockPosition.getX() >> 2,
                            blockPosition.getY() >> 2,
                            blockPosition.getZ() >> 2,
                            newBiome);
                    chunk.markDirty();
                    if (!chunkList.contains(chunk)) chunkList.add(chunk);

                    for (Player pl : block.getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            if ((pl.getLocation().distance(block.getChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                                ((CraftPlayer) pl).getHandle().b.sendPacket(new PacketPlayOutMapChunk(chunk));
                            }
                        }
                    }
                }
            }
            changedBlock.get(player).clear();
        }
    }


    @Override
    public void resetBiomeColor() {
        BlockPosition blockPosition;
        WorldServer nmsWorld = null;
        ArrayList<net.minecraft.world.level.chunk.Chunk> chunkList = new ArrayList<>();

        for (Map.Entry<Player, ArrayList<Block>> entry : changedBlock.entrySet()) {
            for (Block block : entry.getValue()) {
                BiomeBase newBiome = getBiomeBase();

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.getChunkAtWorldCoords(blockPosition);
                if (chunk != null && chunk.getBiomeIndex() != null) {
                    chunk.getBiomeIndex().setBiome(
                            blockPosition.getX() >> 2,
                            blockPosition.getY() >> 2,
                            blockPosition.getZ() >> 2,
                            newBiome);
                    chunk.markDirty();
                    if (!chunkList.contains(chunk)) chunkList.add(chunk);

                    for (Player pl : chunk.getBukkitChunk().getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            if ((pl.getLocation().distance(chunk.getBukkitChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                                ((CraftPlayer) pl).getHandle().b.sendPacket(new PacketPlayOutMapChunk(chunk));
                            }
                        }
                    }
                }
            }

            entry.getValue().clear();
        }

        for (net.minecraft.world.level.chunk.Chunk chunk : chunkList) {
            for (Player pl : chunk.getBukkitChunk().getWorld().getPlayers()) {
                if (pl.isOnline()) {
                    if ((pl.getLocation().distance(chunk.getBukkitChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                        ((CraftPlayer) pl).getHandle().b.sendPacket(new PacketPlayOutMapChunk(chunk));
                    }
                }
            }
        }

        changedBlock.clear();
    }
}
