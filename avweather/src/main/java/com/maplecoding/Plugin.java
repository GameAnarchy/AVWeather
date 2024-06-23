package com.maplecoding;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * avweather java plugin
 */
public class Plugin extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("avweather");

  public void onEnable() {
    LOGGER.info("AV Weather has been enabled");
  }

  public void onDisable() {
    LOGGER.info("AV Weather has been disabled");
  }
}
