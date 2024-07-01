package com.maplecoding.avweather.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.maplecoding.avweather.Handlers.ConfigHandler;
import com.maplecoding.avweather.Handlers.EffectsTimer;
import com.maplecoding.avweather.Handlers.WeatherController;
import net.md_5.bungee.api.ChatColor;

public class WeatherListener implements Listener{
    
    private final FileConfiguration worldConfig;
    private final Map<String,WeatherController> weatherControllerList;
    private final EffectsTimer effectsTimer;

    private Map<String, Map<String,ConfigurationSection>> worldsTempRanges;
    private final Map<Player, String> playerTemperatures = new HashMap<>();
    private final Map<String,Map<String,String>> worldNightChangeList;
    private static final Logger LOGGER = Logger.getLogger("WeatherListener");

    public WeatherListener(ConfigHandler worldHandler, Map<String, WeatherController> weatherControllerList, EffectsTimer effectsTimer){
        this.worldConfig = worldHandler.getConfig();
        this.weatherControllerList = weatherControllerList;
        this.effectsTimer = effectsTimer;

        //Get the ranges for each world
        worldsTempRanges = getWorldsTempRanges();
        worldNightChangeList = getNightChanges();
        LOGGER.info(worldNightChangeList.get("world").toString());
    }

    //Get Player Temperature in location they joined
    //Get Player Weather in location they joined
    //Get DayTime
    //Apply Temperature & Weather Effects based on DayTime
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String playerWorld = p.getWorld().getName();

        //Check Temperature of Player's Range or default Temperature
        String currentTemperature = "";

        //Y has highest priority
        currentTemperature = getTemperature(p, "Y", getRanges(playerWorld, "Y"));

        //X has second priority
        if(currentTemperature.equals("")) {
            currentTemperature = getTemperature(p, "X", getRanges(playerWorld, "X"));
        }

        //Z has last priority
        if(currentTemperature.equals("")) {
            currentTemperature = getTemperature(p, "Z", getRanges(playerWorld, "Z"));
        }

        //Default temperature if no range was provided
        if(currentTemperature.equals("")) {
            currentTemperature = getDefaultTemperature(p);
        }

        //Check Night Temp Changes
        if(weatherControllerList.get(playerWorld).canApplyNightChange()) {
            currentTemperature = worldNightChangeList.get(playerWorld).get(currentTemperature);
        }

        //And add them to the Map
        playerTemperatures.put(p,currentTemperature);
        sendTemperatureMessage(p, currentTemperature);
            
        
    } 

    //Remove player from map on server leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        playerTemperatures.remove(e.getPlayer());
    }

    //Get Player Temperature on Region Change or default Temperature
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        //Only check temp if player moved a block
        Location from = e.getFrom();
        Location to = e.getTo();
        if((from.getZ() != to.getZ()) || (from.getY() != to.getY()) || (from.getX() != to.getX())) {

            Player p = e.getPlayer();
            String playerWorld = p.getLocation().getWorld().getName();

            //Check Temperature of Player's Range
            String currentTemperature = "";
            
            //Y has highest priority
            currentTemperature = getTemperature(p, "Y", getRanges(playerWorld, "Y"));

            //X has second priority
            if(currentTemperature.equals("")) {
                currentTemperature = getTemperature(p, "X", getRanges(playerWorld, "X"));
            }

            //Z has last priority
            if(currentTemperature.equals("")) {
                currentTemperature = getTemperature(p, "Z", getRanges(playerWorld, "Z"));
            }

            //Default temperature if no range was provided
            if(currentTemperature.equals("")) {
                currentTemperature = getDefaultTemperature(p);
            }

            //Check Night Temp Changes
            if(weatherControllerList.get(playerWorld).canApplyNightChange()) {
                currentTemperature = worldNightChangeList.get(playerWorld).get(currentTemperature);
            }

            String previousTemperature = playerTemperatures.get(p);

            //Ensures player is checked to map if they weren't in it
            //(e.g. serve reload reinitializes the map)
            if(previousTemperature == null) {
                previousTemperature = "";
            }
            
            //Inform player if moved to a new temperature range
            if(!currentTemperature.equals(previousTemperature)) {
                playerTemperatures.put(p, currentTemperature);
                sendTemperatureMessage(p, currentTemperature);
            }
            
        }               
    }

    //Get World Ranges (X,Y,Z)
    // Return an null if a range is empty
    public ConfigurationSection getRanges(String world, String axis) {
        return worldsTempRanges.get(world).get(axis);
    }

    //Get Temperature Ranges for each world in config
    public Map<String, Map<String, ConfigurationSection>> getWorldsTempRanges() {
        Map<String, Map<String,ConfigurationSection>> worldsTempRanges = new HashMap<>();
        for(String world : worldConfig.getConfigurationSection("worlds").getKeys(false)) {
            Map<String,ConfigurationSection> tempRanges = new HashMap<>();
            tempRanges.put("X", worldConfig.getConfigurationSection("worlds." + world + ".temperature.rangeX"));
            tempRanges.put("Y", worldConfig.getConfigurationSection("worlds." + world + ".temperature.rangeY"));
            tempRanges.put("Z", worldConfig.getConfigurationSection("worlds." + world + ".temperature.rangeZ"));
            worldsTempRanges.put(world, tempRanges);
        }
        return worldsTempRanges;
    }

    //Gt Night Change Info
    public Map<String,Map<String,String>> getNightChanges() {
        Map<String,Map<String,String>> nightChangeList = new HashMap<>();
        for(String world: worldConfig.getConfigurationSection("worlds").getKeys(false)) {
            Map<String, String> nightChange = new HashMap<>();
            for(String dayTemp: worldConfig.getConfigurationSection("worlds." + world + ".temperature.nightChange").getKeys(false)) {
                nightChange.put(dayTemp, worldConfig.getString("worlds." + world + ".temperature.nightChange." + dayTemp));
            }
            nightChangeList.put(world, nightChange);
        }
        return nightChangeList;
    }

    //Function to get the temperature from the ranges based on player location
    //If outside of range or null ranges, get default temperature in config
    public String getTemperature(Player p, String axis, ConfigurationSection rangeMap) {
        Location loc = p.getLocation();
        if(rangeMap != null) {
            for(String range: rangeMap.getKeys(false)) {
                
                String[] ranges = range.split(",");
                double range1 = Double.parseDouble(ranges[0]);
                double range2 = Double.parseDouble(ranges[1]);
                double playerLoc = 0;
                switch (axis){
                    case "X":
                        playerLoc = loc.getBlockX();
                        break;
                    case "Y":
                        playerLoc = loc.getBlockY();
                        break;
                    case "Z":
                    default:
                        playerLoc = loc.getBlockZ();
                        break;
                }

                if(range1 <= playerLoc && range2 >= playerLoc) {
                    return rangeMap.getString(range);
                }
            }
        }
        return "";        
    }

    public String getDefaultTemperature(Player p) {
        //Get default temperature if not in a range or range is null
        String key = "worlds." + p.getWorld().getName() + ".temperature.defaultTemperature";
        return worldConfig.getString(key);
    }

    public void sendTemperatureMessage(Player p, String temperature) {
        String playerWorld = p.getWorld().getName();
        String currentSeason = weatherControllerList.get(playerWorld).getTemperatureArea(temperature).getCurrentSeason().getName();
        String currentWeather = weatherControllerList.get(playerWorld).getTemperatureArea(temperature).getCurrentWeather();

        switch(temperature) {
            case "HOT":
                temperature = ChatColor.RED + temperature + ChatColor.RED;
                break;
            case "WARM":
                temperature = ChatColor.GOLD + temperature + ChatColor.GOLD;
                break;
            case "TEMPERATE":
                temperature = ChatColor.GREEN + temperature + ChatColor.GREEN;
                break;
            case "COOL":
                temperature = ChatColor.BLUE + temperature + ChatColor.BLUE;
                break;
            case "COLD":
                temperature = ChatColor.DARK_BLUE + temperature + ChatColor.DARK_BLUE;
                break;
        }

        String subtitle = currentSeason + " | " + currentWeather;
        switch(currentSeason) {
            case "COLD":
                subtitle = ChatColor.BLUE + subtitle;
                
                break;
            case "COOL": 
                subtitle = ChatColor.AQUA + subtitle;
                break;
            case "WARM":
                subtitle = ChatColor.YELLOW + subtitle;
            case "HOT":
                subtitle = ChatColor.GOLD + subtitle;
        }

        p.sendTitle(temperature, subtitle, 10, 70, 20);
    }

    public void applyWeatherEffects() {

    }

}
