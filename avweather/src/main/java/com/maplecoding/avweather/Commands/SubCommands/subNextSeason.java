package com.maplecoding.avweather.Commands.SubCommands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.maplecoding.avweather.Handlers.WeatherController;

public class subNextSeason implements CommandExecutor{

    private final Map<String,WeatherController> weatherControllerList;

    public subNextSeason(Map<String,WeatherController> weatherControllerList) {
        this.weatherControllerList = weatherControllerList;     
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(sender instanceof Player) {
            Player p = (Player)sender;
            String worldName = p.getLocation().getWorld().getName();
            if(weatherControllerList.get(worldName) != null) {

                //String currentSeason = weatherControllerList.get(worldName).getTemperatureArea(temperature).getCurrentSeason().getName();
                //String currentWeather = weatherControllerList.get(worldName).getTemperatureArea(temperature).getCurrentWeather();
            }
        }
        return false;
    }
    
}
