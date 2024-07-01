package com.maplecoding.avweather.Utilities;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Protocol;
import com.comphenix.protocol.events.PacketContainer;

public class WeatherVisuals {

    public static void applySunny(Player p) {
        p.resetPlayerWeather();
        p.setPlayerWeather(WeatherType.CLEAR);
    }

    public static void applyRainy(Player p) {
        p.resetPlayerWeather();
        p.setPlayerWeather(WeatherType.DOWNFALL);
        applyThunderStatus(p, false);
    }

    public static void applyRainStorm(Player  p) {
        p.resetPlayerWeather();
        p.setPlayerWeather(WeatherType.DOWNFALL);
        applyThunderStatus(p, true);
    }

    public static void applyCloudy(Player p) {

    }

    public static void setSkyLightLevel(Player p, int x, int y, )

    public static void applyThunderStatus(Player p, boolean status) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer thunderPacket = manager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        thunderPacket.getBytes().write(0, (byte) 8); //Event: Thunder
        
        if(status) {
            thunderPacket.getFloat().write(0, 1.0f); //Thunder lvl: 1
        } else {
            thunderPacket.getFloat().write(0, 0.0f); //No Thunder lvl: 0
        }
        
        try {
            manager.sendServerPacket(p, thunderPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
