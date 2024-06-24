package com.maplecoding.avweather;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherManager {
    // Get AudioAPI

    public enum Temperature {
        COLD, COOL, MIXED, WARM, HOT
    };

    public enum Weather {
        SUNNY, CLOUDY, RAIN, RAINSTORM, SNOW, SNOWSTORM, DRY, HUMID
    }

    public enum Status {
        EXPOSED, SHELTERED
    }

    public WeatherManager(JavaPlugin plugin) {
        // Initialize AudioAPI
    }

    public void getTemperature(Player p) {
        Location loc = p.getLocation();
    }

    public void applyWeatherEffects(Player p, Temperature temp, Weather weather) {
        switch (temp) {
            case COLD:
                switch (weather) {
                    case SUNNY:
                        // Apply Sunny Visuals
                        // Apply Sunny Audio
                        break;
                    case CLOUDY:
                        // Apply Cloudy Visuals
                        // Apply Cloudy Audio
                        break;
                    case SNOW:
                        // Apply Snow Visuals
                        // Apply Cold Snow Audio
                        // Apply Frostbite
                        break;
                    case SNOWSTORM:
                        // Apply SnowStorm Visuals
                        // Apply Cold SnowStorm Visuals
                        // Apply Frostbite if not wearing armor
                        if (p.getInventory().getChestplate().getType() != Material.IRON_CHESTPLATE) {
                            p.setFreezeTicks(1); // Max 140ticks; 20ticks = 1s
                        }
                        // Apply Slowness I or -Speed if not ^^
                        break;
                    case DRY:
                        // Apply Frostbite if not wearing armor
                        break;
                    case HUMID:
                        // Apply Thirst
                        break;
                }
                break;
            case COOL:
                break;
            case WARM:
                break;
            case HOT:
                break;
        }
    }

    public void updateWeatherForPlayer(Player p) {

    }

}
