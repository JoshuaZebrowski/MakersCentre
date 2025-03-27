package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Player {
    String name;
    Color colour;
    Node currentNode;
    List<Task> taskList;
    List<Task> pastTaskList;
    List<String> pastCategories;
    List<Task> pendingTasks;
    String currentCategory;
    float amountDoanted = 0;

    float playerCircleX, playerCircleY;
    float playerTargetX, playerTargetY;

    // resources
    Resource rand; // this will be the money
    Resource rand2; // this will be the people

    int taskSpeed; // Represents the number of turns a player must wait before starting a new task
    float communityMorale; // Represents the workforce's morale, must stay above 0%

    // this will track visited nodes to prevent the user from moving backwards on the board
    private Node lastVisitedNode;

    private Task activeTask;

    private boolean objectiveStarted = false;

    public Player(String name, Color colour) {
        this.name = name;
        this.colour = colour;
        this.currentNode = null;
        this.pastTaskList = new ArrayList<>();
        this.taskList = new ArrayList<>();
        this.pendingTasks = new ArrayList<>();
        this.pastCategories = new ArrayList<>();
        this.currentCategory = null;

        this.rand = new Resource("Money", 500000);
        this.rand2 = new Resource("People", 100);
        this.taskSpeed = 0; // Initially, no task is in progress
        this.communityMorale = 100; // Start with 100% morale

        this.lastVisitedNode = null; // Initialize the list of visited nodes

        this.activeTask = null;
    }

    // Add a method to reset the visited nodes at the start of a new turn
    public void resetVisitedNodes() {
        lastVisitedNode = null;
    }

    public boolean hasVisited(Node node) {
        if(lastVisitedNode != null){
            Gdx.app.log("s", lastVisitedNode.id + " " + node.id);
        }
        return lastVisitedNode != null
            && node != null
            && lastVisitedNode.id != null
            && lastVisitedNode.id.equals(node.id);
    }

    public void markVisited(Node node) {
        this.lastVisitedNode = node;
    }

    public void setPlayerNodeCirclePos(float circleRadius) {
        if (currentNode != null) {
            float offsetX = (currentNode.occupants.indexOf(this) % 2 == 0 ? -1 : 1) * circleRadius;
            float offsetY = (currentNode.occupants.indexOf(this) < 2 ? -1 : 1) * circleRadius;

            this.playerCircleX = currentNode.x + currentNode.size / 2 + offsetX;
            this.playerCircleY = currentNode.y - circleRadius * 1.5f + offsetY;
        }
    }

    public void updateAmountDonated(double amount){
        amountDoanted += amount;
    }

    public void setPlayerNodeTarget(float circleRadius) {
        if (currentNode != null) {
            float offsetX = (currentNode.occupants.indexOf(this) % 2 == 0 ? -1 : 1) * circleRadius;
            float offsetY = (currentNode.occupants.indexOf(this) < 2 ? -1 : 1) * circleRadius;

            this.playerTargetX = currentNode.x + currentNode.size / 2 + offsetX;
            this.playerTargetY = currentNode.y - circleRadius * 1.5f + offsetY;
        }
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    public List<Task> getTasks() {
        return taskList;
    }


    public void addTask(Task task) {
        if (currentCategory == null) {
            currentCategory = task.getCategory();
        }

        // Check if the task already exists in the list before adding
        for (Task existingTask : taskList) {
            if (existingTask.equals(task)) {
                System.out.println("Task " + task.getName() + " is already in the list.");
                return;
            }
        }

        // If not a duplicate, add the task to the list and mark it as selected
        taskList.add(task);
        task.setSelected(true);
    }


    public boolean hasSubTasks(Task task) {
        for (Task subtask : task.getSteps()) {
            if (taskList.contains(subtask)) {
                return true;
            }
        }
        return false;
    }


    public boolean hasTask(Task task) {
        return taskList.contains(task);
    }

    public Resource getRand() {
        return rand;
    }

    public Resource getRand2() {
        return rand2;
    }

    public boolean checkResources(Resource resource) {
        if (resource.getType().contains("Money")) {
            if (rand.getAmount() >= resource.getAmount()) {
                rand.deductAmount(resource.getAmount());
                return true;
            }
        }
        return false;
    }

    public boolean hasEnoughResources(Resource resource) {
        if (resource.getType().contains("Money")) {
            return rand.getAmount() >= resource.getAmount();
        }
        return false;
    }

    public void deductResource(Resource resource){
        if (resource.getType().contains("Money")){
            rand.deductAmount(resource.getAmount());
        }
        if (resource.getType().contains("People")){
            rand2.deductAmount(resource.getAmount());
        }
    }

    public boolean isCurrentCategoryComplete() {
        if (currentCategory == null) {
            Gdx.app.log("DEBUG", "Player " + name + " has no current category.");
            return true; // No category selected, so it's "complete"
        }

        // Check if all tasks of the current category are complete
        for (Task task : taskList) {
            if (task.getCategory().equals(currentCategory) && !task.isCompleted()) {
                Gdx.app.log("DEBUG", "Player " + name + " has incomplete task: " + task.getName() + " (Category: " + task.getCategory() + ")");
                return false; // At least one task in the category is incomplete
            }
        }


        currentCategory = null;
        activeTask = null;
        GameState.getInstance().updateCompletedAllCategories();
        pastTaskList.addAll(taskList);
        taskList.clear();
        pastCategories.add(currentCategory);
        objectiveStarted = false;


        Gdx.app.log("DEBUG", "Player " + name + " has completed all tasks in the " + currentCategory + " category.");
        return true; // All tasks in the category are complete
    }

    public List<Task> getPendingTasks() {
        return pendingTasks;
    }

    public void addPendingTask(Task task) {
        pendingTasks.add(task);
    }

    public void removePendingTask(Task task) {
        pendingTasks.remove(task);
    }



    public int getTaskSpeed() {
        return taskSpeed;
    }

    public void setTaskSpeed(int taskSpeed) {
        this.taskSpeed = taskSpeed;
    }

    public float getCommunityMorale() {
        return communityMorale;
    }

    public void setCommunityMorale(float communityMorale) {
        this.communityMorale = communityMorale;
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }

    // Add getter and setter for activeTask
    public Task getActiveTask() {
        return activeTask;
    }

    public void setActiveTask(Task activeTask) {
        this.activeTask = activeTask;
    }

    // Add a method to start a task
    public void startTask(Task task) {


        // Calculate the initial payment
        double initialMoneyCost = task.getResourceAmount("Money") / task.getTime();
        double peopleCost = task.getResourceAmount("People");

        // Deduct the initial payment (no check for negative values)
        rand.deductAmount(initialMoneyCost);
        rand2.deductAmount(peopleCost);

        // Set the task as active
        task.setActive(true);
        task.setRemainingTurns(task.getTime() - 1); // Subtract 1 for the current turn
        task.deductRemainingMoneyCost(initialMoneyCost);

        // Assign the task to the player
        activeTask = task;
        Gdx.app.log("DEBUG", task.getName() + " started. Remaining turns: " + task.getRemainingTurns());

    }

    public void progressTaskPayedOtherPlayer(Task task) {
        if (activeTask != null) {
            // Deduct the next payment
            double moneyCost = activeTask.getResourceAmount("Money") / activeTask.getTime();
            activeTask.deductRemainingMoneyCost(moneyCost);
            activeTask.decrementRemainingTurns();

            Gdx.app.log("DEBUG", "Progressed " + activeTask.getName() + ". Remaining turns: " + activeTask.getRemainingTurns());

            // Check if the task is complete
            if (activeTask.getRemainingTurns() <= 0) {
                activeTask.setCompleted(true);
                activeTask.setActive(false);
                activeTask = null; // Clear the active task
                SoundManager.getInstance().playSound("taskFinished");
                Gdx.app.log("DEBUG", "Task completed.");
            }
        }
    }

    public void progressTask() {
        if (activeTask != null) {
            // Deduct the next payment
            double moneyCost = activeTask.getResourceAmount("Money") / activeTask.getTime();
            rand.deductAmount(moneyCost);
            activeTask.deductRemainingMoneyCost(moneyCost);
            activeTask.decrementRemainingTurns();

            Gdx.app.log("DEBUG", "Progressed " + activeTask.getName() + ". Remaining turns: " + activeTask.getRemainingTurns());

            // Check if the task is complete
            if (activeTask.getRemainingTurns() <= 0) {
                activeTask.setCompleted(true);
                activeTask.setActive(false);
                activeTask = null; // Clear the active task
                SoundManager.getInstance().playSound("taskFinished");

                Gdx.app.log("DEBUG", "Task completed.");
            }
        }
    }


    // Add a method to check if the player has an active task
    public boolean hasActiveTask() {
        return activeTask != null;
    }

    public int getTurnsLeftForTask() {
        if (activeTask != null) {
            return activeTask.getRemainingTurns();
        }
        return -1; // Return -1 if no task is active
    }



    // Add getter and setter for objectiveStarted
    public boolean isObjectiveStarted() {
        return objectiveStarted;
    }

    public void setObjectiveStarted(boolean objectiveStarted) {
        this.objectiveStarted = objectiveStarted;
    }
}
