package com.LAbility;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface NMS {
    BiomeColors getBiomeColors();
    void changeBiomeColor(Player player, Block[] blocks, BiomeColors colors);
    void resetBiomeColor(Player player);
    void resetBiomeColor();
}
