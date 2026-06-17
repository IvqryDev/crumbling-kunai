package net.ivqrydev.crumbling_kunai;

import net.ivqrydev.crumbling_kunai.config.ModConfig;
import net.ivqrydev.crumbling_kunai.sound.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CrumblingKunai.MOD_ID)
public class CrumblingKunai {
    public static final String MOD_ID = "crumbling_kunai";

    public CrumblingKunai(IEventBus modEventBus, ModContainer container) {
        ModSounds.register(modEventBus);
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
    }
}