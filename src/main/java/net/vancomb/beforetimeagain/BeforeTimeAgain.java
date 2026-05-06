package net.vancomb.beforetimeagain;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.vancomb.beforetimeagain.creativetab.ModCreativeModeTabs;
import net.vancomb.beforetimeagain.entity.ModEntities;
import net.vancomb.beforetimeagain.item.ModItems;
import net.vancomb.beforetimeagain.particle.ModParticles;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/** MAIN MOD CLASS */
@Mod(BeforeTimeAgain.MOD_ID)
public class BeforeTimeAgain {

    // MOD ID must match mods.toml
    public static final String MOD_ID = "beforetimeagain";

    // Logger = prints info/errors to console for debugging
    public static final Logger LOGGER = LogUtils.getLogger();

    public BeforeTimeAgain(IEventBus modEventBus, ModContainer modContainer) {

        /* =============================
           SETUP / LIFECYCLE EVENTS
           ============================= */
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        /* =============================
           REGISTER MOD CONTENT
           ============================= */
        ModCreativeModeTabs.register(modEventBus); // creative tab
        ModItems.register(modEventBus);            // items
        ModEntities.register(modEventBus);         // entities (mobs)
        ModParticles.register(modEventBus);        // particles

        /* =============================
           EVENT BUS (global game events)
           ============================= */
        NeoForge.EVENT_BUS.register(this);

        /* =============================
           CONFIG FILE SETUP
           ============================= */
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /** Runs once during mod setup. Good for networking, registries, or early initialization.*/
    private void commonSetup(FMLCommonSetupEvent event) {
        // currently empty
    }

    /** Adds items to vanilla creative tabs.This is where you decide where your items show up in inventory.*/
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

        // Only modify the INGREDIENTS tab
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ZIRCON);
            event.accept(ModItems.RAW_ZIRCON);
        }
    }

    /** Server startup event (runs when you launch a dedicated server)*/
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // currently empty
    }
}
