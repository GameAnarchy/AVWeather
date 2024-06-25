package com.maplecoding.avweather;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.maplecoding.avweather.Handlers.ConfigHandler;
import com.maplecoding.avweather.Listeners.WeatherController;
import com.maplecoding.avweather.Listeners.WeatherListener;
import com.maplecoding.avweather.Listeners.WorldGuardListener;

/*
 * avweather java plugin
 */
public class Plugin extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("avweather");

  private ConfigHandler worldConfig;
  private WeatherController weatherController;
  private Map<String,WeatherController> weatherControllerList = new HashMap<>();

  public void onEnable() {
    
    //Load Config Handlers
    LOGGER.info("Loading World Config...");
    worldConfig = new ConfigHandler(this, "worldConfig.yml");


    //LOAD CONTROLLERS

    //Load weatherController Scheduler for each world in worldConfig
    Set<String> worldsinConfig = worldConfig.getConfig().getConfigurationSection("worlds").getKeys(false);
    for (String worldName : worldsinConfig) {
      World world = getServer().getWorld(worldName);
      weatherController = new WeatherController(this, world, worldConfig);
      weatherControllerList.put(worldName, weatherController);
      weatherController.startTimeScheduler();
    }

    //LOAD LISTENERS

    getServer().getPluginManager().registerEvents(new WorldGuardListener(), this);

    //Load WeatherListener for each weatherController/world
    for(String worldName : worldsinConfig) {
      getServer().getPluginManager().registerEvents(new WeatherListener(worldConfig,weatherControllerList.get(worldName)), this);
    }

    LOGGER.info("AVWeather has been enabled");
  }

  public void onDisable() {

    LOGGER.info("Disabling Time Schedulers...");
    for (WeatherController controller : weatherControllerList.values()) {
      controller.stopTimeScheduler();
    }

    LOGGER.info("AV Weather has been disabled");
  }
}
