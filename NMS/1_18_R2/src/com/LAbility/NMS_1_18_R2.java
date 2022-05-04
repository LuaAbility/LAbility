package com.LAbility;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
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
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class NMS_1_18_R2 implements NMS {
    private final IRegistryWritable<BiomeBase> biomeRegistry = (IRegistryWritable<BiomeBase>) IRegistryCustom.e(IRegistry.aP);
    private HashMap<Player, ArrayList<Block>> changedBlock = new HashMap<>();

    public BiomeBase getBiomeBase() {
        ResourceKey<BiomeBase> oldKey = ResourceKey.a(IRegistry.aP, new MinecraftKey("minecraft", "plains"));
        BiomeBase forestbiome = biomeRegistry.a(oldKey);
        return forestbiome;
    }

    @Override
    public BiomeColors getBiomeColors() {
        try {
            BiomeFog biomeFog = (BiomeFog) ReflectionUtil.getPrivateObject(getBiomeBase(), "n");
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
        ResourceKey<BiomeBase> customBiomeKey = ResourceKey.a(IRegistry.aP, new MinecraftKey("lability", "custombiome"));
        BiomeBase.a customBiomeBuilder = new BiomeBase.a();
        Holder<BiomeBase> holder = new Holder.a<>(biome);

        customBiomeBuilder.a(BiomeBase.a(holder));
        customBiomeBuilder.a(biome.c());
        try {
            Field biomeSettingMobsField = BiomeBase.class.getDeclaredField("l");
            biomeSettingMobsField.setAccessible(true);
            BiomeSettingsMobs biomeSettingMobs = (BiomeSettingsMobs) biomeSettingMobsField.get(biome);
            customBiomeBuilder.a(biomeSettingMobs);

            Field biomeSettingGenField = BiomeBase.class.getDeclaredField("k");
            biomeSettingGenField.setAccessible(true);
            BiomeSettingsGeneration biomeSettingGen = (BiomeSettingsGeneration) biomeSettingGenField.get(biome);
            customBiomeBuilder.a(biomeSettingGen);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        customBiomeBuilder.a(0.2F);
        customBiomeBuilder.b(0.05F);

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
        WorldServer nmsWorld;

        if (!changedBlock.containsKey(player)) changedBlock.put(player, new ArrayList<>());

        for (Block block : blocks) {
            BiomeBase newBiome = biomeRegistry.a(ResourceKey.a(IRegistry.aP, new MinecraftKey("lability", "custombiome")));

            if (newBiome == null) {
                newBiome = cloneWithDifferentColors(getBiomeBase(), colors);
            }

            Holder<BiomeBase> holder = new Holder.a<>(newBiome);

            blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

            net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
            if (chunk != null) {
                chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, holder);

                for (Player pl : chunk.getBukkitChunk().getWorld().getPlayers()) {
                    if (pl.isOnline()) {
                        if((pl.getLocation().distance(chunk.getBukkitChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                            ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.k().a(), null, null, false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resetBiomeColor(Player player) {
        BlockPosition blockPosition;
        WorldServer nmsWorld;

        if (changedBlock.containsKey(player)) {
            for (Block block : changedBlock.get(player)) {
                BiomeBase newBiome = getBiomeBase();
                Holder<BiomeBase> holder = new Holder.a<>(newBiome);

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
                if (chunk != null) {
                    chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, holder);

                    for (Player pl : chunk.getBukkitChunk().getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            if ((pl.getLocation().distance(chunk.getBukkitChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                                ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.k().a(), null, null, false));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resetBiomeColor() {
        BlockPosition blockPosition;
        WorldServer nmsWorld;

        for (Map.Entry<Player, ArrayList<Block>> entry : changedBlock.entrySet()) {
            for (Block block : entry.getValue()) {
                BiomeBase newBiome = getBiomeBase();
                Holder<BiomeBase> holder = new Holder.a<>(newBiome);

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
                if (chunk != null) {
                    chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, holder);

                    for (Player pl : chunk.getBukkitChunk().getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            if ((pl.getLocation().distance(chunk.getBukkitChunk().getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
                                ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.k().a(), null, null, false));
                            }
                        }
                    }
                }
            }
        }
    }
}
