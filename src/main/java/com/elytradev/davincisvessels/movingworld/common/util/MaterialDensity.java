package com.elytradev.davincisvessels.movingworld.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

import java.util.HashMap;
import java.util.Map;

public class MaterialDensity {
    public static final float WATER_DENSITY = 1.000F;
    public static final float DEFAULT_DENSITY = 0.34f;

    private static Map<Material, Float> materialDensityMap = new HashMap<>();
    private static Map<String, Float> blockDensityMap = new HashMap<>();

    public static void addDensity(Material mat, float dens) {
        materialDensityMap.put(mat, dens);
    }

    public static void addDensity(Block block, float dens) {
        blockDensityMap.put(Block.REGISTRY.getNameForObject(block).toString(), dens);
    }

    @SuppressWarnings("unlikely-arg-type")
	public static float getDensity(IBlockState state) {
        if (state == null) return DEFAULT_DENSITY;
        Float f = blockDensityMap.get(Block.REGISTRY.getNameForObject(state.getBlock()));
        if (f != null) return f;
        return getDensity(state.getMaterial());
    }

    public static float getDensity(Material mat) {
        Float f = materialDensityMap.get(mat);
        if (f != null) return f;
        return DEFAULT_DENSITY;
    }
}
