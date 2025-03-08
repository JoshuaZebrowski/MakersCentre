package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Node> nodes;

    public Board() {
        nodes = new ArrayList<>();
        generateBoard();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void generateBoard(){
        int gridRows = 2 * 2;
        int gridCols = 2 * 2;
        float spacing = 100;

        float startX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f - spacing;

        // Generate nodes in an isometric grid format
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                float isoX = startX + (col - row) * spacing * 0.5f;   // Isometric x
                float isoY = startY + (col + row) * spacing * 0.25f;  // Isometric y
                Node node = new Node(isoX, isoY, "Node " + (row * gridCols + col + 1), 20);
                nodes.add(node);
            }
        }

        linkNodes(gridRows, gridCols);
        Gdx.app.log("Board", "Board generated");
        Gdx.app.log("Board", nodes.size() + " nodes generated");


    }

    private void linkNodes(int gridRows, int gridCols) {
        // Step 1: Randomly link nodes
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                int currentIndex = row * gridCols + col;
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

        // Step 2: Ensure every node has at least one link
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                int currentIndex = row * gridCols + col;
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
    }
}
