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

    private Window confirmationPopup;


    public Renderer(OrthographicCamera camera, OrthographicCamera uiCamera, Viewport viewport, float circleRadius, Player player, Main main) {
        if (camera == null || uiCamera == null || viewport == null) {
            throw new IllegalArgumentException("Camera, UI Camera, or Viewport cannot be null.");
        }

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
        createConfirmationPopup();
        this.tab = new com.main.player.playerTab(player);
        this._main = main; // Store the reference to the Main class
    }





    private void loadTextures() {
        makersCenter = new Texture(Gdx.files.internal("ui/makersCenter.png"));
    }




    public void setPlayerTab() {
        tab.isExpanded();
    }

    public void renderBoard(List<Node> nodes) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw links between nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Node node : nodes) {
            for (Node linkedNode : node.links) {
                if (node.subNodes == null || !node.subNodes.contains(linkedNode)) {
                    shapeRenderer.line(node.x + node.size / 2, node.y + node.size / 2,
                        linkedNode.x + linkedNode.size / 2, linkedNode.y + linkedNode.size / 2);
                }
            }
        }
        shapeRenderer.end();

        // Draw nodes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Node node : nodes) {
            // Check if the node's task is selected by any player
            boolean isTaskSelectedByAnyPlayer = _main.isTaskSelectedByAnyPlayer(node);
            float scaleFactor = isTaskSelectedByAnyPlayer ? 1.5f : 1.0f; // Scale up by 1.5x if selected

            float halfWidth = (node.size / 2) * scaleFactor;
            float halfHeight = (node.size / 4) * scaleFactor;

            float topX = node.x + 10, topY = node.y + 10 + halfHeight;
            float rightX = node.x + 10 + halfWidth, rightY = node.y + 10;
            float bottomX = node.x + 10, bottomY = node.y + 10 - halfHeight;
            float leftX = node.x + 10 - halfWidth, leftY = node.y + 10;

            // If the task is completed, set the node color to yellow
            if (node.getTask() != null && node.getTask().isCompleted()) {
                shapeRenderer.setColor(Color.YELLOW);
            } else {
                // Otherwise, use the node's original color
                shapeRenderer.setColor(node.color);
            }


            // Draw the node
            shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
            shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

            // Check if the node's task is selected by the current player
            boolean isTaskSelectedByCurrentPlayer = _main.isTaskSelectedByCurrentPlayer(node);

            // Draw a white border if the task is selected by the current player
            if (isTaskSelectedByCurrentPlayer) {
                shapeRenderer.end(); // End the filled shape rendering
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // Switch to line rendering
                shapeRenderer.setColor(Color.WHITE); // Set border color to white

                // Draw the border around the node
                shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
                shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

                shapeRenderer.end(); // End the line rendering
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Switch back to filled rendering
            }

            // Handle sub-nodes
            if (node.subNodes != null) {
                for (Node subNode : node.subNodes) {
                    boolean isSubNodeTaskSelectedByAnyPlayer = _main.isTaskSelectedByAnyPlayer(subNode);
                    float subNodeScaleFactor = isSubNodeTaskSelectedByAnyPlayer ? 1.5f : 1.0f;

                    halfWidth = (subNode.size / 2) * subNodeScaleFactor;
                    halfHeight = (subNode.size / 4) * subNodeScaleFactor;

                    topX = subNode.x + 10;
                    topY = subNode.y + 10 + halfHeight;
                    rightX = subNode.x + 10 + halfWidth;
                    rightY = subNode.y + 10;
                    bottomX = subNode.x + 10;
                    bottomY = subNode.y + 10 - halfHeight;
                    leftX = subNode.x + 10 - halfWidth;
                    leftY = subNode.y + 10;

                    // Use the sub-node's original color
                    shapeRenderer.setColor(subNode.color);

                    // Draw the sub-node
                    shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
                    shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

                    // Check if the sub-node's task is selected by the current player
                    boolean isSubNodeTaskSelectedByCurrentPlayer = _main.isTaskSelectedByCurrentPlayer(subNode);

                    // Draw a yellow border if the task is selected by the current player
                    if (isSubNodeTaskSelectedByCurrentPlayer) {
                        shapeRenderer.end(); // End the filled shape rendering
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // Switch to line rendering
                        shapeRenderer.setColor(Color.YELLOW); // Set border color to yellow

                        // Draw the border around the sub-node
                        shapeRenderer.triangle(topX, topY, rightX, rightY, bottomX, bottomY);
                        shapeRenderer.triangle(bottomX, bottomY, leftX, leftY, topX, topY);

                        shapeRenderer.end(); // End the line rendering
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Switch back to filled rendering
                    }

                    // Draw occupants (players) on the sub-node
                    if (subNode.occupied) {
                        for (int i = 0; i < Math.min(4, subNode.getOccupants().size()); i++) {
                            Player player = subNode.getOccupants().get(i);
                            shapeRenderer.setColor(player.getColor());
                            shapeRenderer.circle(player.playerCircleX, player.playerCircleY, circleRadius);
                        }
                    }
                }
            }

            // Draw occupants (players) on the node
            if (node.occupied) {
                for (int i = 0; i < Math.min(4, node.getOccupants().size()); i++) {
                    Player player = node.getOccupants().get(i);
                    shapeRenderer.setColor(player.getColor());
                    shapeRenderer.circle(player.playerCircleX, player.playerCircleY, circleRadius);
                }
            }
        }

        shapeRenderer.end();

        // Draw the Makers Center icon
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
                float textureY = centerY - textureHeight / 2 + (node.size / 4);

                batch.draw(makersCenter, textureX, textureY, textureWidth, textureHeight);
            }
        }
        batch.end();
    }

    public void renderUI(int turn, int maxMoves, int currentMoves, String currentWeather, String currentSeason, Node currentNode, boolean attemptedTaskSelection, boolean hasMoved, boolean attemptedPrematureTurnEnd) {
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        font.getData().setScale(2f);

        // Draw weather and season information
        font.setColor(Color.YELLOW);
        String weatherText = "Season: " + currentSeason + "\nWeather: " + currentWeather + "\nPress 'W' to view information regarding weather.";
        font.draw(batch, weatherText, 10, Gdx.graphics.getHeight() - 250);

        // Draw the current player's objective below the weather information
        String objective = _main.getCurrentPlayerObjective();
        if (objective != null) {
            Color objectiveColor = _main.getColorForObjective(objective);
            font.setColor(objectiveColor);

            String objectiveText = "Current Objective: " + objective;
            GlyphLayout layout = new GlyphLayout(font, objectiveText);

            float x = 10; // Left side of the screen with padding
            float y = Gdx.graphics.getHeight() - 400; // Below the weather information

            font.draw(batch, objectiveText, x, y);
        }

        // Draw current player information
        font.setColor(players.get(turn).getColor());
        String playerName = players.get(turn).getName();
        int hashIndex = playerName.indexOf('#');
        if (hashIndex != -1) {
            playerName = playerName.substring(0, hashIndex);
        }
        String turnText = "Turn : " + playerName;

        // Draw money (rand) and people (rand2) resources
        String resourceRand = players.get(turn).getRand().getType() + " : " + players.get(turn).getRand().getAmount() + " ZAR";
        String resourcePeople = players.get(turn).getRand2().getType() + " : " + players.get(turn).getRand2().getAmount();

        font.draw(batch, turnText, 10, viewport.getWorldHeight() - 30);

        if (maxMoves == 0) {
            font.draw(batch, "Number of moves left : Roll dice ", 10, viewport.getWorldHeight() - 60);
        } else {
            font.draw(batch, "Number of moves left : " + (maxMoves - currentMoves), 10, viewport.getWorldHeight() - 60);
        }
        font.draw(batch, "Resources :", 10, viewport.getWorldHeight() - 90);

        // Draw money (rand)
        font.draw(batch, resourceRand, 40, viewport.getWorldHeight() - 120);
        // Draw people (rand2) below money
        font.draw(batch, resourcePeople, 40, viewport.getWorldHeight() - 150);

        // Draw task progress information
        int turnsLeft = players.get(turn).getTurnsLeftForTask();
        if (turnsLeft >= 0) {
            String taskProgressText = "Turns left for task: " + turnsLeft;
            font.draw(batch, taskProgressText, 10, viewport.getWorldHeight() - 180);
        } else {
            String taskProgressText = "No active task.";
            font.draw(batch, taskProgressText, 10, viewport.getWorldHeight() - 180);
        }



        // Draw the "Press 'T' to open player tab" text in the top-right corner
        font.setColor(Color.WHITE);
        String playerTabText = "Press 'T' to open player tab";
        GlyphLayout playerTabLayout = new GlyphLayout(font, playerTabText);
        float playerTabX = Gdx.graphics.getWidth() - playerTabLayout.width - 20; // Right side of the screen with padding
        float playerTabY = Gdx.graphics.getHeight() - 50; // Top of the screen with padding
        font.draw(batch, playerTabText, playerTabX, playerTabY);

        // Check if the objective is already claimed by another player
        boolean isObjectiveClaimed = false;
        if (currentNode.getTask() != null) {
            String taskCategory = currentNode.getTask().getCategory();
            isObjectiveClaimed = _main.getObjectiveOwners().containsKey(taskCategory) &&
                _main.getObjectiveOwners().get(taskCategory) != players.get(turn);
        }

        Player currentPlayer = players.get(turn);

        // Only show task selection prompts if the player has no moves left
        if (currentMoves >= maxMoves) {
            // If the objective hasn't started
            if (!currentPlayer.isObjectiveStarted()) {
                // If the task is available to select (not taken and not selected)
                if (currentNode.getTask() != null && !currentNode.getTask().taskTaken() && !currentNode.getTask().isSelected()) {
                    // Display the appropriate message based on whether the player attempted to select a task
                    if (attemptedTaskSelection) {
                        if (currentNode.getTask().isChanceSquare()) {
                            if (!currentNode.getTask().hasBeenOpened()){
                                font.setColor(Color.WHITE);
                                font.getData().setScale(2f); // Larger font size for better visibility
                                String taskMessage = "This is a chance square, press 's' to open.";
                                GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                                float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20; // Right side of the screen with padding
                                float taskY = playerTabY - taskLayout.height - 50; // Positioned lower to avoid overlap
                                font.draw(batch, taskMessage, taskX, taskY);
                            }
                            else {
                                font.setColor(Color.RED);
                                font.getData().setScale(2f); // Larger font size for better visibility
                                String taskMessage = "This chance square has already been opened.";
                                GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                                float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20; // Right side of the screen with padding
                                float taskY = playerTabY - taskLayout.height - 50; // Positioned lower to avoid overlap
                                font.draw(batch, taskMessage, taskX, taskY);
                            }
                        } else if (!isObjectiveClaimed && (currentPlayer.getCurrentCategory() == null ||
                            currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) {
                            font.setColor(Color.WHITE);
                            font.getData().setScale(2f); // Larger font size for better visibility
                            String taskMessage = "Task Available to Select. Press 's' to Select Task.";
                            GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                            float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20; // Right side of the screen with padding
                            float taskY = playerTabY - taskLayout.height - 50; // Positioned lower to avoid overlap
                            font.draw(batch, taskMessage, taskX, taskY);
                        } else if (isObjectiveClaimed || (currentPlayer.getCurrentCategory() != null && !currentNode.getTask().getCategory().equals(currentPlayer.getCurrentCategory()))) {
                            font.setColor(Color.RED);
                            font.getData().setScale(2f); // Larger font size for better visibility
                            String categoryMessage = "You cannot select tasks from other people's categories.";
                            GlyphLayout categoryLayout = new GlyphLayout(font, categoryMessage);
                            float categoryX = Gdx.graphics.getWidth() - categoryLayout.width - 20; // Right side of the screen with padding
                            float categoryY = playerTabY - categoryLayout.height - 50; // Positioned lower to avoid overlap
                            font.draw(batch, categoryMessage, categoryX, categoryY);
                        }
                    } else {
                        // Only display the initial message if the task is in the same category
                        if (currentNode.getTask().isChanceSquare()) {
                            if (!currentNode.getTask().hasBeenOpened()){
                                font.setColor(Color.WHITE);
                                font.getData().setScale(2f); // Larger font size for better visibility
                                String taskMessage = "This is a chance square, press 's' to open.";
                                GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                                float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20; // Right side of the screen with padding
                                float taskY = playerTabY - taskLayout.height - 50; // Positioned lower to avoid overlap
                                font.draw(batch, taskMessage, taskX, taskY);
                            }

                        } else if (!isObjectiveClaimed && (currentPlayer.getCurrentCategory() == null ||
                            currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) {
                            font.setColor(Color.WHITE);
                            font.getData().setScale(2f); // Larger font size for better visibility
                            String taskMessage = "Task Available. Press 's' to Select Task.";
                            GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                            float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20; // Right side of the screen with padding
                            float taskY = playerTabY - taskLayout.height - 50; // Positioned lower to avoid overlap
                            font.draw(batch, taskMessage, taskX, taskY);
                        } else if (isObjectiveClaimed) {
                            // Display the message to give the task to another player
                            font.setColor(Color.WHITE);
                            font.getData().setScale(2f); // Larger font size for better visibility
                            String giveTaskMessage = "The task is of a different objective.\nPress 'g' to give the task to another player.";
                            GlyphLayout giveTaskLayout = new GlyphLayout(font, giveTaskMessage);
                            float giveTaskX = Gdx.graphics.getWidth() - giveTaskLayout.width - 20; // Right side of the screen with padding
                            float giveTaskY = playerTabY - giveTaskLayout.height - 50; // Positioned lower to avoid overlap
                            font.draw(batch, giveTaskMessage, giveTaskX, giveTaskY);
                        }
                    }
                }
            }

            else if (currentNode.getTask() != null){
                // Execution Phase: Show task starting prompts
                if (currentNode.getTask().taskTaken() &&
                    currentNode.getTask().isSelected() && !currentNode.getTask().isActive() && !currentNode.getTask().isCompleted()
                && !currentPlayer.hasActiveTask()) {
                    font.setColor(Color.WHITE);
                    font.getData().setScale(2f);
                    String taskMessage = "Task Available to Start. Press 's' to Start Task.";
                    GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                    float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20;
                    float taskY = playerTabY - taskLayout.height - 50;
                    font.draw(batch, taskMessage, taskX, taskY);
                }
                else if (currentNode.getTask().isActive()){

                    font.setColor(Color.WHITE);
                    font.getData().setScale(2f);
                    String taskMessage = "Task Is Active";
                    GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                    float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20;
                    float taskY = playerTabY - taskLayout.height - 50;
                    font.draw(batch, taskMessage, taskX, taskY);
                }
                else if (currentNode.getTask().isCompleted()){

                    font.setColor(Color.WHITE);
                    font.getData().setScale(2f);
                    String taskMessage = "Task Completed";
                    GlyphLayout taskLayout = new GlyphLayout(font, taskMessage);
                    float taskX = Gdx.graphics.getWidth() - taskLayout.width - 20;
                    float taskY = playerTabY - taskLayout.height - 50;
                    font.draw(batch, taskMessage, taskX, taskY);
                }
            }
        }

        // Show the "You must use all your moves before ending your turn" message if the player tried to end their turn prematurely
        if (attemptedPrematureTurnEnd) {
            font.setColor(Color.RED);
            font.getData().setScale(2f); // Larger font size for better visibility
            String prematureMessage = "You must use all your moves before ending your turn.";
            GlyphLayout prematureLayout = new GlyphLayout(font, prematureMessage);
            float prematureX = Gdx.graphics.getWidth() - prematureLayout.width - 20; // Right side of the screen with padding
            float prematureY = playerTabY - prematureLayout.height - 100; // Positioned below the task selection message
            font.draw(batch, prematureMessage, prematureX, prematureY);
        }

        batch.end();

        drawPlayerIndicators(turn);

        if (playerPopup.isVisible()) {
            showPlayerPopup();
            drawPlayerPop();
        }
    }
    public void renderToolTips() {
        com.main.Tooltip.getInstance().render(uiCamera, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    private void drawPlayerIndicators(int currentTurn) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        final float GLOW_OFFSET = 5f;
        final float circleRadius = 70f;
        final float SPACER = 175f;

        int playerCount = players.size();
        float screenWidth = viewport.getWorldWidth();
        float startX = screenWidth / 2 - ((playerCount - 1) * SPACER / 2);
        float circleY = viewport.getWorldHeight() - circleRadius - 20;
        float textY = circleY - circleRadius - 20;

        for (int i = 0; i < playerCount; i++) {
            float circleX = startX + i * SPACER;


            if (i == currentTurn && playerCount > 1) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(circleX, circleY, circleRadius + GLOW_OFFSET);
            }

            shapeRenderer.setColor(players.get(i).getColor());
            shapeRenderer.circle(circleX, circleY, circleRadius);
        }

        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout();

        for (int i = 0; i < playerCount; i++) {
            float circleX = startX + i * SPACER;
            String playerName = players.get(i).getName();
            int hashIndex = playerName.indexOf('#');
            if (hashIndex != -1) {
                playerName = playerName.substring(0, hashIndex);
            }

            layout.setText(font, playerName);
            float textX = circleX - layout.width / 2;
            font.draw(batch, layout, textX, textY);
        }

        batch.end();
    }

    public void updatePlayerTab(Player player) {
        tab.playerTarget(player);
    }

    public void renderProgressBar(float progress, Color color) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);

        float barWidth = 200;
        float barHeight = 20;
        float x = (Gdx.graphics.getWidth() - barWidth) / 2;
        float y = 0;

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

        batch.begin();
        font.setColor(Color.WHITE);
        String fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
        String currentPos = "Current X: " + players.get(turn).playerCircleX + "Y: " + players.get(turn).playerCircleY;
        String targetPos = "Target X: " + players.get(turn).playerTargetX + "Y: " + players.get(turn).playerTargetY;
        String task = "Task: " + (currentNode.getTask() != null ? currentNode.getTask().getName() : "No Task") + " | Number Of Sub tasks: " + (currentNode.getTask() != null && currentNode.getTask().getSteps() != null ? currentNode.getTask().getSteps().size() : 0);
        String numbTurns = "Number of turns: " + globalTurn;
        String cS = "Current number years " + years + " | Current Season: " + currentSeason;
        String tabExpansion = "Expansion tab: " + tab.isExpanded();

        String nodeId = "Current Node Id " + currentNode.id;
        String occupants = "Current Node Occupants: ";
        for (Player occupant : currentNode.occupants) {
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

    public void renderDebugTravelLine(Player targetPlayer) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        float startX = targetPlayer.playerCircleX;
        float startY = targetPlayer.playerCircleY;

        float targetX = targetPlayer.playerTargetX;
        float targetY = targetPlayer.playerTargetY;

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
            TextButton playerButton = new TextButton(player.getName(), skin);
            playerButton.getStyle().fontColor = Color.WHITE;
            playerButton.setColor(player.getColor());

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

    public void drawPlayerPop() {
        stage.act();
        stage.draw();
    }

    public void hidePlayerPopup(boolean hide) {
        playerPopup.setVisible(hide);
    }

    public void renderPopUp(Node node) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.valueOf("FFFCF2"));

        GlyphLayout layout = new GlyphLayout();
        float padding = 10f;
        float lineSpacing = 5f;

        Array<String> lines = new Array<>();
        if (node.task != null) {
            if (node.task.isChanceSquare()){
                lines.add(node.task.getName());
            }
            else {
                lines.add(node.task.getName());
                lines.add("Category: " + node.task.getCategory());
                String description = node.task.getDescription()
                    .replace("{m}", node.task.getResourceAmountString("Money"))
                    .replace("{p}", node.task.getResourceAmountString("People"));

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
                    lines.add(currentLine.toString().trim());
                }
            }

        } else if (node.isJobCentre) {
            lines.add("Makers Centre");
        }


        float maxTextWidth = 0f;
        for (String line : lines) {
            layout.setText(font, line);
            if (layout.width > maxTextWidth) {
                maxTextWidth = layout.width;
            }
        }

        float rectWidth = maxTextWidth + padding * 2;
        float rectHeight = lines.size * font.getLineHeight() + (lines.size - 1) * lineSpacing + padding * 2;

        Vector3 screenCoords = camera.project(new Vector3(node.x, node.y, 0));

        float rectX = screenCoords.x - rectWidth / 2;
        float rectY = screenCoords.y + node.size + 5;

        shapeRenderer.rect(rectX, rectY, rectWidth, rectHeight);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.BLACK);

        float textY = rectY + rectHeight - padding;
        for (String line : lines) {
            font.draw(batch, line, rectX + padding, textY);
            textY -= font.getLineHeight() + lineSpacing;
        }
        batch.end();
    }

    public void renderCurrentNodeBox(Node node, float boxWidth, float boxHeight, float padding, float tileSize, float rightSidePadding, float centerX, float centerY) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(centerX - boxWidth / 2, padding, boxWidth, boxHeight);

        float rotationAngle = (System.currentTimeMillis() % 3600) / 10f;
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

        batch.begin();
        font.setColor(Color.WHITE);
        String infoText = "Node ID: " + node.id;
        GlyphLayout layout = new GlyphLayout(font, infoText);

        float textX = centerX - layout.width / 2;
        float textY = padding + boxHeight - 20;
        font.draw(batch, infoText, textX, textY);
        batch.end();
    }

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
        font.getData().setScale(4f);
        font.setColor(Color.GOLD);

        GlyphLayout layout = new GlyphLayout(font, weatherAlertText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;

        font.draw(batch, weatherAlertText, x, y + 200);
        font.getData().setScale(1f);
        batch.end();
    }

    private void createConfirmationPopup() {
        confirmationPopup = new Window("Confirm Task", skin);
        confirmationPopup.setVisible(false);
        confirmationPopup.setMovable(false);

        Table contentTable = new Table();
        contentTable.pad(10);

        // Create a label for the message
        Label messageLabel = new Label("", skin);
        messageLabel.setName("message"); // Set the name for finding later
        contentTable.add(messageLabel).row();

        // Create confirm and cancel buttons
        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.setName("confirm"); // Set the name for finding later

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.setName("cancel"); // Set the name for finding later

        contentTable.add(confirmButton).pad(5);
        contentTable.add(cancelButton).pad(5);

        confirmationPopup.add(contentTable);
        confirmationPopup.pack();

        stage.addActor(confirmationPopup);
    }

    public void showConfirmationPopup(Task task, Runnable onConfirm) {
        // Calculate the selecting fee (20% of the task's resources)
        Resource requiredMoney = task.getResources().get(0); // Assuming the first resource is money
        Resource requiredPeople = task.getResources().get(1); // Assuming the second resource is people

        int selectingFeeMoney = (int) (requiredMoney.getAmount() * 0.2);
        int selectingFeePeople = (int) (requiredPeople.getAmount() * 0.2);

        // Create the message with the selecting fee details
        String message = "In order to select this task, there is a selecting fee of 20% of the task's resources. That will be:\n\n"
            + "Money: " + selectingFeeMoney + " ZAR\n"
            + "People: " + selectingFeePeople + "\n\n"
            + "Do you want to proceed?";

        // Find the message label by name
        Label messageLabel = confirmationPopup.findActor("message");
        if (messageLabel != null) {
            // Increase the font size for the message
            messageLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
            font.getData().setScale(1.5f); // Increase font size to 1.5x
            messageLabel.setText(message); // Update the message text
        } else {
            Gdx.app.error("Renderer", "Message label not found in confirmation popup.");
            return;
        }

        // Clear existing listeners
        confirmationPopup.clearListeners();

        // Find the confirm button by name
        TextButton confirmButton = confirmationPopup.findActor("confirm");
        if (confirmButton != null) {
            confirmButton.clearListeners(); // Clear existing listeners
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onConfirm.run(); // Run the confirmation action
                    confirmationPopup.setVisible(false); // Hide the pop-up
                }
            });
        } else {
            Gdx.app.error("Renderer", "Confirm button not found in confirmation popup.");
        }

        // Find the cancel button by name
        TextButton cancelButton = confirmationPopup.findActor("cancel");
        if (cancelButton != null) {
            cancelButton.clearListeners(); // Clear existing listeners
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    confirmationPopup.setVisible(false); // Hide the pop-up
                }
            });
        } else {
            Gdx.app.error("Renderer", "Cancel button not found in confirmation popup.");
        }

        // Increase the size of the pop-up window to accommodate the larger text
        confirmationPopup.pack(); // Recalculate the size of the pop-up
        confirmationPopup.setWidth(500); // Set a fixed width for the pop-up
        confirmationPopup.setHeight(300); // Set a fixed height for the pop-up

        // Position the pop-up in the center of the screen
        confirmationPopup.setPosition(
            (Gdx.graphics.getWidth() - confirmationPopup.getWidth()) / 2,
            (Gdx.graphics.getHeight() - confirmationPopup.getHeight()) / 2
        );

        // Ensure the pop-up is on top of everything
        confirmationPopup.toFront();

        // Make the pop-up visible
        confirmationPopup.setVisible(true);
    }








    public Stage getStage() {
        return stage;
    }
}
