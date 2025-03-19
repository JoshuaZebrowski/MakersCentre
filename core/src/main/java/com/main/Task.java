package com.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Task {
    private final String name;
    private final String description;
    private final List<Resource> resources; // Supports multiple resources
    private boolean completed = false;
    private final List<Task> steps; // Nested subtasks
    private final int time;
    private boolean taken = false;
    private Player owner;
    private final String category;
    private boolean selected = false;
    private boolean active = false;
    private boolean isChanceSquare = false;
    private boolean hasBeenOpened = false;
    private int remainingTurns; // Turns left to complete the task
    private double remainingMoneyCost; // Remaining money cost to be paid

    @JsonCreator
    public Task(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("resource") List<Resource> resources,
        @JsonProperty("steps") List<Task> steps,
        @JsonProperty("time") int time,
        @JsonProperty("category") String category,
        @JsonProperty("isChanceSquare") boolean isChanceSquare) { // Added communityMoraleImpact
        this.name = name;
        this.description = description;
        this.resources = resources;
        this.steps = steps;
        this.time = time;
        this.owner = null;
        this.category = category;
        this.isChanceSquare = isChanceSquare;
        this.remainingTurns = time;
        this.remainingMoneyCost = getResourceAmount("Money"); // Initialize the remaining money
    }


    // Add a method to get the amount of a specific resource
    public double getResourceAmount(String type) {
        for (Resource resource : resources) {
            if (resource.getType().equals(type)) {
                return resource.getAmount();
            }
        }
        return 0;
    }




    // Add a new method to check if the task is selected
    public boolean isSelected() {
        return selected;
    }

    // Add a new method to set the task as selected
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChanceSquare() {
        return isChanceSquare;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public List<Resource> getResources() {
        return resources;
    }

    @JsonIgnore
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getIsCompleted() {
        return completed;
    }

    public List<Task> getSteps() {
        return steps;
    }

    public String getResourceAmountString(String type) {
        for (Resource resource : resources) {
            if (resource.getType().equals(type)) {
                return String.valueOf(resource.getAmount()); // Return the amount as a String
            }
        }
        return String.valueOf(0); // Return "0" if the resource type is not found
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean taskTaken() {
        return taken;
    }

    public String getCategory() {
        return category;
    }

    public int getTime() {
        return time;
    }



    // Add getters and setters for the new fields
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    // Add a method to decrement the remaining turns
    public void decrementRemainingTurns() {
        remainingTurns--;
    }

    // Add a method to get the remaining money cost
    public double getRemainingMoneyCost() {
        return remainingMoneyCost;
    }

    // Add a method to deduct money from the remaining cost
    public void deductRemainingMoneyCost(double amount) {
        remainingMoneyCost -= amount;
    }

    // Add a new method to check if the chance square has been opened
    public boolean hasBeenOpened() {
        return hasBeenOpened;
    }

    // Add a new method to mark the chance square as opened
    public void setHasBeenOpened(boolean hasBeenOpened) {
        this.hasBeenOpened = hasBeenOpened;
    }
}
