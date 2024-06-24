package com.maplecoding.avweather;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.maplecoding.avweather.Handlers.ConfigHandler;
import com.maplecoding.avweather.Listeners.WeatherListener;
import com.maplecoding.avweather.Listeners.WorldGuardListener;

/*
 * avweather java plugin
 */
public class Plugin extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("avweather");

  private ConfigHandler worldConfig;

  public void onEnable() {
    LOGGER.info("AVWeather has been enabled");
    
    //Load Config Handlers
    worldConfig = new ConfigHandler(this, "worldConfig.yml");
    
    //Load Listeners
    getServer().getPluginManager().registerEvents(new WorldGuardListener(), this);
    getServer().getPluginManager().registerEvents(new WeatherListener(worldConfig), this);

  }

  public void onDisable() {
    LOGGER.info("AV Weather has been disabled");
  }
}
