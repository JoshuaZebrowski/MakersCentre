package com.main.weatherSystem;

import java.util.Random;

public class Winter {

    String[] weather = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Thunderstorms", "Snow"};
    double[] weatherProbabilities = {0.15, 0.1, 0.3, 0.25, 0.19999999, 0.00000001}; //stores calculated probabilities given previous two arrays

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

}
