package com.maplecoding.Handlers;

import java.io.File;
import java.util.ArrayList;
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

            String key = "";
            String world = "worlds.world.";
            List<String> values = new ArrayList<String>();

            switch (configName) {
                case "config.yml":

                    // Enable Weather for this world
                    key = world + "enable";
                    config.set(key, false);

                    // Temperature Ranges and Associated Temperature
                    key = world + "temperature";
                    Map<String, String> temperatureValues = new HashMap<>();
                    temperatureValues.put("12,34", "COLD");
                    temperatureValues.put("35,60", "COOL");
                    config.set(key, temperatureValues);

                    // seasons for Each Temperature
                    Map<String, Integer> weatherFreq = new HashMap<>();
                    key = world + "coldSeasons";
                    // Summer
                    weatherFreq.put("SUNNY", 40);
                    weatherFreq.put("CLOUDY", 30);
                    weatherFreq.put("RAIN", 30);
                    config.set(key + ".COOL", weatherFreq);
                    weatherFreq.clear();
                    // Fall
                    weatherFreq.put("CLOUDY", 30);
                    weatherFreq.put("SNOW", 30);
                    weatherFreq.put("SNOWSTORM", 10);
                    weatherFreq.put("RAIN", 30);
                    config.set(key + ".COLD", weatherFreq);
                    values.add("COOL"); // Summer
                    values.add("COLD"); // Fall
                    values.add("COLD"); // Winter
                    values.add("COOL"); // Spring
                    config.set(key, values);
                    values.removeAll(values);

                    key = world + "coolSeasons";
                    values.add("WARM"); // Summer
                    values.add("COOL"); // Fall
                    values.add("COLD"); // Winter
                    values.add("COOL"); // Spring
                    config.set(key, values);
                    values.removeAll(values);

                    key = world + "mixedSeasons";
                    values.add("HOT"); // Summer
                    values.add("COOL"); // Fall
                    values.add("COLD"); // Winter
                    values.add("WARM"); // Spring
                    config.set(key, values);
                    values.removeAll(values);

                    key = world + "warmSeasons";
                    values.add("HOT"); // Summer
                    values.add("WARM"); // Fall
                    values.add("COOL"); // Winter
                    values.add("WARM"); // Spring
                    config.set(key, values);
                    values.removeAll(values);

                    key = world + "hotSeasons";
                    values.add("HOT"); // Summer
                    values.add("WARM"); // Fall
                    values.add("WARM"); // Winter
                    values.add("HOT"); // Spring
                    config.set(key, values);
                    values.removeAll(values);

                    // weather frequency for each temperature
                    key = world + "coldWeathers";
                    Map<String, String> weatherFrequency = new HashMap<>();
                    weatherFrequency.put("SUNNY", "30");
                    weatherFrequency.put("CLOUDY", "30");
                    weatherFrequency.put("SNOWSTORM", "20");
                    weatherFrequency.put("SNOW", "20");
                    break;

            }
        }
    }
}
