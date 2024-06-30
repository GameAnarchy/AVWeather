package com.maplecoding.avweather.Handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.maplecoding.avweather.Utilities.WeatherEffects;

public class EffectsTimer {

    private JavaPlugin plugin;
    private BukkitRunnable timeScheduler;

    private Map<UUID, List<String>> playerList = new HashMap<>();

    public EffectsTimer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayerEffect(Player p, String effect, String amplifier, int duration) {

    }

    public void startTimeScheduler() {
        if(timeScheduler == null || timeScheduler.isCancelled()) {
            
            timeScheduler = new BukkitRunnable() {

                @Override
                public void run() {
                    
                    Set<UUID> playerSet = playerList.keySet();
                    
                    // Get each affected player in list
                    for (UUID uuid : playerSet) {
                        Player p = Bukkit.getPlayer(uuid);
                        if(p.isOnline()) {
                            List<String> playerEffects = playerList.get(uuid);

                            //Get info of each effect player has
                            for (String effect : playerEffects) {
                                String[] effectInfo = effect.split(".");
                                String effectName = effectInfo[0];
                                String effectAmplifier = effectInfo[1];
                                int effectDuration = Integer.parseInt(effectInfo[2]);

                                if(effectDuration <= 0) {
                                    WeatherEffects.applyEffects(p);
                                }
                                else {
                                    String neweffect = String.join(".", Arrays.asList());
                                }

                            }
                        } else {
                            //Remove offline player from list
                            playerList.remove(uuid);
                        }
                    }
                }
            };
            timeScheduler.runTaskTimer(plugin, 0, 20L); //20L = 1 sec
        }
    }

    public void stopTimeScheduler() {
        if(timeScheduler != null) {
            timeScheduler.cancel();
        }
    }
    
}
