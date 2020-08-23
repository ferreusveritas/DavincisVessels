package com.elytradev.davincisvessels.movingworld;

import java.io.File;

import com.elytradev.davincisvessels.movingworld.common.config.MainConfig;
import com.elytradev.davincisvessels.movingworld.common.network.MovingWorldNetworking;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MovingWorldLib {
	public static MovingWorldLib INSTANCE;

    private MainConfig localConfig;

    public MovingWorldLib() {
    	INSTANCE = this;
    }
    
    public void preInit(FMLPreInitializationEvent e) {
        File configFolder = new File(e.getModConfigurationDirectory(), "MovingWorld");
        File mConfigFile = new File(configFolder, "Main.cfg");
        localConfig = new MainConfig(new Configuration(mConfigFile));
        localConfig.loadAndSave();
    }

    public void init(FMLInitializationEvent e) {
        localConfig.postLoad();
        MovingWorldNetworking.setupNetwork();
        localConfig.getShared().assemblePriorityConfig.loadAndSaveInit();
    }

	public void postInit(FMLPostInitializationEvent e) {
		localConfig.getShared().assemblePriorityConfig.loadAndSavePostInit();
	}

    public MainConfig getLocalConfig() {
        return localConfig;
    }
    
}
