package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Node> nodes;

    public Board(List<Task> tasks) {
        nodes = new ArrayList<>();
        generateBoard(tasks); // Pass the list of tasks to generateBoard
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void generateBoard(List<Task> tasks) {
        int gridRows = 5; // 5 rows
        int gridCols = 6; // 6 columns (5x6 = 30 nodes)
        float spacing = 100;

        // Calculate the starting position for the main board
        float startX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f - spacing;

        // Create the starting node (Node 0)
        // Position it slightly below the main board using the correct coordinates
        float startingNodeX = startX - (gridCols / 2f) * (spacing / 6); // Center it horizontally relative to the board
        float startingNodeY = startY - (spacing / 3); // Position it slightly below the board
        Node startingNode = new Node(startingNodeX, startingNodeY, "Node 0", 20);
        startingNode.setIsJobCentre(true); // Mark this node as the Makers Center
        startingNode.setTask(null); // Ensure the starting node has no task
        startingNode.updateColour(); // Update its color (should be yellow)
        nodes.add(startingNode); // Add the starting node to the list

        // Shuffle the tasks to ensure random distribution
        java.util.Collections.shuffle(tasks);

        // Generate the 30 nodes in an isometric grid format
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                float isoX = startX + (col - row) * spacing * 0.5f;   // Isometric x
                float isoY = startY + (col + row) * spacing * 0.25f;  // Isometric y
                Node node = new Node(isoX, isoY, "Node " + (row * gridCols + col + 1), 20);

                // Assign a task to the node
                if (!tasks.isEmpty()) {
                    Task task = tasks.remove(0); // Remove the first task from the list (already shuffled)
                    node.setTask(task);
                }

                nodes.add(node);
            }
        }

        linkNodes(gridRows, gridCols);
        Gdx.app.log("Board", "Board generated");
        Gdx.app.log("Board", nodes.size() + " nodes generated");
    }

    private void linkNodes(int gridRows, int gridCols) {
        // Step 1: Randomly link nodes (skip the starting node)
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                int currentIndex = row * gridCols + col + 1; // +1 to skip the starting node
                Node currentNode = nodes.get(currentIndex);

                if (MathUtils.randomBoolean()) {
                    // Link to the node on the right if available
                    if (col < gridCols - 1) {
                        Node rightNode = nodes.get(currentIndex + 1);
                        currentNode.addLink(rightNode);
                    }

                    // Link to the node below if available
                    if (row < gridRows - 1) {
                        Node belowNode = nodes.get(currentIndex + gridCols);
                        currentNode.addLink(belowNode);
                    }
                }
            }
        }

        // Step 2: Ensure every node has at least one link (skip the starting node)
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                int currentIndex = row * gridCols + col + 1; // +1 to skip the starting node
                Node currentNode = nodes.get(currentIndex);

                // If the current node has no links, link it to either the right or below node
                if (currentNode.links.isEmpty()) {
                    // Link to the right node if available
                    if (col < gridCols - 1) {
                        Node rightNode = nodes.get(currentIndex + 1);
                        currentNode.addLink(rightNode);
                    }
                    // If no right node, link to the below node if available
                    else if (row < gridRows - 1) {
                        Node belowNode = nodes.get(currentIndex + gridCols);
                        currentNode.addLink(belowNode);
                    }
                }
            }
        }

        // Step 3: Link the starting node to at least one other node
        Node startingNode = nodes.get(0);
        if (startingNode.links.isEmpty()) {
            // Link the starting node to the first node in the grid
            startingNode.addLink(nodes.get(1));
        }
    }
}
