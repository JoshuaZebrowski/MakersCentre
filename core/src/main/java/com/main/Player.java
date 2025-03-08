package com.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class Player
{
    String name;
    Color color;
    Node currentNode;
    List<Task> taskList;

    float playerCircleX, playerCircleY;
    float playerTargetX, playerTargetY;

    // resources
    Resource rand;
    Resource people;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.currentNode = null;
        this.taskList = new ArrayList<>();

        this.rand = new Resource("Money", 2000000);
        this.people = new Resource("People", 10);

    }

    public void setPlayerNodeCirclePos(float circleRadius){

        if(currentNode != null) {
            float offsetX = (currentNode.occupants.indexOf(this) % 2 == 0 ? -1 : 1) * circleRadius;  // X offset for left/right circles
            float offsetY = (currentNode.occupants.indexOf(this) < 2 ? -1 : 1) * circleRadius;       // Y offset for top/bottom circles

            this.playerCircleX = currentNode.x + currentNode.size / 2 + offsetX ;
            this.playerCircleY = currentNode.y - circleRadius * 1.5f + offsetY;
        }

    }

    public void setPlayerNodeTarget(float circleRadius) {
        if (currentNode != null) {

            float offsetX = (currentNode.occupants.indexOf(this) % 2 == 0 ? -1 : 1) * circleRadius;  // X offset for left/right circles
            float offsetY = (currentNode.occupants.indexOf(this) < 2 ? -1 : 1) * circleRadius;       // Y offset for top/bottom circles

            this.playerTargetX = currentNode.x + currentNode.size / 2 + offsetX; // Center of the target node
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


    public List<Task> getTasks(){
        return taskList;
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public boolean hasSubTasks(Task task){
        for(Task subtask : task.getSteps()){
            if(taskList.contains(subtask)){
                return true;
            }
        }
        return false;
    }

    public boolean hasTask(Task task){
        return taskList.contains(task);
    }

    public Resource getRand(){
        return rand;
    }

    public boolean checkResources(Resource resource){
        if(resource.getType().contains("Money")){
            if (rand.getAmount() >= resource.getAmount()){
                rand.removeAmount(resource.getAmount());
                return true;
            }
        }else if(resource.getType().contains("People")){
            if (people.getAmount() >= resource.getAmount()){
                people.removeAmount(resource.getAmount());
                return true;
            }
        }

        return false;
    }

    public Resource getPeople() {
        return people;
    }

}
