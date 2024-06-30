package com.maplecoding.avweather.Commands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.maplecoding.avweather.Commands.SubCommands.subNextSeason;
import com.maplecoding.avweather.Handlers.WeatherController;

public class WeatherCommand implements CommandExecutor{
    
    private final JavaPlugin plugin;
    private final Map<String, WeatherController> weatherControllerList;

    public WeatherCommand(JavaPlugin plugin, Map<String, WeatherController> weatherControllerList) {
        this.plugin = plugin;
        this.weatherControllerList = weatherControllerList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(args.length == 0) {
            sender.sendMessage("Please provide a subcommand");
        }

        if(args[0].equalsIgnoreCase("nextSeason")) {
            return new subNextSeason(weatherControllerList).onCommand(sender, command, label, args);
        }

        sender.sendMessage("Unknown subcommand.");
        
        return false;
    }
}
