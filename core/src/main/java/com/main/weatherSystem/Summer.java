package com.main.weatherSystem;

import java.util.Random;

public class Summer {

    String[] weather = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Thunderstorms"};
    double[] weatherProbabilities = {0.5, 0.25, 0.15, 0.08, 0.02}; //stores calculated probabilities given previous two arrays

    //generates a random weather based on the probabilities
    public String generateWeather() {
        Random rand = new Random();
        double randomValue = rand.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < weatherProbabilities.length; i++) {
            cumulativeProbability += weatherProbabilities[i];
            if (randomValue <= cumulativeProbability) {
                return weather[i];
            }
        }
        return weather[weather.length - 1]; // Fallback in case of rounding errors
    }

    //returns the effect that a weather has on the turn
    //the return format is [resource efficiency, task speed, community morale]
    public double[] weatherEffects(String weather) {
        if (weather.equals("Sunny")) {
            double effects[] = {1.2, 1.1, 1.3};
            return effects;
        } else if (weather.equals("Partly Cloudy")) {
            double effects[] = {1, 1, 1};
            return effects;
        } else if (weather.equals("Cloudy")) {
            double effects[] = {1, 1, 0.9};
            return effects;
        } else if (weather.equals("Rainy")) {
            double effects[] = {0.9, 0.8, 0.8};
            return effects;
        } else if (weather.equals("Thunderstorms")) {
            double effects[] = {0.7, 0.5, 0.5};
            return effects;
        } else {
            double effects[] = {1, 1, 1};
            return effects;
        }
    }


}
