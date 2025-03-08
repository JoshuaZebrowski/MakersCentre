package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.Tooltip;

import java.util.List;

public class Renderer {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Main _main;

    public OrthographicCamera camera;
    public OrthographicCamera uiCamera;
    private Viewport viewport;

    private float circleRadius;

    private Stage stage;
    private Slider maxMovesSlider;
    private Skin skin;

    private Window playerPopup;
    private Texture makersCenter;
    private List<Player> players;

    private com.main.player.playerTab tab;


    public Renderer(OrthographicCamera camera, OrthographicCamera uiCamera, Viewport viewport, float circleRadius, Player player,  Main main) {
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);

        this.camera = camera;
        this.uiCamera = uiCamera;
        this.viewport = viewport;

        this.circleRadius = circleRadius;

        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        players = PlayerManager.getInstance().getPlayers();

        loadTextures();


        createPlayerPopup();
        this.tab = new com.main.player.playerTab(player);

//        this._main = main;  // Store the reference to Main class


    }

    private void loadTextures(){
        makersCenter = new Texture(Gdx.files.internal("ui/makersCenter.png"));
    }

    public void setPlayerTab(){
        tab.isExpanded();
    }

    public void renderBoard(List<Node> nodes) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw lines between linked nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Node node : nodes) {
            for (Node linkedNode : node.links) {
                if(node.subNodes == null || !node.subNodes.contains(linkedNode)){
                    shapeRenderer.line(node.x + node.size / 2, node.y + node.size / 2,
                        linkedNode.x + linkedNode.size / 2, linkedNode.y + linkedNode.size / 2);
                }

            }
        }
        shapeRenderer.end();


        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw the nodes as filled isometric diamonds and player occupancy indicators
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Node node : nodes) {
            float halfWidth = node.size / 2;
            float halfHeight = node.size / 4;

            // Define the diamond vertices for the filled shape
            float topX = node.x + 10, topY = node.y+ 10 + halfHeight;
            float rightX = node.x + 10 + halfWidth, rightY = node.y+10;
            float bottomX =node.x + 10, bottomY = node.y +10- halfHeight;
            float leftX = node.x + 10 - halfWidth, leftY = node.y + 10;

            if(node.isHighlighted()){
                shapeRenderer.setColor(Color.YELLOW);
            }else{
                shapeRenderer.setColor(node.color);
            }
            shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
            shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);


            if(node.subNodes != null) {
                for(Node subNode : node.subNodes) {
                    halfWidth = subNode.size / 2;
                    halfHeight = subNode.size / 4;

                    // Define the diamond vertices for the filled shape
                    topX = subNode.x + 10;
                    topY = subNode.y+ 10 + halfHeight;
                    rightX = subNode.x + 10 + halfWidth;
                    rightY = subNode.y+10;
                    bottomX = subNode.x + 10;
                    bottomY = subNode.y +10- halfHeight;
                    leftX = subNode.x + 10 - halfWidth;
                    leftY = subNode.y + 10;


                    shapeRenderer.setColor(subNode.color);
                    shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
                    shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

                    if (subNode.occupied) {
                        for (int i = 0; i < Math.min(4, subNode.getOccupants().size()); i++) {

                            Player player = subNode.getOccupants().get(i);
                            shapeRenderer.setColor(player.getColor());
                            shapeRenderer.circle(player.playerCircleX , player.playerCircleY, circleRadius);
                        }
                    }
                }



            }

            // Draw occupancy indicators if node is occupied
            if (node.occupied) {
                for (int i = 0; i < Math.min(4, node.getOccupants().size()); i++) {

                    Player player = node.getOccupants().get(i);
                    shapeRenderer.setColor(player.getColor());
                    shapeRenderer.circle(player.playerCircleX , player.playerCircleY, circleRadius);
                }
            }
        }
        shapeRenderer.end();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (Node node : nodes) {
            if (node.isJobCentre) {

                float scale = 0.25f;
                float textureWidth = makersCenter.getWidth() * scale;
                float textureHeight = makersCenter.getHeight() * scale;

                float centerX = node.x + 10;
                float centerY = node.y + 10;

                float textureX = centerX - textureWidth / 2;
                float textureY = centerY - textureHeight / 2 + (node.size / 4); ;

                batch.draw(makersCenter, textureX, textureY, textureWidth, textureHeight);

            }
        }
        batch.end();

    }



    public void renderColourPicker(){
        // initialise
    }

    public void renderUI(int turn, int maxMoves, int currentMoves, String currentWeather, String currentSeason) {
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        //increase font size for all text
        font.getData().setScale(2f);

        // Display the prompt to press 'W' for weather info
        font.setColor(Color.YELLOW); // Use a noticeable color
        String weatherText = "\n\nSeason: " + currentSeason + "\nWeather: " + currentWeather + "\nPress 'W' to view information regarding weather." ;
        font.draw(batch, weatherText, 10, Gdx.graphics.getHeight() - 180); // Position at the top-left

        // Existing UI rendering logic
        font.setColor(Color.WHITE);
        font.draw(batch, "Work in progress -- Placeholder assets, data and look", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight());

        font.setColor(players.get(turn).getColor());
        int iPN = players.get(turn).getName().indexOf('#');
        String turnText = "Turn : " + players.get(turn).getName().substring(0, iPN);

        String resourceRand = players.get(turn).getRand().getType() + " : " + players.get(turn).getRand().getAmount() + " ZAR";
        String resourcePeople = players.get(turn).getPeople().getType() + " : " + players.get(turn).getPeople().getAmount();

        font.draw(batch, turnText, 10, viewport.getWorldHeight() - 30);

        if (maxMoves == 0) {
            font.draw(batch, "Number of moves left : Roll dice ", 10, viewport.getWorldHeight() - 60);
        } else {
            font.draw(batch, "Number of moves left : " + (maxMoves - currentMoves), 10, viewport.getWorldHeight() - 60);
        }
        font.draw(batch, "Resources :", 10, viewport.getWorldHeight() - 90);

        font.draw(batch, resourceRand, 40, viewport.getWorldHeight() - 120);
        font.draw(batch, resourcePeople, 40, viewport.getWorldHeight() - 150);

        batch.end();

        drawPlayerIndicators(turn);

        tab.draw(batch);

        if (playerPopup.isVisible()) {
            showPlayerPopup();
            drawPlayerPop();
        }
    }

    public void renderToolTips(){
        com.main.Tooltip.getInstance().render(uiCamera, viewport.getWorldWidth(), viewport.getWorldHeight());
    }
    private void drawPlayerIndicators(int currentTurn) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        final float GLOW_OFFSET = 5f;
        final float DEFAULT_RADIUS = 15f;
        final float ACTIVE_RADIUS = 30f;
        final float SPACER = 100f;

        int playerCount = players.size();
        float screenWidth = viewport.getWorldWidth();
        float startX = screenWidth / 2 - ((playerCount - 1) * SPACER / 2); // Center circles dynamically
        float circleY = viewport.getWorldHeight() - 50f;
        float textY = circleY - 50f;

        // Draw player indicators
        for (int i = 0; i < playerCount; i++) {
            float circleX = startX + i * SPACER;
            float circleRadius = (i == currentTurn) ? ACTIVE_RADIUS : DEFAULT_RADIUS;

            if (i == currentTurn && playerCount > 1) {
                shapeRenderer.setColor(Color.YELLOW);  // Glow effect
                shapeRenderer.circle(circleX, circleY, circleRadius + GLOW_OFFSET);
            }

            shapeRenderer.setColor(players.get(i).getColor());
            shapeRenderer.circle(circleX, circleY, circleRadius);
        }

        shapeRenderer.end();

        // Draw player names
        batch.begin();
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout();

        for (int i = 0; i < playerCount; i++) {
            float circleX = startX + i * SPACER;
            String playerName = players.get(i).getName();
            int hashIndex = playerName.indexOf('#');
            if (hashIndex != -1) {
                playerName = playerName.substring(0, hashIndex); // Extract name before '#'
            }

            layout.setText(font, playerName);
            float textX = circleX - layout.width / 2;
            font.draw(batch, layout, textX, textY);
        }

        batch.end();
    }

    public void updatePlayerTab(Player player){
        tab.playerTarget(player);
    }

    public void renderProgressBar(float progress, Color color) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);

        float barWidth = 200;  // Width of the progress bar
        float barHeight = 20;  // Height of the progress bar
        float x = (Gdx.graphics.getWidth() - barWidth) / 2;  // Center horizontally
        float y = 0;  // Position slightly below the top

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(x, y, barWidth, barHeight);

        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, barWidth * progress, barHeight);
        shapeRenderer.end();
    }

    public void renderDebugInfo(float debugDisplayX, float debugDisplayY, float debugDisplayWidth, float debugDisplayHeight, Node currentNode, int turn, int globalTurn, String currentSeason, int years) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(debugDisplayX, debugDisplayY, debugDisplayWidth, debugDisplayHeight);
        shapeRenderer.end();

        // Render the FPS text inside the square
        batch.begin();
        font.setColor(Color.WHITE);
        String fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
        String currentPos = "Current X: " + players.get(turn).playerCircleX + "Y: " + players.get(turn).playerCircleY;
        String targetPos = "Target X: " + players.get(turn).playerTargetX + "Y: " + players.get(turn).playerTargetY;
        String task = "Task: " + (currentNode.getTask() != null ? currentNode.getTask().getName() : "No Task") + " | Number Of Sub tasks: " + (currentNode.getTask() != null && currentNode.getTask().getSteps() != null ? currentNode.getTask().getSteps().size() : 0);
        String numbTurns = "Number of turns: " + globalTurn;
        String cS = "Current number years "+ years + " | Current Season: " + currentSeason;
        String tabExpansion = "Expansion tab: " + tab.isExpanded();

        String nodeId = "Current Node Id " + currentNode.id;
        String occupants = "Current Node Occupants: ";
        for(Player occupant : currentNode.occupants){
            occupants += occupant.getName() + " " + currentNode.occupants.indexOf(occupant) + " ";
        }
        font.draw(batch, fpsText, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 10);
        font.draw(batch, currentPos, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 40);
        font.draw(batch, targetPos, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 80);
        font.draw(batch, nodeId, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 100);
        font.draw(batch, occupants, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 120);
        font.draw(batch, task, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 140);
        font.draw(batch, numbTurns, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 160);
        font.draw(batch, cS, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 180);
        font.draw(batch, tabExpansion, debugDisplayX + 10, debugDisplayY + debugDisplayHeight - 200);

        batch.end();


    }

    public void renderDebugTravelLine(Player targetPlayer){
        // Ensure ShapeRenderer is in line-drawing mode
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE); // Optional: Set color for clarity


        // Source position: Current player position (centered at node)
        float startX = targetPlayer.playerCircleX;
        float startY = targetPlayer.playerCircleY;

        // Target position: Player's target position (centered at target node)
        float targetX = targetPlayer.playerTargetX;
        float targetY = targetPlayer.playerTargetY;

        // Draw the line from the player's current position to the target position
        shapeRenderer.line(startX, startY, targetX, targetY);

        shapeRenderer.end();
    }

    private void createPlayerPopup() {
        playerPopup = new Window("Assign Task", skin);
        playerPopup.setVisible(false);
        playerPopup.setMovable(true);

        stage.addActor(playerPopup);
    }

    public void showPlayerPopup() {
        playerPopup.clear();
        playerPopup.setVisible(true);

        Table contentTable = new Table();
        for (Player player : players) {
            // Player Name
            TextButton playerButton = new TextButton(player.getName(), skin);
            playerButton.getStyle().fontColor = Color.WHITE;
            playerButton.setColor(player.getColor()); // Set button color

            // Add click listener to print the player's name
            playerButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Player: " + player.getName());
                }
            });

            contentTable.add(playerButton).pad(10).right();
        }

        playerPopup.add(contentTable).pad(10);
        playerPopup.pack();
        playerPopup.setPosition(stage.getWidth() / 2 - playerPopup.getWidth() / 2, stage.getHeight() / 2 - playerPopup.getHeight() / 2);
    }

    public void drawPlayerPop(){
        stage.act();
        stage.draw();
    }

    public void hidePlayerPopup(boolean hide) {
        playerPopup.setVisible(hide);
    }

    public void renderPopUp(Node node) {
        // Start shape rendering for the background rectangle
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Set rectangle color
        shapeRenderer.setColor(Color.valueOf("FFFCF2"));

        // Measure the text and calculate rectangle dimensions
        GlyphLayout layout = new GlyphLayout(); // For measuring text dimensions
        float padding = 10f;                   // Padding around the text
        float lineSpacing = 5f;                // Space between lines


        // Prepare the text to be displayed
        Array<String> lines = new Array<>();
        if(node.task != null){
            lines.add(node.task.getName());
            String description = node.task.getDescription()
                .replace("{m}", node.task.getResourceAmount("Money"))
                .replace("{p}", node.task.getResourceAmount("People"));

            String[] words = description.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > 50) {
                    lines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder();
                }
                currentLine.append(word).append(" ");
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString().trim()); // Add the last line
            }

        }else if(node.isJobCentre){
            lines.add("Makers Centre");
        }

        // Determine the widest line for rectangle width
        float maxTextWidth = 0f;
        for (String line : lines) {
            layout.setText(font, line);
            if (layout.width > maxTextWidth) {
                maxTextWidth = layout.width;
            }
        }

        float rectWidth = maxTextWidth + padding * 2;
        float rectHeight = lines.size * font.getLineHeight() + (lines.size - 1) * lineSpacing + padding * 2;

        // Position rectangle above the node

        Vector3 screenCoords = camera.project(new Vector3(node.x, node.y, 0));

        float rectX = screenCoords.x - rectWidth / 2;
        float rectY = screenCoords.y + node.size + 5;

        // Draw rectangle
        shapeRenderer.rect(rectX, rectY, rectWidth, rectHeight);
        shapeRenderer.end();

        // Start batch rendering for the text
        batch.begin();
        font.setColor(Color.BLACK);

        // Draw each line of text inside the rectangle
        float textY = rectY + rectHeight - padding; // Start drawing text from the top
        for (String line : lines) {
            font.draw(batch, line, rectX + padding, textY);
            textY -= font.getLineHeight() + lineSpacing; // Move down for the next line
        }
        batch.end();
    }


    public void renderCurrentNodeBox(Node node, float boxWidth, float boxHeight, float padding, float tileSize, float rightSidePadding, float centerX, float centerY) {
        // Start rendering the box background
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background box
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(centerX - boxWidth / 2, padding, boxWidth, boxHeight); // Position it at the bottom


//        // Spinning tile logic (unchanged)
        float rotationAngle = (System.currentTimeMillis() % 3600) / 10f; // Rotate over time
        float halfWidth = tileSize / 2;
        float halfHeight = tileSize / 4;

        float cosA = (float) Math.cos(Math.toRadians(rotationAngle));
        float sinA = (float) Math.sin(Math.toRadians(rotationAngle));

        float topX = centerX, topY = centerY + halfHeight;
        float rightX = centerX + halfWidth, rightY = centerY;
        float bottomX = centerX, bottomY = centerY - halfHeight;
        float leftX = centerX - halfWidth, leftY = centerY;

        topX = rotateX(topX, topY, centerX, centerY, cosA, sinA);
        topY = rotateY(topX, topY, centerX, centerY, cosA, sinA);

        rightX = rotateX(rightX, rightY, centerX, centerY, cosA, sinA);
        rightY = rotateY(rightX, rightY, centerX, centerY, cosA, sinA);

        bottomX = rotateX(bottomX, bottomY, centerX, centerY, cosA, sinA);
        bottomY = rotateY(bottomX, bottomY, centerX, centerY, cosA, sinA);

        leftX = rotateX(leftX, leftY, centerX, centerY, cosA, sinA);
        leftY = rotateY(leftX, leftY, centerX, centerY, cosA, sinA);

        shapeRenderer.setColor(node.color);
        shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
        shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

        shapeRenderer.end();


        // Draw the node information box (unchanged)
        batch.begin();
        font.setColor(Color.WHITE);
        String infoText = "Node ID: " + node.id;
        GlyphLayout layout = new GlyphLayout(font, infoText);

        float textX = centerX - layout.width / 2; // Center text horizontally in the box
        float textY = padding + boxHeight - 20; // Position the text near the top of the box
        font.draw(batch, infoText, textX, textY);
        batch.end();
    }

    // Helper methods for rotating vertices
    private float rotateX(float x, float y, float cx, float cy, float cosA, float sinA) {
        return cosA * (x - cx) - sinA * (y - cy) + cx;
    }

    private float rotateY(float x, float y, float cx, float cy, float cosA, float sinA) {
        return sinA * (x - cx) + cosA * (y - cy) + cy;
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();

    }


    public void renderWeatherAlert(String weatherAlertText) {
        batch.begin();
        font.getData().setScale(4f); // Increase font size
        font.setColor(Color.GOLD);

        // Calculate the position to center the text
        GlyphLayout layout = new GlyphLayout(font, weatherAlertText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;

        // Draw the alert text
        font.draw(batch, weatherAlertText, x, y+200);
        font.getData().setScale(1f); // Reset font size
        batch.end();
    }


}
