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

    @JsonCreator
    public Task(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("resource") List<Resource> resources, // Map to "resource" in JSON
        @JsonProperty("steps") List<Task> steps,
        @JsonProperty("time") int time,
        @JsonProperty("isSubTask") boolean isSubTask)
    {
        this.name = name;
        this.description = description;
        this.resources = resources;
        this.steps = steps;
        this.time = time;
        this.isSubTask = isSubTask;
        this.owner = null;

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
        //either check if sub nodes are completed but if there are none then check if resources have been added and time has been done
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getIsCompleted(){
        return completed;
    }

    public List<Task> getSteps() {
        return steps;
    }

    public String getResourceAmount(String type){
        for (Resource resource : resources) {
            if (resource.getType().equals(type)) {
                return String.valueOf(resource.getAmount());
            }
        }
        return String.valueOf(0);
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean taskTaken(){
        return owner != null;
    }
}
