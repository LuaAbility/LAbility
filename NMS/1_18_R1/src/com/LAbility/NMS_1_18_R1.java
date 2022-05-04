package com.LAbility;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
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
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class NMS_1_18_R1 implements NMS {
    private final IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().aV().b(IRegistry.aR);
    private HashMap<Player, ArrayList<Block>> changedBlock = new HashMap<>();
    private int count = 63;

    public BiomeBase getBiomeBase() {
        ResourceKey<BiomeBase> oldKey = ResourceKey.a(IRegistry.aR, new MinecraftKey("minecraft", "plains"));
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

    public BiomeBase cloneWithDifferentColors(BiomeBase biome, BiomeColors newColors, String key) {
        ResourceKey<BiomeBase> customBiomeKey = ResourceKey.a(IRegistry.aR, new MinecraftKey("lability", key));
        BiomeBase.a customBiomeBuilder = new BiomeBase.a();

        customBiomeBuilder.a(biome.r());
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

        biomeRegistry.a(customBiomeKey, customBiome, Lifecycle.stable());

        return customBiome;
    }

    @Override
    public void changeBiomeColor(Player player, Block[] blocks, BiomeColors colors) {
        BlockPosition blockPosition;
        WorldServer nmsWorld;
        String key = randomString();
        ArrayList<net.minecraft.world.level.chunk.Chunk> chunkList = new ArrayList<>();

        if (!changedBlock.containsKey(player)) changedBlock.put(player, new ArrayList<>());

        for (Block block : blocks) {
            changedBlock.get(player).add(block);
            BiomeBase newBiome = biomeRegistry.a(ResourceKey.a(IRegistry.aR, new MinecraftKey("lability", key)));

            if (newBiome == null) {
                cloneWithDifferentColors(getBiomeBase(), colors, key);
                newBiome = biomeRegistry.a(ResourceKey.a(IRegistry.aR, new MinecraftKey("lability", key)));
            }

            blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

            net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
            if (chunk != null) {
                chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, newBiome);
                for (Player pl : block.getWorld().getPlayers()) {
                    if (pl.isOnline()) {
                        ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.l_(), null, null, true));
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

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
                if (chunk != null) {
                    chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, newBiome);
                    for (Player pl : block.getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.l_(), null, null, false));
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
        WorldServer nmsWorld;

        for (Map.Entry<Player, ArrayList<Block>> entry : changedBlock.entrySet()) {
            for (Block block : entry.getValue()) {
                BiomeBase newBiome = getBiomeBase();

                blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

                net.minecraft.world.level.chunk.Chunk chunk = nmsWorld.l(blockPosition);
                if (chunk != null) {
                    chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, newBiome);
                    for (Player pl : block.getWorld().getPlayers()) {
                        if (pl.isOnline()) {
                            ((CraftPlayer) pl).getHandle().b.a(new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.l_(), null, null, false));
                        }
                    }
                }
            }

            entry.getValue().clear();
        }

        changedBlock.clear();
    }

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString.toLowerCase();
    }
}
