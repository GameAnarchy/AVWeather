package com.maplecoding.avweather.Utilities;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class TemperatureArea {
    private Season currentSeason;
    private String currentWeather;
    private List<Season> seasons;
    private int currentSeasonIndex;

    public TemperatureArea(List<Season> seasons, int currentSeasonIndex) {
        this.seasons = seasons; 
        this.currentSeasonIndex = currentSeasonIndex;
        this.currentSeason = seasons.get(currentSeasonIndex);
        rollWeather();
    }

    //Switch to next season for the temp area
    public void nextSeason() {
        currentSeasonIndex = (currentSeasonIndex + 1) % seasons.size();
        currentSeason = seasons.get(currentSeasonIndex);
        rollWeather();
    }

    //Roll weather based on frequency in config
    public void rollWeather() {
        int totalProbability = currentSeason.getWeatherProbabilities().values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = new Random().nextInt(totalProbability);
        int cumulativeProbability = 0;
        for (Map.Entry<String, Integer> entry : currentSeason.getWeatherProbabilities().entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue < cumulativeProbability) {
                currentWeather = entry.getKey();
                break;
            }
        }
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }
}
