package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String name;
    Color color;
    Node currentNode;
    List<Task> taskList;
    List<Task> pendingTasks;
    String currentCategory;

    float playerCircleX, playerCircleY;
    float playerTargetX, playerTargetY;

    // resources
    Resource rand; // this will be the money
    Resource rand2; // this will be the people

    int taskSpeed; // Represents the number of turns a player must wait before starting a new task
    float communityMorale; // Represents the workforce's morale, must stay above 0%

    // this will track visited nodes to prevent the user from moving backwards on the board
    private List<Node> visitedNodes;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.currentNode = null;
        this.taskList = new ArrayList<>();
        this.pendingTasks = new ArrayList<>();
        this.currentCategory = null;

        this.rand = new Resource("Money", 2000000);
        this.rand2 = new Resource("People", 1000);
        this.taskSpeed = 0; // Initially, no task is in progress
        this.communityMorale = 100; // Start with 100% morale

        this.visitedNodes = new ArrayList<>(); // Initialize the list of visited nodes
    }

    // Add a method to reset the visited nodes at the start of a new turn
    public void resetVisitedNodes() {
        visitedNodes.clear();
    }

    // Add a method to check if a node has been visited
    public boolean hasVisited(Node node) {
        return visitedNodes.contains(node);
    }

    // Add a method to mark a node as visited
    public void markVisited(Node node) {
        if (!visitedNodes.contains(node)) {
            visitedNodes.add(node);
        }
    }

    public void setPlayerNodeCirclePos(float circleRadius) {
        if (currentNode != null) {
            float offsetX = (currentNode.occupants.indexOf(this) % 2 == 0 ? -1 : 1) * circleRadius;
            float offsetY = (currentNode.occupants.indexOf(this) < 2 ? -1 : 1) * circleRadius;

            this.playerCircleX = currentNode.x + currentNode.size / 2 + offsetX;
            this.playerCircleY = currentNode.y - circleRadius * 1.5f + offsetY;
        }
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

    public Color getColor() {
        return color;
    }

    public List<Task> getTasks() {
        return taskList;
    }


    public Task getCurrentTask(){
        Task currentTask = null;
        for (int i=0; i<taskList.size(); i++){
            currentTask = taskList.get(i);
        }
        return currentTask;
    }

    public void addTask(Task task) {
        if (currentCategory == null) {
            currentCategory = task.getCategory(); // Set the current category if not already set
        }
        taskList.add(task);
        task.setSelected(true); // Mark the task as selected
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
}
