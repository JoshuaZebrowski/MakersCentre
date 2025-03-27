package com.main;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {
    float x, y;
    float size;
    public List<Node> links;
    String id;
    public Color colour;
    public boolean occupied;
    List<Player> occupants;
    boolean isJobCentre;
    boolean highlighted = false;
    List<Node> subNodes;
    Task task;

    public Node(float x, float y, String id, float size) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.size = size;
        this.links = new ArrayList<>();
        this.colour = Color.BLUE;
        this.occupied = false;
        this.isJobCentre = false;
        this.occupants = new ArrayList<>();
        this.task = null;
    }

    public boolean shouldHighlight(Player currentPlayer) {
        return task != null && currentPlayer.getTasks().contains(task);
    }

    public boolean isTaskSelectedByAnyPlayer(List<Player> players) {
        if (task == null) {
            return false;
        }
        for (Player player : players) {
            if (player.getTasks().contains(task)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTaskSelectedByCurrentPlayer(Player currentPlayer) {
        return task != null && currentPlayer.getTasks().contains(task);
    }

    public void setIsJobCentre(boolean isJobCentre) {
        this.isJobCentre = isJobCentre;
    }

    public void addLink(Node node) {
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

    public void deOccupy(String name) {
        Iterator<Player> iterator = occupants.iterator();
        if (occupants.size() - 1 == 0) {
            occupied = false;
            updateColour();
        }
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.name.equals(name)) {
                iterator.remove();
                break;
            }
        }
    }

    public void updateColour() {
        String hex;

        if (isJobCentre) {
            hex = GameState.getInstance().getColourHex("Yellow");
        } else if (task != null) {
            switch (task.getCategory()) {
                case "Financial":
                    hex = GameState.getInstance().getColourHex("Red");
                    break;
                case "Educational":
                    hex = GameState.getInstance().getColourHex("Green");
                    break;
                case "Business":
                    hex = GameState.getInstance().getColourHex("Blue");
                    break;
                case "Community":
                    hex = GameState.getInstance().getColourHex("Purple");
                    break;
                case "CHANCE":
                    hex = "#D3D3D3";
                    break;
                default:
                    hex = GameState.getInstance().getColourHex("White");
            }
        } else {
            hex = GameState.getInstance().getColourHex("White");
        }

        this.colour = com.badlogic.gdx.graphics.Color.valueOf(hex);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        if (this.task == null) { // Only set the task if it hasn't been set before
            this.task = task;
            updateColour(); // Update the node's colour based on the task
        }
    }

}
