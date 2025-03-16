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
    private int currentTime;
    private final boolean isSubTask;
    private boolean taken = false;
    private Player owner;
    private final String category;
    private final float communityMoraleImpact; // Added community morale impact
    private boolean selected = false;

    @JsonCreator
    public Task(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("resource") List<Resource> resources,
        @JsonProperty("steps") List<Task> steps,
        @JsonProperty("time") int time,
        @JsonProperty("isSubTask") boolean isSubTask,
        @JsonProperty("category") String category,
        @JsonProperty("communityMoraleImpact") float communityMoraleImpact) { // Added communityMoraleImpact
        this.name = name;
        this.description = description;
        this.resources = resources;
        this.steps = steps;
        this.time = time;
        this.isSubTask = isSubTask;
        this.owner = null;
        this.category = category;
        this.communityMoraleImpact = communityMoraleImpact; // Initialize community morale impact
    }

    // Add a new method to check if the task is selected
    public boolean isSelected() {
        return selected;
    }

    // Add a new method to set the task as selected
    public void setSelected(boolean selected) {
        this.selected = selected;
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

    public boolean getIsSubTask() {
        return isSubTask;
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

    public String getResourceAmount(String type) {
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

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public float getCommunityMoraleImpact() {
        return communityMoraleImpact; // Added getter for community morale impact
    }
}
