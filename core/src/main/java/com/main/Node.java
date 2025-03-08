package com.main;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node
{
    float x, y;
    float size;
    List<Node> links;
    String id;
    Color color;
    boolean occupied;
    List<Player> occupants;
    boolean isJobCentre;
    boolean highlighted = false;
    List<Node> subNodes;

    Task task;


    Node(float x, float y, String id, float size) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.size = size;
        this.links = new ArrayList<>();
        this.color = Color.BLUE;
        this.occupied = false;
        this.isJobCentre = false;
        this.occupants = new ArrayList<>();
        this.task = null;
    }

    public void setIsJobCentre(boolean isJobCentre) {
        this.isJobCentre = isJobCentre;
    }

    void addLink(Node node) {
        links.add(node);
    }

    public List<Player> getOccupants() {
        return occupants;
    }

    public void occupy(Player player) {
        occupants.add(player);
        occupied = true;
        updateColour();
    }

    public boolean containsCurrentPlayer(Player player) {
        return occupants.contains(player);
    }

    public void deOccupy(String name){
        Iterator<Player> iterator = occupants.iterator();
        if(occupants.size() -1 == 0){
            occupied = false;

            updateColour();
        }
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.name.equals(name)) {
                iterator.remove();  // Safely remove the player while iterating
                break;  // Stop after finding the first match
            }
        }
    }

    public void updateColour() {
        this.color = isJobCentre ? Color.YELLOW : (task != null ? Color.CORAL : Color.BLUE);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isHighlighted(){
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}

