package com.maplecoding.avweather.Listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.maplecoding.avweather.Handlers.ConfigHandler;

import net.md_5.bungee.api.ChatColor;

public class WeatherListener implements Listener{
    
    private final FileConfiguration worldConfig;
    private final WeatherController weatherController;
    private final Map<Player, String> playerTemperatures = new HashMap<>();

    public WeatherListener(ConfigHandler worldHandler, WeatherController weatherController){
        this.worldConfig = worldHandler.getConfig();
        this.weatherController = weatherController;
    }

    //Get Player Temperature in location they joined
    //Get Player Weather in location they joined
    //Get DayTime
    //Apply Temperature & Weather Effects based on DayTime
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        World world = p.getWorld();

        //Ignore if incorrect world
        if(!world.getName().equals(weatherController.getWorld())) {
            return;
        }

        String key = "worlds."+ loc.getWorld().getName() +".temperature.";

        //Check Temperature of Player's Z Range or default Temperature
        //And add them to the Map
        ConfigurationSection rangeZ = worldConfig.getConfigurationSection(key+"rangeZ");
        String currentTemperature = getTemperature(p, "Z", loc, rangeZ);
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
        
        World world = e.getPlayer().getLocation().getWorld();
        //Ignore if incorrect world
        if(!world.getName().equals(weatherController.getWorld())) {
            return;
        }
        //Only check temp if player moved a block
        Location from = e.getFrom();
        Location to = e.getTo();
        if((from.getZ() != to.getZ()) || (from.getY() != to.getY()) || (from.getX() != to.getX())) {

            Player p = e.getPlayer();
            String key = "worlds." + p.getLocation().getWorld().getName() + ".temperature.";

            //Check Temperature of Player's Z Range
            ConfigurationSection rangeZ = worldConfig.getConfigurationSection(key+"rangeZ");
            String currentTemperature = getTemperature(p, "Z", to, rangeZ);
            String previousTemperature = playerTemperatures.get(p);
            
            //Inform player if new temperature
            if(previousTemperature != null && currentTemperature != previousTemperature) {
                playerTemperatures.put(p, currentTemperature);
                //Get Season

                sendTemperatureMessage(p, currentTemperature);
            }
            
        }               
    }

    //Function to get the temperature from the ranges based on player location
    //If outside of range or null ranges, get default temperature in config
    public String getTemperature(Player p, String axis, Location loc, ConfigurationSection rangeMap) {
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
                    case "Z":
                    default:
                        playerLoc = loc.getBlockZ();
                }

                if(range1 <= playerLoc && range2 >= playerLoc) {
                    return rangeMap.getString(range);
                }
            }
        }
        //Get default temperature if not in a range or range is null
        String key = "worlds." + loc.getWorld().getName() + ".temperature.defaultTemperature";
        String defaultTemperature = worldConfig.getString(key);
        return defaultTemperature;
        
    }

    public void sendTemperatureMessage(Player p, String temperature) {
        
        String currentSeason = weatherController.getTemperatureArea(temperature).getCurrentSeason().getName();
        String currentWeather = weatherController.getTemperatureArea(temperature).getCurrentWeather();

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

}
