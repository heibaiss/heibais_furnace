package com.heibai.furnace;

import com.heibai.furnace.config.FurnaceConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Heibai's Furnace
 * Mod ID: heibais_furnace
 * 
 * 让我的世界原版熔炉能一次性烧完当前熔炉里面的所有东西。
 */
@Mod(HeibaisFurnace.MOD_ID)
public class HeibaisFurnace {
    public static final String MOD_ID = "heibais_furnace";
    public static final String MOD_NAME = "Heibai's Furnace";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public HeibaisFurnace() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册通用配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FurnaceConfig.SPEC, "heibais_furnace-common.toml");

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("{} initialized successfully!", MOD_NAME);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup complete.", MOD_NAME);
    }
}
