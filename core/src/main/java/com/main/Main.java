package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
    private String weatherAlertText; // Text to display in the alert
    private float weatherAlertTimer; // Timer to control how long the alert is displayed
    private final float WEATHER_ALERT_DURATION = 3f; // Duration of the alert in seconds
    private WeatherInfoScreen weatherInfoScreen;
    private boolean isWeatherInfoScreenVisible = false;

    // Variables for camera movement
    private float dragStartX, dragStartY;
    private boolean dragging;

    private int turn;
    private int currentMoves;
    private int maxMoves;
    private int globalTurn = 0; // used to progress season
    private int years = 0;
    private ArrayList<String> seasons;
    private String gameMode;

    // Ending turn
    private float spaceBarHeldTime = 0;  // To track the time the space bar is held
    private boolean isSpaceBarHeld = false;  // To check if space bar is currently held
    private final float requiredHoldTime = 1f;  // Time to hold in seconds

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
    private float debugDisplayX = 50;  // Initial position
    private float debugDisplayY = 50;
    private float debugDisplayWidth = 100;  // Initial width
    private float debugDisplayHeight = 50;  // Initial height
    private boolean draggingDebugBox = false;
    private float offsetX = 0;
    private float offsetY = 0;

    // node box
    private float boxWidth = 150f;   // Make the box smaller
    private float boxHeight = 200f;  // Make the box smaller
    private float padding = 10f;
    private float tileSize = 80f;    // Adjust the tile size accordingly
    private float rightSidePadding = 10f; // Distance from the right edge of the screen
    private float centerX;
    private float centerY = padding + boxHeight / 2;  // Position the box at the bottom of the screen
    private boolean draggingNodeBox = false;

    // Animation for player moving
    private boolean animatingPlayerMoving = false;
    private float moveSpeed = 4f;  // Adjust this to control the movement speed
    private float circleRadius; // player characters size

    // Tasks
    private ArrayList<Task> task;

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
    Node n1=null;
    Node n2=null;
    Boolean selectingNode = false;

    private Texture gameBackgroundTexture; // Background texture for the main game screen

    public Main(List<Node> nodes) {
        this.nodes = nodes;
        players = PlayerManager.getInstance().getPlayers();
        initializeGame();
        SoundManager.getInstance().loadMusic("background", "audio/backgroundMusic.mp3");
        //SoundManager.getInstance().playMusic("background", true);


        seasons = new ArrayList<>();
        seasons.add("Spring");
        seasons.add("Summer");
        seasons.add("Autumn");
        seasons.add("Winter");

        currentSeason = seasons.get(0);
        this.weatherManager = new WeatherManager();
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

        setupCameras();

        renderer = new Renderer(camera, uiCamera, viewport, circleRadius, players.get(0), this);  // Pass 'this' (Main) to Renderer
    }

    private void setupCameras() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom -= .5f;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), uiCamera);
        viewport.apply();

        circleRadius = nodes.get(0).size / 8f;  // Size of each player indicator circle

    }

    private void initializeGame() {

        // Load the background texture
        gameBackgroundTexture = new Texture(Gdx.files.internal("ui/weatherBackground.png"));

        // load tutorials
        TutorialManager.getInstance().registerTutorial("overview",
            List.of("ui/tutorial/overview.png", "ui/tutorial/overview2.png"));


//        TutorialManager.getInstance().registerTutorial("combat",
//            List.of("ui/tutorial/combat1.png", "ui/tutorial/combat2.png", "ui/tutorial/combat3.png"));


        SoundManager.getInstance().loadSound("moving", "audio/moving.mp3");

        // load tasks
        task = ResourceLoader.loadTask();

        nodes.get(0).setIsJobCentre(true);
        nodes.get(0).updateColour();

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

        Tooltip.getInstance().addTooltip("S","Settings", "ui/toolTips/keyboard_key_p.png", TooltipPosition.BOTTOM_RIGHT, false, false);
        Tooltip.getInstance().addTooltip("MC","Enter Makers Center", "ui/toolTips/mouse_right_button.png", TooltipPosition.BOTTOM_RIGHT);
        Tooltip.getInstance().addTooltip("DR","Click on the dice to roll", TooltipPosition.CLICK_ROLL, true, true);
        Tooltip.getInstance().addTooltip("DP","Click on dice to play",TooltipPosition.CLICK_ROLL, true, true);
        Tooltip.getInstance().addTooltip("AT","Acquire task", "ui/toolTips/keyboard_key_l.png", TooltipPosition.BOTTOM_RIGHT);

        // Steps :
        // 1. setup smaller nodes
        // 2. check through the nodes with tasks that have subtasks (currentNode.getTask() != null && currentNode.getTask().getSteps() != null)
        // 3. then assign number of squares for those subs-tasks and assign them to the node as sub-nodes position them on the line to the connecting node
        // 4. when player gets to the end of sub-node goes to connect node or if on main node they can skip to the other main node assign subtask to those nodes
        for (Node currentNode : nodes) {
            Task task = currentNode.getTask();
            if (task != null && task.getSteps() != null) {
                List<Task> subtasks = task.getSteps();
                currentNode.subNodes = new ArrayList<>();

                // Assign sub-nodes along the line to the first linked node
                if (!currentNode.links.isEmpty() || checkLinkedNode(currentNode)) {
                    Node connectedNode;
                    if(currentNode.links.isEmpty()){
                        connectedNode = getLinkedNode(currentNode); // Assume first link as the main connection

                    }else{
                        connectedNode = currentNode.links.get(0); // Assume first link as the main connection

                    }
                    float dx = (connectedNode.x - currentNode.x) / (subtasks.size() + 1); // x increment
                    float dy = (connectedNode.y - currentNode.y) / (subtasks.size() + 1); // y increment

                    for (int i = 0; i < subtasks.size(); i++) {
                        float subX = currentNode.x + dx * (i + 1); // Increment position along the line
                        float subY = currentNode.y + dy * (i + 1);
                        Node subNode = new Node(subX, subY, currentNode.id + "-sub" + i, currentNode.size / 2);
                        subNode.setTask(subtasks.get(i)); // Assign subtask to sub-node
                        currentNode.subNodes.add(subNode);

                        // Link the sub-node to the main and connected nodes
                        currentNode.addLink(subNode);
                        subNode.addLink(currentNode);
                        subNode.addLink(connectedNode);

                        // Add links to all other sub-nodes for this node
                        for (int j = 0; j < currentNode.subNodes.size(); j++) {
                            Node otherSubNode = currentNode.subNodes.get(j);
                            if (otherSubNode != subNode) { // Don't add a self-link
                                subNode.addLink(otherSubNode);
                                otherSubNode.addLink(subNode);
                            }
                        }
                        subNode.updateColour();
                    }
                }
            }
        }


        for (Player player : players) {
            nodes.get(0).occupy(player);
            player.setCurrentNode(nodes.get(0));
            player.setPlayerNodeCirclePos(circleRadius);
        }

        currentMoves = 0;
        turn = 0;
        currentNode = nodes.get(0);

        centerX = Gdx.graphics.getWidth() - rightSidePadding - boxWidth / 2;  // Move to the right side of the screen

        setupDice();

        dice.setIsVisible(true);
    }

    public void renderWeatherAlert(String weatherAlertText) {
        batch.begin();
        font.getData().setScale(2f); // Increase font size
        font.setColor(Color.WHITE);

        // Calculate the position to center the text
        GlyphLayout layout = new GlyphLayout(font, weatherAlertText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2;

        // Draw the alert text
        font.draw(batch, weatherAlertText, x, y);
        font.getData().setScale(1f); // Reset font size
        batch.end();
    }

    public void setupDice(){
        modelBatch = new ModelBatch();

        // Load textures for each dice face
        Texture[] diceTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            diceTextures[i] = new Texture("ui/dice/normal/dice_face_" + (i + 1) + ".png"); // dice1.png to dice6.png
        }

        // Create the dice
        dice = new Dice(diceTextures);

        // Set up a perspective camera
        camera3d = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Position the camera in front of the dice, facing the front face
        camera3d.position.set(0f, 0f, 3f);  // Move the camera along the Z-axis (3 units away from the dice)
        camera3d.lookAt(0f, 0f, 0f);        // Make the camera look at the center of the dice

        camera3d.near = 0.1f;
        camera3d.far = 100f;
        camera3d.update();

        TutorialManager.getInstance().startTutorial("overview"); // Shows movement tutorial
    }

    public Boolean checkLinkedNode(Node currentNode) {
        for (Node node : nodes) {
            return node.links.contains(currentNode);
        }
        return null;
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
        // Handle input
        handleInput();

        // Render the main game screen
        if (!isWeatherInfoScreenVisible) {
            // Existing rendering logic for the main game screen
            Tooltip.getInstance().clear();
            TutorialManager.getInstance().update();
            renderer.camera.update();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glClearColor(0.0078f, 0.0078f, 0.0078f, 0.71f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // Draw the background texture
            batch.begin();
            batch.draw(gameBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

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
            renderer.renderUI(turn, maxMoves, currentMoves, currentWeather, currentSeason);

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

        } else {
            // Render the weather info screen
            weatherInfoScreen.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Adjust main camera (if needed)
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();

        camera.update();

        // Adjust the UI camera and viewport
        viewport.update(width, height);
        uiCamera.update();

    }

    private void updatePlayerAnimation() {
        if (animatingPlayerMoving) {
            players.get(turn).playerCircleX = MathUtils.lerp(players.get(turn).playerCircleX, players.get(turn).playerTargetX, moveSpeed * Gdx.graphics.getDeltaTime());
            players.get(turn).playerCircleY = MathUtils.lerp(players.get(turn).playerCircleY, players.get(turn).playerTargetY, moveSpeed * Gdx.graphics.getDeltaTime());

            // Check if we have reached the target position (with a small threshold)
            if (Math.abs(players.get(turn).playerCircleX - players.get(turn).playerTargetX) < 1 && Math.abs(players.get(turn).playerCircleY - players.get(turn).playerTargetY) < 1) {

                players.get(turn).playerCircleX = players.get(turn).playerTargetX;  // Ensure it exactly matches
                players.get(turn).playerCircleY = players.get(turn).playerTargetY;
                animatingPlayerMoving = false;  // Stop the animation
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

        // Check if player has moves left
        if (currentMoves + 1 > maxMoves) {
            hasClickedNM = true;
            timeLastNM = 1.5f;
            return;
        }

        // Iterate over all nodes
        for (Node node : nodes) {

            // Check if the clicked position is within the bounds of the sub-nodes
            if (node.subNodes != null) {
                if(handleSubNodeClick(mousePos, node)){
                    return;  // Exit if a sub-node was clicked
                }
            }

            // Check if the clicked position is within the bounds of the main node
            if (handleNodeClick(mousePos, node)) {
                return;  // Exit if a main node was clicked
            }
        }
    }

    // Helper method to handle sub-node click
    private boolean handleSubNodeClick(Vector3 mousePos, Node node) {
        for (Node subNode : node.subNodes) {
            if (mousePos.x >= subNode.x && mousePos.x <= subNode.x + subNode.size &&
                mousePos.y >= subNode.y && mousePos.y <= subNode.y + subNode.size) {

                if (currentNode != subNode && currentNode.containsCurrentPlayer(players.get(turn))) {
                    if (currentNode.links.contains(subNode) || subNode.links.contains(currentNode)) {
                        moveToNode(subNode);
                        Gdx.app.log("DEBUG", "Sub-node changed");
                        SoundManager.getInstance().playSound("moving", 0.3f);

                        return true;  // Exit if sub-node is clicked
                    }
                }
            }
        }
        return false;  // Return false if no sub-node was clicked
    }

    // Helper method to handle main node click
    private boolean handleNodeClick(Vector3 mousePos, Node node) {
        if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
            mousePos.y >= node.y && mousePos.y <= node.y + node.size) {


            if (currentNode != node && currentNode.containsCurrentPlayer(players.get(turn))) {
                if (currentNode.links.contains(node) || node.links.contains(currentNode)) {
                    moveToNode(node);
                    Gdx.app.log("DEBUG", "Node changed");
                    SoundManager.getInstance().playSound("moving", 0.3f);

                    return true;  // Exit if main node is clicked
                }
            }
        }
        return false;  // Return false if no main node was clicked
    }

    // Helper method to move player to a specific node
    private void moveToNode(Node targetNode) {
        currentMoves++;

        // De-occupy current node and occupy the target node
        for (Player occupant : targetNode.occupants) {
            occupant.setPlayerNodeCirclePos(circleRadius);
        }

        currentNode.deOccupy(players.get(turn).getName());
        targetNode.occupy(players.get(turn));
        players.get(turn).setCurrentNode(targetNode);
        currentNode = targetNode;

        players.get(turn).setPlayerNodeTarget(circleRadius);
        animatingPlayerMoving = true;

        if (debugWindow) {
            renderer.renderDebugTravelLine(players.get(turn));
        }
    }

    private void handleInput() {
        // Handle space-bar press logic
        handleSpaceBarInput();

        // Handle tooltips
        if (Tooltip.getInstance().isVisible()) {
            handleToolTips();
        }

        // Handle camera zoom (UP/DOWN keys)
        handleCameraZoom();

        // Toggle debug window visibility
        handleDebugWindowToggle();

        // Handle mouse interactions for dragging boxes or interacting with UI
        handleMouseInput();

        // Handle dragging of the camera
        handleCameraDrag();

        // Handle dice
        handleDice();

        // Temporary Handle
        handleAttachTask();

        handleOptions();

        handleMakersCenter();

        // Handle 'W' key press to toggle weather info screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            isWeatherInfoScreenVisible = !isWeatherInfoScreenVisible;

            if (isWeatherInfoScreenVisible) {
                weatherInfoScreen = new WeatherInfoScreen();
            } else {
                weatherInfoScreen.dispose();
            }
        }
    }

    private void handleToolTips(){
        if (currentNode.isJobCentre) {
            // make an enum for the icons
            Tooltip.getInstance().setVisible("MC", true);
        }else{
            Tooltip.getInstance().setVisible("MC", false);
        }
        if (!dice.isAlreadyRolled() && dice.getIsVisible() && !dice.isRolling()) {
            Tooltip.getInstance().setVisible("DR", true);
        }else{
            Tooltip.getInstance().setVisible("DR", false);
        }

        if(dice.isAlreadyRolled()&& dice.getIsVisible()){
            Tooltip.getInstance().setVisible("DP", true);
        }else{
            Tooltip.getInstance().setVisible("DP", false);
        }
        if(currentNode.getTask() != null){
            Tooltip.getInstance().setVisible("AT", true);
        }else{
            Tooltip.getInstance().setVisible("AT", false);
        }

    }

    private void handleOptions(){

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            // change to options
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(((Game) Gdx.app.getApplicationListener()).getScreen()));
        }
    }

    private void handleMakersCenter() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && currentNode.isJobCentre) {
            // If MakersCenter hasn't been initialized, create it once

            if (makersCenter == null) {
                Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
                makersCenter = new MakersCenter(currentScreen);
            }

            // Switch to the MakersCenter screen
            ((Game) Gdx.app.getApplicationListener()).setScreen(makersCenter);
        }
    }

    private void handleBridge(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Start the bridge creation process
            selectingNode = true;
            n1 = null;
            n2 = null;
        }

        if (selectingNode) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            if (Gdx.input.justTouched()) { // Check for a mouse click
                for (Node node : nodes) {
                    // Check if the clicked position is within the bounds of the node
                    if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
                        mousePos.y >= node.y && mousePos.y <= node.y + node.size) {

                        if (n1 == null) {
                            // Select the first node
                            n1 = node;
                            n1.setHighlighted(true);
                            break;
                        } else if (n1 != node) {
                            // Select the second node
                            n2 = node;
                            n2.setHighlighted(true);
                            break;
                        }
                    }
                }
            }

            if (n1 != null && n2 != null) {
                // Both nodes are selected, create the bridge
                n1.links.add(n2);
                n2.links.add(n1);

                n1.setHighlighted(false);
                n2.setHighlighted(false);

                // Reset the selection state
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
                // Check if the click is inside the bounds of the dice
                if (input.isButtonPressed(0) && isMouseInsideBox(mouseX, mouseY, diceX, diceY, 400, 400)) {
                    dice.onClicked(); // Trigger the dice roll
                    dice.setIsVisible(true);
                }
            } else {
                // Check if the click is inside the bounds of the dice
                if (input.isButtonPressed(0) && isMouseInsideBox(mouseX, mouseY, diceX, diceY, 400, 400)) {
                    dice.setIsVisible(false); // Hide the dice
                    dice.setAlreadyRolled(false); // Reset the roll state

                    // Ensure the dice has finished rolling
                    if (dice.isRolling()) {
                        Gdx.app.log("Dice", "Dice is still rolling. Cannot set maxMoves.");
                        return;
                    }

                    // Get the dice face value
                    int faceValue = dice.getFaceValue();
                    Gdx.app.log("Dice", "Dice Face Value: " + faceValue);

                    // Get the weather and modifier
                    currentWeather = weatherManager.getWeatherForTurn(currentSeason);
                    int maxMovesModifier = weatherManager.getMaxMovesModifier(currentWeather);
                    Gdx.app.log("Weather", "Current Weather: " + currentWeather);
                    Gdx.app.log("Weather", "Max Moves Modifier: " + maxMovesModifier);

                    // Set maxMoves to the rolled face value + modifier
                    maxMoves = faceValue + maxMovesModifier;
                    Gdx.app.log("Game", "Adjusted Max Moves: " + maxMoves);

                    // Ensure maxMoves doesn't go below a minimum value (e.g., 1)
                    maxMoves = Math.max(1, maxMoves);

                    // Set the weather alert text and start the timer
                    weatherAlertText = "Weather: " + currentWeather + " (" + (maxMovesModifier >= 0 ? "+" : "") + maxMovesModifier + " moves)";
                    weatherAlertTimer = WEATHER_ALERT_DURATION;

                    dice.resetFace();
                }
            }
        }
    }


    private void handleAttachTask(){

        if(Gdx.input.isKeyJustPressed(Input.Keys.K)
            && currentNode.getTask() != null
            && !currentNode.getTask().taskTaken()
            && players.size() > 1
        ){

            //screen to pop up with player names and squares in the middle
            renderer.hidePlayerPopup(true);

        }
        if ((Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.L))
            && currentNode.getTask() != null
            && !currentNode.getTask().taskTaken())
        {
            Gdx.app.log("DEBUG", "Trying to attach task");

            // check if task already in it or not
            if (currentNode.getTask().getOwner() == null || currentNode.getTask().getOwner() != null
                && !currentNode.getTask().getOwner().equals(players.get(turn))) {
                players.get(turn).addTask(currentNode.getTask());
                currentNode.getTask().setOwner(players.get(turn));
                currentNode.getTask().setTaken(true);
                renderer.updatePlayerTab(players.get(turn));
                Gdx.app.log("DEBUG", "Task attached");
                renderer.setPlayerTab();
            }else{
                Gdx.app.log("DEBUG", "Task already attached");

            }
        }

        }

    private void handleSpaceBarInput() {
        if (input.isKeyPressed(Input.Keys.SPACE)) {
            isSpaceBarHeld = true;
            spaceBarHeldTime += Gdx.graphics.getDeltaTime();  // Increment hold time

            // Check if the space bar has been held for the required duration
            if (spaceBarHeldTime >= requiredHoldTime) {
                // Cycle through players' turns
                if (turn + 1 < players.size()) {
                    turn++;
                } else {
                    turn = 0;
                    globalTurn ++;

                    // Get the season of the next global turn
                    currentSeason = weatherManager.getSeason(globalTurn);

                }
                // Reset currentMoves for the new turn
                currentNode = players.get(turn).getCurrentNode();
                currentMoves = 0;
                renderer.updatePlayerTab(players.get(turn));
                dice.resetFace();
                dice.setAlreadyRolled(false);
                dice.setRolling(false);
                dice.setIsVisible(true);

                // Reset maxMoves to the dice roll value (if applicable)
                if (dice.isAlreadyRolled()) {
                    maxMoves = dice.getFaceValue() + weatherManager.getMaxMovesModifier(currentWeather);;
                }

                spaceBarHeldTime = 0;
                isSpaceBarHeld = false;
            }
        } else {
            spaceBarHeldTime = 0;
            isSpaceBarHeld = false;
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
        float mouseY = Gdx.graphics.getHeight() - input.getY(); // Adjust for Y-axis inversion

        // Check if the mouse is inside the currentNode box
//        if(isMouseInsideBox(mouseX, mouseY, centerX, centerY, boxWidth, boxHeight)){
//            handleNodeBoxWindowDrag(mouseX, mouseY);
//        }

        // Handle dragging of the debug window
        if (debugWindow) {
            handleDebugWindowDrag(mouseX, mouseY);
        }
    }

    private boolean isMouseInsideBox(float mouseX, float mouseY, float boxCenterX, float boxCenterY, float boxWidth, float boxHeight) {
        // Get the bounds of the box
        float boxLeft = boxCenterX - boxWidth / 2;
        float boxRight = boxCenterX + boxWidth / 2;
        float boxTop = boxCenterY + boxHeight / 2;
        float boxBottom = boxCenterY - boxHeight / 2;

        // Check if mouse is inside the box's bounds
        return mouseX >= boxLeft && mouseX <= boxRight && mouseY >= boxBottom && mouseY <= boxTop;
    }

    private void handleNodeBoxWindowDrag(float mouseX, float mouseY) {
        Vector3 mousePosition = uiCamera.unproject(new Vector3(mouseX, mouseY, 0));

        draggingNodeBox = true;
        offsetX = mousePosition.x - debugDisplayX;
        offsetY = mousePosition.y - debugDisplayY;

        Gdx.app.log("DEBUG", "NodeBox drag");

        // Update position while dragging
        if (draggingNodeBox) {

            centerX =  mousePosition.x - offsetX;
            centerY = mousePosition.y - offsetY;
        }

        // Stop dragging when mouse is released
        if (!input.isButtonPressed(Input.Buttons.LEFT)) {
            draggingNodeBox = false;
        }

    }

    private void handleDebugWindowDrag(float mouseX, float mouseY) {
        if (input.isButtonJustPressed(Input.Buttons.LEFT) &&
            isMouseInsideBox(mouseX, mouseY, debugDisplayX + debugDisplayWidth / 2, debugDisplayY + debugDisplayHeight / 2, debugDisplayWidth, debugDisplayHeight)) {

            // Start dragging the debug box
            draggingDebugBox = true;
            offsetX = mouseX - debugDisplayX;
            offsetY = mouseY - debugDisplayY;
        }

        // Update position while dragging
        if (draggingDebugBox) {
            debugDisplayX = mouseX - offsetX;
            debugDisplayY = mouseY - offsetY;
        }

        // Stop dragging when mouse is released
        if (!input.isButtonPressed(Input.Buttons.LEFT)) {
            draggingDebugBox = false;
        }

        // Handle resizing of the debug window
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        gameBackgroundTexture.dispose(); // Dispose of the background texture
    }


}
