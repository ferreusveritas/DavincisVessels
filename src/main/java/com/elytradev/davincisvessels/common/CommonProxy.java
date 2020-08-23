package com.elytradev.davincisvessels.common;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.client.gui.DavincisVesselsGuiHandler;
import com.elytradev.davincisvessels.common.handler.CommonHookContainer;
import com.elytradev.davincisvessels.common.handler.CommonPlayerTicker;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public CommonPlayerTicker playerTicker;
    public CommonHookContainer hookContainer;

    public CommonHookContainer getHookContainer() {
        return new CommonHookContainer();
    }

    public void registerKeyHandlers(DavincisVesselsConfig cfg) {
    }

    public void registerEventHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(DavincisVesselsMod.INSTANCE, new DavincisVesselsGuiHandler());

        playerTicker = new CommonPlayerTicker();
        MinecraftForge.EVENT_BUS.register(playerTicker);
        MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
    }

    public void registerRenderers(LoaderState.ModState state) {
    }

    public World getWorld(int id) {
        return DimensionManager.getWorld(id);
    }
    
}
