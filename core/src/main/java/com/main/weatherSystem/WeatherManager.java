package com.main.weatherSystem;

import java.util.Random;

public class WeatherManager {
    private String currentSeason;
    private Random random;

    public WeatherManager() {
        random = new Random();
    }

    public String getSeason(int globalTurn) {
        // Determine the season based on globalTurn
        String[] seasons = {"Spring", "Summer", "Autumn", "Winter"};
        currentSeason = seasons[globalTurn % 4];
        return currentSeason;
    }

    public String getWeatherForTurn(String currentSeason) {
        // Generate weather based on the current season
        switch (currentSeason) {
            case "Spring":
                return new Spring().generateWeather();
            case "Summer":
                return new Summer().generateWeather();
            case "Autumn":
                return new Autumn().generateWeather();
            case "Winter":
                return new Winter().generateWeather();
            default:
                return "Clear"; // Fallback
        }
    }

    public int getMaxMovesModifier(String weather) {
        // Return a modifier for maxMoves based on the weather
        switch (weather) {
            case "Sunny":
                return 1;  // Increase maxMoves by 1
            case "Partly Cloudy":
                return 0;  // No effect
            case "Cloudy":
                return 0;  // No effect
            case "Rainy":
                return -1; // Reduce maxMoves by 1
            case "Thunderstorms":
                return -2; // Reduce maxMoves by 2
            case "Snow":
                return -2; // Reduce maxMoves by 2
            default:
                return 0;  // No change
        }
    }

}
