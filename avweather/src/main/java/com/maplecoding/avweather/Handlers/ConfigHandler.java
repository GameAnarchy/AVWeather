package com.maplecoding.avweather.Handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigHandler {

    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;

    // Instantiate ConfigHandler by getting the name of the config to handle
    public ConfigHandler(JavaPlugin plugin, String configName) {
        this.plugin = plugin;
        loadConfig(configName);
    }

    // Load config file
    public void loadConfig(String configName) {
        configFile = new File(plugin.getDataFolder(), configName);

        // Create new file if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveResource(configName, false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        // If file is empty, add default values
        if (config.getKeys(true).isEmpty()) {
            switch (configName) {
                case "worldConfig.yml":
                    String key = "";
                    String world = "worlds.world.";
                    String setting = "general.";
                    // General Settings
                    key = world + setting;
                    config.set(key+"enable", true);
                    config.set(key+"enableColdSource", false);
                    config.set(key+"enableHeatSource", false);
                    config.set(key+"enableOpenAudioMC", false);
                    config.set(key+"enableProjectKorra", false);
                    config.set(key+"enableProtection", false);
                    config.set(key+"enableRegions", false);
                    config.set(key+"enableSheltering", false);

                    // Temperature Settings
                    setting = "temperature.";
                    key = world + setting;
                    config.set(key+"defaultTemperature", "MIXED");
                    config.set(key+"temperatureDegree", "C");
                    config.set(key+"temperatureFluctuationRange", 5);
                    config.set(key+"enableTemperatureEffects", true);
                    config.set(key+"enableTemperatureNightChange", true);
                    config.set(key+"coldTemperatureRange", "-40,-10");
                    config.set(key+"coolTemperatureRange", "-9,0");
                    config.set(key+"mixedTemperaturerange", "1-15");
                    config.set(key+"warmTemperatureRange", "-16,30");
                    config.set(key+"hotTemperatureRange", "31,50");
                    config.set(key+"axis", Arrays.asList("Z"));
                    
                    Map<String,String> temperatureRangeX = new HashMap<>();
                    config.set(key+"rangeX", temperatureRangeX);

                    Map<String,String> temperatureRangeY = new HashMap<>();
                    config.set(key+"rangeY", temperatureRangeY);

                    Map<String,String> temperatureRangeZ = new HashMap<>();
                    temperatureRangeZ.put("-176,179", "COLD");
                    temperatureRangeZ.put("-180,-183","COOL");
                    temperatureRangeZ.put("-184,-187", "MIXED");
                    temperatureRangeZ.put("-188,-191", "WARM");
                    temperatureRangeZ.put("-192,-195", "HOT");
                    config.set(key+"rangeZ", temperatureRangeZ);
                   
                    
                    // Seasons Settings
                    setting = "seasons.";
                    key = world + setting;
                    config.set(key+"enableSeasons", true);
                    config.set(key+"seasonLength", 30);
                    config.set(key+"seasonPeak", 15);
                    config.set(key+"enableSeasonalWeather", true);

                    Map<String,String> coldSeasons = new HashMap<>();
                    coldSeasons.put("1_COOL","SUNNY.40+CLOUDY.30+RAIN.30");
                    coldSeasons.put("2_COLD", "SUNNY.20+CLOUDY.40+SNOW.30+SNOWSTORM.10");
                    coldSeasons.put("3_COLD", "SNOW.40+SNOWSTORM.30+CLOUDY.20+SUNNY.10");
                    coldSeasons.put("4_COOL", "SUNNY.34+CLOUDY.33+SNOW.33");
                    config.set(key+"coldSeasons", coldSeasons);

                    // Weather Settings
                    setting = "weather.";
                    key = world + setting;
                    config.set(key+"enableWeatherChanges", true);
                    config.set(key+"weatherChangeInterval", 600);


                    // Sheltering Settings
                    setting = "sheltering.";
                    key = world + setting;
                    config.set(key+"enableShelterDetection", true);
                    config.set(key+"shelterEffectRadius", 5);

                    // Player Settings
                    setting = "player.";
                    key = world + setting;
                    config.set(key+"enableWeatherEffects", true);
                    config.set(key+"weatherEffectStrength", 1.0);
                    config.set(key+"enableTemperatureEffects", true);
                    config.set(key+"temperatureEffectStrength", 1.0);
                    break;
                default:
                    break;

            }
            saveConfig();
        }
    }

    //Save config to file
    public void saveConfig() {
        try {
            config.save(configFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get the config data
    public FileConfiguration getConfig()
    {
        return config;
    }
}
