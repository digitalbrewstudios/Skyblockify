package io.github.eveeifyeve.skyblockify

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.apache.logging.log4j.LogManager

@Mod(modid = "skyblockify")
class ModInit {

  private val logger = LogManager.getLogger("Skyblockify");

	// A Cheesy easter egg 👀
	private static final String[] INIT_RESPONSES = {
        "hello stranger.",
        "hello skyblock no life, here to no life again.",
        // TODO: Add more responses here
  };

  init {
    logger.info("Hello ")
  }

  @EventHandler
  fun onInitialization(event: FMLInitializationEvent) {
    logger.info("Initalized")
    MinecraftForge.EVENT_BUS.register(this)
  }
}

