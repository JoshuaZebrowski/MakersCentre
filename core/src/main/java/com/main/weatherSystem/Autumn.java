package com.main.weatherSystem;

import java.util.Random;

public class Autumn {

    int[] highTemps = {14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    int[] highTempsFreq = {5, 6, 4, 7, 7, 8, 16, 11, 10, 8, 6, 8, 4};
    double[] probabilities = {0.05, 0.06, 0.04, 0.07, 0.07, 0.08, 0.16, 0.11, 0.10, 0.08, 0.06, 0.08, 0.04}; //stores calculated probabilities given previous two arrays

    String[] weather = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Thunderstorms"};
    int[] weatherFreq = {46, 23, 11, 6, 1}; //stores frequency of each weather type
    double[] weatherProbabilities = {0.1, 0.23, 0.15, 0.5, 0.02}; //stores calculated probabilities given previous two arrays

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

    //generates a random temperature based on the probabilities
    public int generateTemperature() {
        Random rand = new Random();
        double randomValue = rand.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return highTemps[i];
            }
        }
        return highTemps[highTemps.length - 1]; // Fallback in case of rounding errors
    }
}
