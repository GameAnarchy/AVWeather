package com.maplecoding.avweather.Utilities;

import java.util.Map;

public class Season {
    private String name;
    private Map<String, Integer> weatherProbabilities;
    
    public Season(String name, Map<String, Integer> weatherProbabilities) {
        if(!name.equals("TEMPERATE")) {
            this.name = name;
        } else {
            this.name = "WARM";
        }
        
        this.weatherProbabilities = weatherProbabilities;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getWeatherProbabilities() {
        return weatherProbabilities;
    }
}
