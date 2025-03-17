package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.Tooltip;
import com.main.player.playerTab;
import com.main.tooltips.*;
import com.main.weatherSystem.WeatherManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.Gdx.input;

public class Main implements Screen {
    private ShapeRenderer shapeRenderer;
    private List<Node> nodes;
    private Node currentNode;
    private final List<Player> players;

    // Weather things
    private WeatherManager weatherManager;
    private String currentSeason;
    private String currentWeather;
    private String weatherAlertText;
    private float weatherAlertTimer;
    private final float WEATHER_ALERT_DURATION = 3f;
    private WeatherInfoScreen weatherInfoScreen;
    private boolean isWeatherInfoScreenVisible = false;

    // Variables for camera movement
    private float dragStartX, dragStartY;
    private boolean dragging;

    private int turn;
    private int currentMoves;
    private int maxMoves;
    private int globalTurn = 0;
    private int years = 0;
    private ArrayList<String> seasons;
    private String gameMode;

    // Ending turn
    private float spaceBarHeldTime = 0;
    private boolean isSpaceBarHeld = false;
    private final float requiredHoldTime = 1f;

    // Camera setup
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;

    // Render setup
    private SpriteBatch batch;
    private BitmapFont font;
    private Renderer renderer;

    // Debug window
    private boolean debugWindow = false;
    private float debugDisplayX = 50;
    private float debugDisplayY = 50;
    private float debugDisplayWidth = 100;
    private float debugDisplayHeight = 50;
    private boolean draggingDebugBox = false;
    private float offsetX = 0;
    private float offsetY = 0;

    // node box
    private float boxWidth = 150f;
    private float boxHeight = 200f;
    private float padding = 10f;
    private float tileSize = 80f;
    private float rightSidePadding = 10f;
    private float centerX;
    private float centerY = padding + boxHeight / 2;
    private boolean draggingNodeBox = false;

    // Animation for player moving
    private boolean animatingPlayerMoving = false;
    private float moveSpeed = 4f;
    private float circleRadius;

    // Tasks
    private ArrayList<Task> task;
    private boolean attemptedTaskSelection = false; // Tracks if the player tried to select a task

    // Timer for no movement left text prevent it from being spammed
    private boolean hasClickedNM = false;
    private float timeLastNM;

    // Dice setup
    private Dice dice;
    private ModelBatch modelBatch;
    private PerspectiveCamera camera3d;

    // Makers center setup
    private MakersCenter makersCenter;

    // Test Bridge
    Node n1 = null;
    Node n2 = null;
    Boolean selectingNode = false;

    private Texture gameBackgroundTexture;

    // playertab
    private playerTab tab;

    // to make sure the player doesn't try to end their turn before using all their moves
    private boolean hasMoved = false;
    private boolean attemptedPrematureTurnEnd = false;


    public Main(List<Node> nodes) {
        this.nodes = nodes;
        players = PlayerManager.getInstance().getPlayers();
        initializeGame();
        SoundManager.getInstance().loadMusic("background", "audio/backgroundMusic.mp3");

        seasons = new ArrayList<>();
        seasons.add("Spring");
        seasons.add("Summer");
        seasons.add("Autumn");
        seasons.add("Winter");

        currentSeason = seasons.get(0);
        this.weatherManager = new WeatherManager();

        // Initialize the playerTab
        tab = new playerTab(players.get(0)); // Pass the first player (or current player)
    }

    public Player getCurrentPlayer() {
        return players.get(turn);
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Set up cameras and viewport BEFORE creating the renderer
        setupCameras();

        // Now create the renderer with the initialized cameras and viewport
        renderer = new Renderer(camera, uiCamera, viewport, circleRadius, players.get(0), this);

        // Set the stage as the input processor
        Gdx.input.setInputProcessor(renderer.getStage());
    }

    private void setupCameras() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom -= .5f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), uiCamera);
        viewport.apply();

        // Initialize circleRadius here
        circleRadius = nodes.get(0).size / 8f;
    }

    private void initializeGame() {
        gameBackgroundTexture = new Texture(Gdx.files.internal("ui/weatherBackground.png"));

        TutorialManager.getInstance().registerTutorial("overview",
            List.of("ui/tutorial/overview.png", "ui/tutorial/overview2.png"));

        SoundManager.getInstance().loadSound("moving", "audio/moving.mp3");

        task = ResourceLoader.loadTask();

        nodes.get(0).setIsJobCentre(true);
        nodes.get(0).updateColour();

        // Create the board with the list of tasks
        Board board = new Board(new ArrayList<>(task)); // Pass a copy of the task list
        nodes = board.getNodes();

        // Debug: Check the starting node and other nodes
        Node startingNode = nodes.get(0);
        Gdx.app.log("Debug", "Starting Node Task: " + startingNode.getTask()); // Should be null
        Gdx.app.log("Debug", "Starting Node Color: " + startingNode.color); // Should be yellow

        // Debug: Check all tasks assigned to nodes
        for (int i = 1; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Gdx.app.log("Debug", "Node " + i + " Task: " + (node.getTask() != null ? node.getTask().getName() : "No Task"));
            Gdx.app.log("Debug", "Node " + i + " Category: " + (node.getTask() != null ? node.getTask().getCategory() : "No Category"));
        }

        int taskId = 0;
        while (true) {
            int a = MathUtils.random(nodes.size() - 1);
            if (a != 0 && !nodes.get(a).isJobCentre) {
                if (taskId >= task.size()) {
                    break;
                }
                nodes.get(a).setTask(task.get(taskId));
                nodes.get(a).updateColour();
                taskId++;
            }
        }

        Tooltip.getInstance().addTooltip("S", "Settings", "ui/toolTips/keyboard_key_p.png", TooltipPosition.BOTTOM_RIGHT, false, false);
        Tooltip.getInstance().addTooltip("MC", "Enter Makers Center", "ui/toolTips/mouse_right_button.png", TooltipPosition.BOTTOM_RIGHT);
        Tooltip.getInstance().addTooltip("DR", "Click on the dice to roll", TooltipPosition.CLICK_ROLL, true, true);
        Tooltip.getInstance().addTooltip("DP", "Click on dice to play", TooltipPosition.CLICK_ROLL, true, true);
        Tooltip.getInstance().addTooltip("AT", "Acquire task", "ui/toolTips/keyboard_key_l.png", TooltipPosition.BOTTOM_RIGHT);

        for (Player player : players) {
            nodes.get(0).occupy(player);
            player.setCurrentNode(nodes.get(0));
            player.setPlayerNodeCirclePos(circleRadius);

            // Mark the starting node as visited for all players
            player.markVisited(nodes.get(0));
        }

        currentMoves = 0;
        turn = 0;
        currentNode = nodes.get(0);

        centerX = Gdx.graphics.getWidth() - rightSidePadding - boxWidth / 2;

        setupDice();

        dice.setIsVisible(true);
    }

    public void renderWeatherAlert(String weatherAlertText) {
        batch.begin();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        GlyphLayout layout = new GlyphLayout(font, weatherAlertText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;

        font.draw(batch, weatherAlertText, x, y);
        font.getData().setScale(1f);
        batch.end();
    }

    public void setupDice() {
        modelBatch = new ModelBatch();

        Texture[] diceTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            diceTextures[i] = new Texture("ui/dice/normal/dice_face_" + (i + 1) + ".png");
        }

        dice = new Dice(diceTextures);

        camera3d = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera3d.position.set(0f, 0f, 3f);
        camera3d.lookAt(0f, 0f, 0f);

        camera3d.near = 0.1f;
        camera3d.far = 100f;
        camera3d.update();

        TutorialManager.getInstance().startTutorial("overview");
    }

    public Boolean checkLinkedNode(Node currentNode) {
        for (Node node : nodes) {
            return node.links.contains(currentNode);
        }
        return null;
    }

    public boolean isNodeSelectedByCurrentPlayer(Node node) {
        Player currentPlayer = players.get(turn);
        return node.getTask() != null && currentPlayer.getTasks().contains(node.getTask());
    }

    public Node getLinkedNode(Node currentNode) {
        for (Node node : nodes) {
            if (node.links.contains(currentNode)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (!isWeatherInfoScreenVisible) {
            Tooltip.getInstance().clear();
            TutorialManager.getInstance().update();
            renderer.camera.update();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glClearColor(0.0078f, 0.0078f, 0.0078f, 0.71f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            batch.begin();
            batch.draw(gameBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            renderer.getStage().act(delta);
            renderer.getStage().draw();

            if (!TutorialManager.getInstance().isActive()) {
                if (input.isButtonJustPressed(0) && !dice.getIsVisible()) {
                    nodeClicked();
                }

                updatePlayerAnimation();

                if (hasClickedNM) {
                    timeLastNM -= delta;
                    if (timeLastNM >= 0) {
                        batch.begin();
                        font.draw(batch, "No moves left ... Hold space-bar to end turn", (Gdx.graphics.getWidth() - 200) / 2, (Gdx.graphics.getHeight() - 200) / 2);
                        batch.end();
                    }
                }
            }

            renderer.renderBoard(nodes);
            renderer.renderUI(turn, maxMoves, currentMoves, currentWeather, currentSeason, currentNode, attemptedTaskSelection, hasMoved, attemptedPrematureTurnEnd);

            if (isSpaceBarHeld) {
                float progress = Math.min(spaceBarHeldTime / requiredHoldTime, 1);
                renderer.renderProgressBar(progress, players.get(turn).getColor());
            } else {
                batch.begin();
                font.draw(batch, "Hold space-bar to end turn", (Gdx.graphics.getWidth() - 200) / 2, 20);
                batch.end();
            }

            if (!TutorialManager.getInstance().isActive()) {
                nodeHover();
            }

            if (debugWindow) {
                renderer.renderDebugInfo(debugDisplayX, debugDisplayY, debugDisplayWidth, debugDisplayHeight, currentNode, turn, globalTurn, currentSeason, years);
            }

            camera3d.update();
            dice.update(delta);

            modelBatch.begin(camera3d);
            if (dice.getIsVisible()) {
                dice.render(modelBatch);
            }
            modelBatch.end();

            TutorialManager.getInstance().render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.renderToolTips();

            if (weatherAlertTimer > 0) {
                weatherAlertTimer -= delta;
                renderer.renderWeatherAlert(weatherAlertText);
            }

            // Handle 't' key press to open the PlayerTabScreen
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                Player currentPlayer = players.get(turn);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayerTabScreen(this, currentPlayer));
            }

        } else {
            weatherInfoScreen.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();

        camera.update();

        viewport.update(width, height);
        uiCamera.update();
    }

    private void updatePlayerAnimation() {
        if (animatingPlayerMoving) {
            players.get(turn).playerCircleX = MathUtils.lerp(players.get(turn).playerCircleX, players.get(turn).playerTargetX, moveSpeed * Gdx.graphics.getDeltaTime());
            players.get(turn).playerCircleY = MathUtils.lerp(players.get(turn).playerCircleY, players.get(turn).playerTargetY, moveSpeed * Gdx.graphics.getDeltaTime());

            if (Math.abs(players.get(turn).playerCircleX - players.get(turn).playerTargetX) < 1 && Math.abs(players.get(turn).playerCircleY - players.get(turn).playerTargetY) < 1) {
                players.get(turn).playerCircleX = players.get(turn).playerTargetX;
                players.get(turn).playerCircleY = players.get(turn).playerTargetY;
                animatingPlayerMoving = false;
                Gdx.app.log("Debug", "Animation Finished");
            }
        }
    }

    private void nodeHover() {
        Vector3 mousePos = new Vector3(input.getX(), input.getY(), 0);
        camera.unproject(mousePos);

        for (Node node : nodes) {
            if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
                mousePos.y >= node.y && mousePos.y <= node.y + node.size) {

                if (debugWindow) {
                    renderer.renderDebugTravelLine(players.get(turn));
                }

                renderer.renderPopUp(node);
            }

            if (node.subNodes != null) {
                for (Node subNode : node.subNodes) {
                    if (mousePos.x >= subNode.x && mousePos.x <= subNode.x + subNode.size &&
                        mousePos.y >= subNode.y && mousePos.y <= subNode.y + subNode.size) {

                        if (debugWindow) {
                            renderer.renderDebugTravelLine(players.get(turn));
                        }

                        renderer.renderPopUp(subNode);
                    }
                }
            }
        }
    }

    public void nodeClicked() {
        Vector3 mousePos = new Vector3(input.getX(), input.getY(), 0);
        camera.unproject(mousePos);

        if (currentMoves + 1 > maxMoves) {
            hasClickedNM = true;
            timeLastNM = 1.5f;
            return;
        }

        for (Node node : nodes) {
            if (node.subNodes != null) {
                if (handleSubNodeClick(mousePos, node)) {
                    return;
                }
            }

            if (handleNodeClick(mousePos, node)) {
                return;
            }
        }
    }

    private boolean handleSubNodeClick(Vector3 mousePos, Node node) {
        for (Node subNode : node.subNodes) {
            if (mousePos.x >= subNode.x && mousePos.x <= subNode.x + subNode.size &&
                mousePos.y >= subNode.y && mousePos.y <= subNode.y + subNode.size) {

                if (currentNode != subNode && currentNode.containsCurrentPlayer(players.get(turn))) {
                    if (currentNode.links.contains(subNode) || subNode.links.contains(currentNode)) {
                        moveToNode(subNode);
                        Gdx.app.log("DEBUG", "Sub-node changed");
                        SoundManager.getInstance().playSound("moving", 0.3f);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean handleNodeClick(Vector3 mousePos, Node node) {
        if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
            mousePos.y >= node.y && mousePos.y <= node.y + node.size) {

            Player currentPlayer = players.get(turn);

            // Prevent moving back to a visited node
            if (currentPlayer.hasVisited(node)) {
                Gdx.app.log("DEBUG", "Cannot move back to a visited node.");
                return false;
            }

            if (currentNode != node && currentNode.containsCurrentPlayer(currentPlayer)) {
                if (currentNode.links.contains(node) || node.links.contains(currentNode)) {
                    moveToNode(node);
                    Gdx.app.log("DEBUG", "Node changed");
                    SoundManager.getInstance().playSound("moving", 0.3f);

                    return true;
                }
            }
        }
        return false;
    }

    private void moveToNode(Node targetNode) {
        Player currentPlayer = players.get(turn);

        // Check if the target node has already been visited
        if (currentPlayer.hasVisited(targetNode)) {
            Gdx.app.log("DEBUG", "Cannot move back to a visited node.");
            return; // Prevent the player from moving back
        }

        // Mark the target node as visited
        currentPlayer.markVisited(targetNode);

        currentMoves++;
        hasMoved = true; // Player has moved during their turn

        for (Player occupant : targetNode.occupants) {
            occupant.setPlayerNodeCirclePos(circleRadius);
        }

        currentNode.deOccupy(currentPlayer.getName());
        targetNode.occupy(currentPlayer);
        currentPlayer.setCurrentNode(targetNode);
        currentNode = targetNode;

        currentPlayer.setPlayerNodeTarget(circleRadius);
        animatingPlayerMoving = true;

        // Clear the weather alert text when the user makes a move
        weatherAlertTimer = 0;

        if (debugWindow) {
            renderer.renderDebugTravelLine(currentPlayer);
        }
    }



    private void handleInput() {
        handleSpaceBarInput();

        if (Tooltip.getInstance().isVisible()) {
            handleToolTips();
        }

        handleCameraZoom();

        handleDebugWindowToggle();

        handleMouseInput();

        handleCameraDrag();

        handleDice();

        // Only allow task selection if the player has no moves left
        if (currentMoves >= maxMoves) {
            handleAttachTask();
        }

        handleOptions();

        handleMakersCenter();

        handleTaskRequest(); // Handle task requests

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            isWeatherInfoScreenVisible = !isWeatherInfoScreenVisible;

            if (isWeatherInfoScreenVisible) {
                weatherInfoScreen = new WeatherInfoScreen();
            } else {
                weatherInfoScreen.dispose();
            }
        }
    }

    private void handleToolTips() {
        if (currentNode.isJobCentre) {
            Tooltip.getInstance().setVisible("MC", true);
        } else {
            Tooltip.getInstance().setVisible("MC", false);
        }
        if (!dice.isAlreadyRolled() && dice.getIsVisible() && !dice.isRolling()) {
            Tooltip.getInstance().setVisible("DR", true);
        } else {
            Tooltip.getInstance().setVisible("DR", false);
        }

        if (dice.isAlreadyRolled() && dice.getIsVisible()) {
            Tooltip.getInstance().setVisible("DP", true);
        } else {
            Tooltip.getInstance().setVisible("DP", false);
        }
        if (currentNode.getTask() != null) {
            Tooltip.getInstance().setVisible("AT", true);
        } else {
            Tooltip.getInstance().setVisible("AT", false);
        }
    }

    private void handleOptions() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(((Game) Gdx.app.getApplicationListener()).getScreen()));
        }
    }

    private void handleMakersCenter() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && currentNode.isJobCentre) {
            if (makersCenter == null) {
                Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
                makersCenter = new MakersCenter(currentScreen);
            }

            ((Game) Gdx.app.getApplicationListener()).setScreen(makersCenter);
        }
    }

    public boolean isTaskSelectedByCurrentPlayer(Node node) {
        Player currentPlayer = players.get(turn);
        return node.getTask() != null && currentPlayer.getTasks().contains(node.getTask());
    }

    public boolean isTaskSelectedByAnyPlayer(Node node) {
        if (node.getTask() == null) {
            return false;
        }
        for (Player player : players) {
            if (player.getTasks().contains(node.getTask())) {
                return true;
            }
        }
        return false;
    }

    private void handleBridge() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            selectingNode = true;
            n1 = null;
            n2 = null;
        }

        if (selectingNode) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            if (Gdx.input.justTouched()) {
                for (Node node : nodes) {
                    if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
                        mousePos.y >= node.y && mousePos.y <= node.y + node.size) {

                        if (n1 == null) {
                            n1 = node;
                            n1.setHighlighted(true);
                            break;
                        } else if (n1 != node) {
                            n2 = node;
                            n2.setHighlighted(true);
                            break;
                        }
                    }
                }
            }

            if (n1 != null && n2 != null) {
                n1.links.add(n2);
                n2.links.add(n1);

                n1.setHighlighted(false);
                n2.setHighlighted(false);

                selectingNode = false;
            }
        }
    }

    private void handleDice() {
        if (dice.getIsVisible()) {
            float mouseX = input.getX();
            float mouseY = Gdx.graphics.getHeight() - input.getY();

            Vector3 dicePosition = dice.getPosition();
            Vector3 screenPosition = camera3d.project(dicePosition);

            float diceX = screenPosition.x;
            float diceY = screenPosition.y;

            if (!dice.isAlreadyRolled()) {
                if (input.isButtonPressed(0) && isMouseInsideBox(mouseX, mouseY, diceX, diceY, 400, 400)) {
                    dice.onClicked();
                    dice.setIsVisible(true);
                }
            } else {
                if (input.isButtonPressed(0) && isMouseInsideBox(mouseX, mouseY, diceX, diceY, 400, 400)) {
                    dice.setIsVisible(false);
                    dice.setAlreadyRolled(false);

                    if (dice.isRolling()) {
                        Gdx.app.log("Dice", "Dice is still rolling. Cannot set maxMoves.");
                        return;
                    }

                    int faceValue = dice.getFaceValue();
                    Gdx.app.log("Dice", "Dice Face Value: " + faceValue);

                    currentWeather = weatherManager.getWeatherForTurn(currentSeason);
                    int maxMovesModifier = weatherManager.getMaxMovesModifier(currentWeather);
                    Gdx.app.log("Weather", "Current Weather: " + currentWeather);
                    Gdx.app.log("Weather", "Max Moves Modifier: " + maxMovesModifier);

                    maxMoves = faceValue + maxMovesModifier;
                    Gdx.app.log("Game", "Adjusted Max Moves: " + maxMoves);

                    maxMoves = Math.max(1, maxMoves);

                    weatherAlertText = "Weather: " + currentWeather + " (" + (maxMovesModifier >= 0 ? "+" : "") + maxMovesModifier + " moves)";
                    weatherAlertTimer = WEATHER_ALERT_DURATION;

                    dice.resetFace();
                }
            }
        }
    }





    private void handleTaskRequest() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            Player currentPlayer = players.get(turn);
            Task task = currentNode.getTask();

            if (task != null && !task.taskTaken()) {
                // Check if the current player can select the task
                if (currentPlayer.getCurrentCategory() == null || currentPlayer.getCurrentCategory().equals(task.getCategory())) {
                    // The current player can select the task, so no need to send a request
                    Gdx.app.log("DEBUG", "You can select this task yourself.");
                    return;
                }

                // Find eligible players
                List<Player> eligiblePlayers = new ArrayList<>();
                for (Player player : players) {
                    if (player != currentPlayer && (player.getCurrentCategory() == null || player.getCurrentCategory().equals(task.getCategory()))) {
                        eligiblePlayers.add(player);
                    }
                }

                if (eligiblePlayers.isEmpty()) {
                    // No eligible players, show the "No Eligible Players" screen
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new NoEligiblePlayersScreen(this));
                } else {
                    // Show the PlayerRequestScreen with eligible players
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayerRequestScreen(this, task, eligiblePlayers));
                }
            }
        }
    }




    private void handleAttachTask() {
        // Only allow task selection if the player has no moves left
        if (currentMoves >= maxMoves) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                attemptedTaskSelection = true; // Player attempted to select a task

                if (currentNode.getTask() != null && !currentNode.getTask().taskTaken()) {
                    Player currentPlayer = players.get(turn);
                    Task task = currentNode.getTask();

                    // Check if the player can select this task
                    if (currentPlayer.getCurrentCategory() == null || currentPlayer.getCurrentCategory().equals(task.getCategory())) {
                        // Calculate 20% of the required resources
                        Resource requiredMoney = task.getResources().get(0); // Assuming the first resource is money
                        Resource requiredPeople = task.getResources().get(1); // Assuming the second resource is people

                        int selectingFeeMoney = (int) (requiredMoney.getAmount() * 0.2);
                        int selectingFeePeople = (int) (requiredPeople.getAmount() * 0.2);

                        // Check if the player has enough resources to pay the selecting fee
                        if (currentPlayer.hasEnoughResources(new Resource("Money", selectingFeeMoney))
                            && currentPlayer.getRand2().getAmount() >= selectingFeePeople) {

                            // Show the TaskSelectionScreen
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskSelectionScreen(this, task, () -> {
                                // Deduct the selecting fee and assign the task (only when confirmed)
                                currentPlayer.getRand().deductAmount(selectingFeeMoney); // Deduct money
                                currentPlayer.getRand2().deductAmount(selectingFeePeople); // Deduct people
                                currentPlayer.addTask(task);
                                task.setOwner(currentPlayer);
                                task.setTaken(true); // Mark the task as selected
                                renderer.updatePlayerTab(currentPlayer);
                                Gdx.app.log("DEBUG", "Selecting fee deducted: " + selectingFeeMoney + " ZAR and " + selectingFeePeople + " people");
                                Gdx.app.log("DEBUG", "Task selected but not started");
                                renderer.setPlayerTab();

                                // Refresh the playerTab to reflect the new task
                                tab.playerTarget(currentPlayer);
                            }));
                        } else {
                            Gdx.app.log("DEBUG", "Not enough resources to pay the selecting fee.");
                        }
                    } else {
                        Gdx.app.log("DEBUG", "Cannot select tasks from different categories.");
                    }
                }
            }
        }
    }







    public void resumeGame() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(this); // Return to the main game screen
    }


    private void endTurn() {
        Gdx.app.log("DEBUG", "Ending turn for player: " + players.get(turn).getName());

        Player currentPlayer = players.get(turn);
        if (currentPlayer.getTaskSpeed() > 0) {
            currentPlayer.setTaskSpeed(currentPlayer.getTaskSpeed() - 1); // Decrement task speed
            if (currentPlayer.getTaskSpeed() == 0) {
                // Task completed
                Task completedTask = currentPlayer.getTasks().get(currentPlayer.getTasks().size() - 1);
                currentPlayer.setCommunityMorale(currentPlayer.getCommunityMorale() + completedTask.getCommunityMoraleImpact());
                completedTask.setCompleted(true);
                Gdx.app.log("DEBUG", "Task completed: " + completedTask.getName());
            }
        }

        // Check if all tasks of the current category are complete
        if (currentPlayer.isCurrentCategoryComplete()) {
            currentPlayer.setCurrentCategory(null); // Reset the current category
            Gdx.app.log("DEBUG", "All tasks of the current category are complete. Resetting category.");
        }

        // Reset the list of visited nodes for the current player
        currentPlayer.resetVisitedNodes();

        // Reset the task selection attempt flag
        attemptedTaskSelection = false;

        // Reset the premature turn end attempt flag
        attemptedPrematureTurnEnd = false;

        if (turn + 1 < players.size()) {
            turn++;
        } else {
            turn = 0;
            globalTurn++;
            currentSeason = weatherManager.getSeason(globalTurn);
        }

        // Mark the starting node as visited at the beginning of the turn
        currentNode = players.get(turn).getCurrentNode();
        players.get(turn).markVisited(currentNode); // Mark the starting node as visited

        currentMoves = 0;
        hasMoved = false; // Reset the movement flag at the start of the new turn
        renderer.updatePlayerTab(players.get(turn));
        dice.resetFace();
        dice.setAlreadyRolled(false);
        dice.setRolling(false);
        dice.setIsVisible(true);

        if (dice.isAlreadyRolled()) {
            maxMoves = dice.getFaceValue() + weatherManager.getMaxMovesModifier(currentWeather);
        }

        spaceBarHeldTime = 0;
        isSpaceBarHeld = false;

        // Update the playerTab to reflect the current player's tasks
        tab.playerTarget(players.get(turn));

        // Handle pending tasks at the beginning of the turn
        handlePendingTasks();
    }




    private void handlePendingTasks() {
        Player currentPlayer = players.get(turn);
        List<Task> pendingTasks = currentPlayer.getPendingTasks();

        if (!pendingTasks.isEmpty()) {
            // Show the TaskSelectionScreen for the first pending task
            Task pendingTask = pendingTasks.get(0);
            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskSelectionScreen(this, pendingTask, () -> {
                // If the player confirms, add the task to their task list and deduct the fee
                currentPlayer.addTask(pendingTask);
                pendingTask.setOwner(currentPlayer);
                pendingTask.setTaken(true); // Mark the task as selected
                currentPlayer.removePendingTask(pendingTask); // Remove the task from pending tasks

                // Deduct the selecting fee
                Resource requiredMoney = pendingTask.getResources().get(0); // Assuming the first resource is money
                Resource requiredPeople = pendingTask.getResources().get(1); // Assuming the second resource is people

                int selectingFeeMoney = (int) (requiredMoney.getAmount() * 0.2);
                int selectingFeePeople = (int) (requiredPeople.getAmount() * 0.2);

                currentPlayer.getRand().deductAmount(selectingFeeMoney); // Deduct money
                currentPlayer.getRand2().deductAmount(selectingFeePeople); // Deduct people

                renderer.updatePlayerTab(currentPlayer);
                Gdx.app.log("DEBUG", "Selecting fee deducted: " + selectingFeeMoney + " ZAR and " + selectingFeePeople + " people");
                Gdx.app.log("DEBUG", "Task selected but not started");

                // Return to the main game screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(this);
            }));
        }
    }




    private void handleSpaceBarInput() {
        if (input.isKeyPressed(Input.Keys.SPACE)) {
            // Check if the player is at a dead end (no available moves)
            boolean isDeadEnd = true;
            for (Node linkedNode : currentNode.links) {
                if (!players.get(turn).hasVisited(linkedNode)) {
                    isDeadEnd = false;
                    break;
                }
            }

            // Allow ending the turn if the player hasn't moved, has used all moves, or is at a dead end
            if (!hasMoved || currentMoves >= maxMoves || isDeadEnd) {
                isSpaceBarHeld = true;
                spaceBarHeldTime += Gdx.graphics.getDeltaTime();

                if (spaceBarHeldTime >= requiredHoldTime) {
                    endTurn();
                }
            } else {
                // Player tried to end their turn prematurely
                attemptedPrematureTurnEnd = true;
                Gdx.app.log("DEBUG", "You must use all your moves before ending your turn.");
            }
        } else {
            spaceBarHeldTime = 0;
            isSpaceBarHeld = false;
            attemptedPrematureTurnEnd = false; // Reset the flag when spacebar is released
        }
    }





    private void handleCameraZoom() {
        if (input.isKeyPressed(Input.Keys.UP)) {
            renderer.camera.zoom -= 0.02f;
        } else if (input.isKeyPressed(Input.Keys.DOWN)) {
            renderer.camera.zoom += 0.02f;
        }
    }

    private void handleDebugWindowToggle() {
        if (input.isKeyJustPressed(Input.Keys.D)) {
            debugWindow = !debugWindow;
        }
    }

    private void handleMouseInput() {
        float mouseX = input.getX();
        float mouseY = Gdx.graphics.getHeight() - input.getY();

        if (debugWindow) {
            handleDebugWindowDrag(mouseX, mouseY);
        }
    }



    private boolean isMouseInsideBox(float mouseX, float mouseY, float boxCenterX, float boxCenterY, float boxWidth, float boxHeight) {
        float boxLeft = boxCenterX - boxWidth / 2;
        float boxRight = boxCenterX + boxWidth / 2;
        float boxTop = boxCenterY + boxHeight / 2;
        float boxBottom = boxCenterY - boxHeight / 2;

        return mouseX >= boxLeft && mouseX <= boxRight && mouseY >= boxBottom && mouseY <= boxTop;
    }

    private void handleNodeBoxWindowDrag(float mouseX, float mouseY) {
        Vector3 mousePosition = uiCamera.unproject(new Vector3(mouseX, mouseY, 0));

        draggingNodeBox = true;
        offsetX = mousePosition.x - debugDisplayX;
        offsetY = mousePosition.y - debugDisplayY;

        Gdx.app.log("DEBUG", "NodeBox drag");

        if (draggingNodeBox) {
            centerX = mousePosition.x - offsetX;
            centerY = mousePosition.y - offsetY;
        }

        if (!input.isButtonPressed(Input.Buttons.LEFT)) {
            draggingNodeBox = false;
        }
    }

    private void handleDebugWindowDrag(float mouseX, float mouseY) {
        if (input.isButtonJustPressed(Input.Buttons.LEFT) &&
            isMouseInsideBox(mouseX, mouseY, debugDisplayX + debugDisplayWidth / 2, debugDisplayY + debugDisplayHeight / 2, debugDisplayWidth, debugDisplayHeight)) {

            draggingDebugBox = true;
            offsetX = mouseX - debugDisplayX;
            offsetY = mouseY - debugDisplayY;
        }

        if (draggingDebugBox) {
            debugDisplayX = mouseX - offsetX;
            debugDisplayY = mouseY - offsetY;
        }

        if (!input.isButtonPressed(Input.Buttons.LEFT)) {
            draggingDebugBox = false;
        }
    }

    private void handleCameraDrag() {
        if (input.isButtonPressed(Input.Buttons.LEFT) && !draggingDebugBox) {
            if (!dragging) {
                dragStartX = input.getX();
                dragStartY = input.getY();
                dragging = true;
            } else {
                float deltaX = dragStartX - input.getX();
                float deltaY = dragStartY - input.getY();
                renderer.camera.translate(deltaX * renderer.camera.zoom, deltaY * renderer.camera.zoom);
                dragStartX = input.getX();
                dragStartY = input.getY();
            }
        } else {
            dragging = false;
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        renderer.getStage().dispose(); // Dispose of the stage
        shapeRenderer.dispose();
        gameBackgroundTexture.dispose();
        shapeRenderer.dispose();
        gameBackgroundTexture.dispose();
    }

    public String getCurrentPlayerObjective() {
        Player currentPlayer = players.get(turn);
        return currentPlayer.getCurrentCategory(); // Returns "Financial", "Educational", or "Business"
    }

    public Color getColorForObjective(String objective) {
        if (objective == null) {
            return Color.WHITE; // Default color if no objective is set
        }
        switch (objective) {
            case "Financial":
                return Color.RED;
            case "Educational":
                return Color.GREEN;
            case "Business":
                return Color.BLUE;
            default:
                return Color.WHITE; // Default color for unknown objectives
        }
    }
}
