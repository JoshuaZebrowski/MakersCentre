package com.main;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {
    private String type;  // The type of resource (e.g., "Wood", "Stone").
    private double amount;  // The amount of the resource.
    private boolean completed;

    // Constructor
    public Resource(@JsonProperty("type") String type,
                    @JsonProperty("amount") double amount) {
        this.type = type;
        this.amount = amount;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }


    public double getAmount() {
        return amount;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted() {
        this.completed = true;
    }


    public void addAmount(double amount) {
        this.amount += amount;
    }

    public void removeAmount(double amount) {
        this.amount -= amount;
    }

}
