package com.maplecoding.avweather.Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.maplecoding.avweather.Utilities.Season;
import com.maplecoding.avweather.Utilities.TemperatureArea;

public class WeatherController {

    private JavaPlugin plugin;
    private final World world;

    //Default values if Config is empty
    private int seasonLength = 30;
    private int currentDay = 1;
    private long currentTimeTicks = 0;
    private long previousTimeTicks = 0;
    private int timeCheckInterval = 5;

    private Logger LOGGER = Logger.getLogger("AvWeather");

    private boolean isNight = false;

    private ConfigHandler worldHandler;
    private FileConfiguration worldConfig;
    private BukkitRunnable timeScheduler;

    private final Map<String, TemperatureArea> temperatureAreas;

    private String key;

    public WeatherController(JavaPlugin plugin, World world, ConfigHandler worldHandler) {
        this.plugin = plugin;
        this.world = world;

        this.worldHandler = worldHandler;
        this.worldConfig = this.worldHandler.getConfig();
        
        this.key = "worlds." + world.getName() + ".seasons.";
        this.currentDay = worldConfig.getInt(key+"currentDay");
        this.seasonLength = worldConfig.getInt(key+"seasonLength");
        this.timeCheckInterval = worldConfig.getInt(key+"checkTimeInterval");
    
        this.temperatureAreas = loadTemperatureAreas();
    }

    private Map<String, TemperatureArea> loadTemperatureAreas() {
        Map<String, TemperatureArea> areas = new HashMap<>();
        //Load default areas first
        areas = loadDefaultTemperatures();

        //List of all Temp Areas
        ConfigurationSection tempSection = worldConfig.getConfigurationSection(key+"temperatureAreas");
        if(tempSection != null) {

            //For each Temperature Area
            for (String tempKey : tempSection.getKeys(false)) {
                List<Season> seasons = new ArrayList<>();
                
                //List of selected and seasons
                ConfigurationSection seasonSection = tempSection.getConfigurationSection(tempKey);
                int selectedSeason = 0;
                if(seasonSection != null) {

                    //For Each Season in Temp Area
                    for(String seasonKey : seasonSection.getKeys(false)) {
                        Map<String, Integer> weatherProbabilities = new HashMap<>();
                        
                        if(seasonKey.equals("selected")) {
                            selectedSeason = seasonSection.getInt("selected");
                        } else {
                            String[] weatherEntries = seasonSection.getString(seasonKey).split("\\+");

                            //Get Weathers and their frequency
                            for(String entry: weatherEntries) {
                                String[] parts = entry.split("\\.");
                                if(parts.length == 2) {
                                    String weatherType = parts[0];
                                    int probability = 0;
                                    try {
                                        probability = Integer.parseInt(parts[1]);
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }
                                    weatherProbabilities.put(weatherType,probability);
                                } 
                            }

                            //Add Season name and its weathers to temperatureArea
                            Season season = new Season(seasonKey.split("_")[1],weatherProbabilities);
                            
                            //Add season to list of seasons for a temperature area
                            seasons.add(season);
                        }
                    }
                    //Add Temperature Area to Map (replaces default values)
                    TemperatureArea area = new TemperatureArea(seasons, selectedSeason);
                    areas.put(tempKey, area);
                }
            }
        }
        return areas;
    }

    //Load Default Season & Weather for all temperature area
    private Map<String, TemperatureArea> loadDefaultTemperatures() {
        Map<String, TemperatureArea> defaultTempAreas = new HashMap<>();
        for (String temperature : Arrays.asList("COLD","COOL","TEMPERATE","WARM","HOT")) {
            defaultTempAreas.put(temperature, loadDefaultTemperature(temperature));
        }
        return defaultTempAreas;
    }

    //Load default Season & Weather for a given temperature area
    private TemperatureArea loadDefaultTemperature(String tempArea) {
        String defaultTemperature = worldConfig.getString("worlds." + world.getName() + ".temperature.defaultTemperature");
        
        Map<String,Integer> defaultWeathers = new HashMap<>();
        String defaultWeather = worldConfig.getString("worlds." + world.getName() + ".weather.defaultWeather");
        defaultWeathers.put(defaultWeather, 100);

        List<Season> defaultSeasons = new ArrayList<>();
        Season defaultSeason = new Season(defaultTemperature, defaultWeathers);
        defaultSeasons.add(defaultSeason);

        TemperatureArea defaultTemperatureArea = new TemperatureArea(defaultSeasons, 0);

        return defaultTemperatureArea;
    }

    public TemperatureArea getTemperatureArea(String areaName) {
        return temperatureAreas.get(areaName);
    }

    public String getWorldName() {
        return world.getName();
    }

    public World getWorld() {
        return world;
    }

    public void startTimeScheduler() {
        timeScheduler = new BukkitRunnable() {
            
            @Override
            public void run() {
                
                //Get current time in ticks
                currentTimeTicks = world.getTime();

                //Check if a day has passed (0 to 24000)
                if(previousTimeTicks >= currentTimeTicks)
                {
                    currentDay++;
                    for (TemperatureArea area : temperatureAreas.values()) {
                        area.rollWeather();
                    }
                    LOGGER.info("A Weather Roll occured at next Day");
                    LOGGER.info("Previous Time Ticks: " + previousTimeTicks);
                    LOGGER.info("Current Time Ticks: " + currentTimeTicks);
                }
                previousTimeTicks = currentTimeTicks;

                //Change Season
                if(currentDay > seasonLength) {
                    currentDay = 1;
                    //Switch to next season for each area
                    for (TemperatureArea area : temperatureAreas.values()) {
                        area.nextSeason();
                    }
                    LOGGER.info("A Next Season occured at next Day");
                    LOGGER.info("Current Day: " + currentDay);
                    LOGGER.info("SeasonLength: " + seasonLength);
                }

                if(currentTimeTicks >= 13000) {
                    isNight = true;
                } else {
                    isNight = false;
                }
            }
        };
        timeScheduler.runTaskTimer(plugin, 0L, 20 * (long) timeCheckInterval);
    }


    public void stopTimeScheduler() {
        if(timeScheduler != null) {
            timeScheduler.cancel();
            worldConfig.set(key+"currentDay",currentDay);
            //worldHandler.saveConfig();
        }
    }
    
    
}
