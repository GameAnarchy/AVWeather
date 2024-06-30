package com.maplecoding.avweather.Utilities;

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.maplecoding.avweather.Handlers.WeatherController;

public class WeatherEffects {

    private static final Logger LOGGER = Logger.getLogger("avweather");

    public void applyEffects(Player p) {
        
    }
    
    public void applyFrostEffect(Player p, String intensity) {
        int incrementRate;

        switch (intensity.toUpperCase()) {
            case "I":
                incrementRate = 2;
                break;
            case "II":
                incrementRate = 4;
                break;
            case "III":
            default:
                incrementRate = 6;
                break;
            case "IV":
                incrementRate = 8; 
        }
    }

    public void applyFatigueEffect(Player p, int amplifier, int duration) {
        long currentTick = controller.getWorld().getTime();
        long maxTick = controller.getWorld().getFullTime();
        int tickDuration = (int) (maxTick - currentTick);

        p.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,tickDuration,amplifier));
    }

    
}
