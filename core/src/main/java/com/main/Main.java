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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.tooltips.*;
import com.main.weatherSystem.WeatherManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.Gdx.input;

public class Main implements Screen {
    private ShapeRenderer shapeRenderer;
    private List<Node> nodes;
    private Node currentNode;
    private final List<Player> players;

    // Tasks
    private ArrayList<Task> task;
    private boolean attemptedTaskSelection = false; // Tracks if the player tried to select a task

    // will store the all the selected tasks for an objective
    private List<Task> selectedEducationTasks = new ArrayList<>();
    private List<Task> selectedFinanceTasks = new ArrayList<>();
    private List<Task> selectedBusinessTasks = new ArrayList<>();
    private List<Task> selectedCommunityTasks = new ArrayList<>();
    private boolean financeObjectiveCanStart = false;
    private boolean hasFinanceObjectiveStarted = false;
    private boolean businessObjectiveCanStart = false;
    private boolean hasBusinessObjectiveStarted = false;
    private boolean educationObjectiveCanStart = false;
    private boolean hasEducationObjectiveStarted = false;
    private boolean communityObjectiveCanStart = false;
    private boolean hasCommunityObjectiveStarted = false;
    private Map<String, Player> objectiveOwners = new HashMap<>(); // Tracks which player owns which objective


    // Weather things
    private WeatherManager weatherManager;
    private String currentSeason;
    private String currentWeather;
    private String weatherAlertText; // Text to display in the alert
    private float weatherAlertTimer; // Timer to control how long the alert is displayed
    private final float WEATHER_ALERT_DURATION = 3f; // Duration of the alert in seconds


    // makers
    private boolean makersRefill = false;
    private float makersZarRefillAmount = 75000;
    private float makersPeopleRefillAmount = 50;
    private float makerAlertTimer; // Timer to control how long the alert is displayed



    // Variables for camera movement
    private float dragStartX, dragStartY;
    private boolean dragging;

    public boolean teleport = false;
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

    // Timer for no movement left text prevent it from being spammed
    private boolean hasClickedNM = false;
    private float timeLastNM;

    // Dice setup
    private Dice dice;
    private ModelBatch modelBatch;
    private PerspectiveCamera camera3d;

    // Makers center setup

    //weather particles
    private List<RainParticle> particles = new ArrayList<>();
    ThunderstormEffect thunderstormEffect;


    public Main(List<Node> nodes) {
        this.nodes = nodes;
        players = PlayerManager.getInstance().getPlayers();
        initializeGame();

        // sound
        SoundManager.getInstance().loadSound("moving", "audio/moving.mp3");
        SoundManager.getInstance().loadSound("click", "audio/click.mp3");
        SoundManager.getInstance().loadSound("notification", "audio/notification.mp3");


        SoundManager.getInstance().loadSound("taskFinished", "audio/taskFinished1.mp3");
        SoundManager.getInstance().loadSound("startingTaskSound", "audio/startingTaskSound.mp3");
        SoundManager.getInstance().loadMusic("rainSound", "audio/rainSound.mp3");
        SoundManager.getInstance().loadMusic("stormSound", "audio/stormSound.mp3");
        SoundManager.getInstance().loadMusic("snowfallSound", "audio/snowfallSound.mp3");
        SoundManager.getInstance().loadMusic("windSound", "audio/windSound.mp3");

        Texture particleTexture = new Texture(Gdx.files.internal("ui/rain.png"));


        for (int i = 0; i < 500; i++) {
            // Create a new IsoParticle and add it to the list
            particles.add(new RainParticle(100, 200, 150, particleTexture));
        }

        GameState.getInstance().setCurrentScreen("MS");

        seasons = new ArrayList<>();
        seasons.add("Spring");
        seasons.add("Summer");
        seasons.add("Autumn");
        seasons.add("Winter");

        currentSeason = seasons.get(0);
        this.weatherManager = new WeatherManager();

       // Gdx.input.setInputProcessor(renderer.getStage());
        // Initialize the playerTab


    }


    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        TutorialManager.getInstance().setBatch(batch);


        font = new BitmapFont();
        font.setColor(Color.WHITE);

        setupCameras();
        thunderstormEffect = new ThunderstormEffect(camera, "Clear", nodes);

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

        GameState.getInstance().setUiCamera(uiCamera);
        GameState.getInstance().setViewport(viewport);
    }

    private void initializeGame() {

        // load tutorials
        TutorialManager.getInstance().registerTutorial("overview",
            List.of("ui/tutorial/howToPlay.png", "ui/tutorial/howToPlay2.png", "ui/tutorial/howToPlay3.png","ui/tutorial/howToPlay4.png"));

        TutorialManager.getInstance().registerTutorial("weather",List.of("ui/tutorial/weather.png", "ui/tutorial/weather2.png"));

        // load tasks
        task = ResourceLoader.loadTask();

        nodes.get(0).setIsJobCentre(true);
        nodes.get(0).updateColour();

        // Create the board with the list of tasks
        //Board board = new Board(new ArrayList<>(task));
        //nodes = board.getNodes();

        // Debug: Check the starting node and other nodes
        Node startingNode = nodes.get(0);
        Gdx.app.log("Debug", "Starting Node Task: " + startingNode.getTask()); // Should be null
        Gdx.app.log("Debug", "Starting Node Colour: " + startingNode.colour); // Should be yellow

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

        Tooltip.getInstance().addTooltip("A", "S","Settings", "ui/toolTips/keyboard_key_p.png", TooltipPosition.BOTTOM_RIGHT, false, false);


        Tooltip.getInstance().addTooltip("MS", "W","Weather Info", "ui/toolTips/keyboard_key_w.png", TooltipPosition.BOTTOM_RIGHT, false, false);
        Tooltip.getInstance().addTooltip("MS","H","Help","ui/toolTips/keyboard_key_h.png", TooltipPosition.BOTTOM_RIGHT, false, false);

        Tooltip.getInstance().addTooltip("MS","DR","Click on the dice to roll", TooltipPosition.CLICK_ROLL, true, true);
        Tooltip.getInstance().addTooltip("MS","DP","Click on dice to play",TooltipPosition.CLICK_ROLL, true, true);

        // Task related
        Tooltip.getInstance().addTooltip("MS","AT","Acquire task", "ui/toolTips/keyboard_key_s.png", TooltipPosition.BOTTOM_RIGHT);
        Tooltip.getInstance().addTooltip("MS","GT","Give task", "ui/toolTips/keyboard_key_g.png", TooltipPosition.BOTTOM_RIGHT);
        Tooltip.getInstance().addTooltip("MS","HT","Help Task", "ui/toolTips/keyboard_key_f.png", TooltipPosition.BOTTOM_RIGHT);
        Tooltip.getInstance().addTooltip("MS","MR","Collect Resources", "ui/toolTips/keyboard_key_c.png", TooltipPosition.BOTTOM_RIGHT);

        Tooltip.getInstance().addTooltip("AMC","EMC","Leave","ui/toolTips/keyboard_key_escape.png", TooltipPosition.BOTTOM_RIGHT, false, false);

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



    public void setupDice(){
        modelBatch = new ModelBatch();

        // Load textures for each dice face
        Texture[] diceTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            diceTextures[i] = new Texture("ui/dice/dice_face_" + (i + 1) + ".png"); // dice1.png to dice6.png
        }

        // Create the dice
        dice = new Dice(diceTextures);

        // Set up a perspective camera
        camera3d = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Position the camera in front of the dice, facing the front face
        camera3d.position.set(0f, 0f, 3f);
        camera3d.lookAt(0f, 0f, 0f);

        camera3d.near = 0.1f;
        camera3d.far = 100f;
        camera3d.update();
        TutorialManager.getInstance().startTutorial("overview");
        TutorialManager.getInstance().addToQueue("weather");

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


        if(GameState.getInstance().isForceColourUpdate()){
            for(Node node : nodes){
                node.updateColour();
            }
            GameState.getInstance().setForceColourUpdate();
        }
        // Render the main game screen
            // Existing rendering logic for the main game screen
            Tooltip.getInstance().clear();
            TutorialManager.getInstance().update();
            renderer.camera.update();

        // Check if any player has run out of resources

        for (Player player : players) {
            if (player.getRand().getAmount() <= 0 || player.getRand2().getAmount() <= 0) {
                // Trigger game-over screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameEndScreen(false));
                return; // Stop rendering the current screen
            }
        }

       if(GameState.getInstance().getCompletedAllCategories() == 4){
           ((Game) Gdx.app.getApplicationListener()).setScreen(new GameEndScreen(true));

       }


        if (financeObjectiveCanStart && !hasFinanceObjectiveStarted) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new StartObjectiveScreen(this, "Financial", () -> {
                // Logic to execute when the objective is confirmed
                Gdx.app.log("DEBUG", "Financial objective confirmed to start");
            }));
            hasFinanceObjectiveStarted = true;
        }
        if (educationObjectiveCanStart && !hasEducationObjectiveStarted) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new StartObjectiveScreen(this, "Educational", () -> {
                // Logic to execute when the objective is confirmed
                Gdx.app.log("DEBUG", "Educational objective confirmed to start");

            }));
            hasEducationObjectiveStarted = true;
        }
        if (businessObjectiveCanStart && !hasBusinessObjectiveStarted) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new StartObjectiveScreen(this, "Business", () -> {
                // Logic to execute when the objective is confirmed
                Gdx.app.log("DEBUG", "Business objective confirmed to start");
            }));
            hasBusinessObjectiveStarted = true;
        }
        if (communityObjectiveCanStart && !hasCommunityObjectiveStarted) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new StartObjectiveScreen(this, "Community", () -> {
                Gdx.app.log("DEBUG", "Community objective confirmed to start");

            }));

            hasCommunityObjectiveStarted = true;

        }

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glClearColor(0.0078f, 0.0078f, 0.0078f, 0.71f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            renderer.getStage().act(delta);

            renderer.getStage().draw();

            if (!TutorialManager.getInstance().isActive()) {
                handleInput();

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

            batch.begin();

        batch.setProjectionMatrix(camera.combined);

        if(!GameState.getInstance().isRemoveWeatherEffects()){
            if (currentWeather != null && (currentWeather.equals("Rainy") || currentWeather.equals("Snow"))) {
                if(!SoundManager.getInstance().isMusicPlaying("rainSound")){
                    SoundManager.getInstance().playMusic("rainSound", false);
                }
                batch.setProjectionMatrix(uiCamera.combined);
                for (RainParticle particle : particles) {
                    particle.update(delta);
                    particle.draw(batch);
                }
            }else{
                SoundManager.getInstance().pauseMusic("rainSound");
            }

            if (currentWeather != null &&currentWeather.equals("Thunderstorms")) {
                if(!SoundManager.getInstance().isMusicPlaying("stormSound")){
                    SoundManager.getInstance().playMusic("stormSound", false);
                }

                    thunderstormEffect.update(delta, currentWeather);
                    thunderstormEffect.render(batch, renderer.camera.zoom);

            }else{
                SoundManager.getInstance().pauseMusic("stormSound");
            }
            if (currentWeather != null &&currentWeather.equals("Cloudy") ) {
                if(!SoundManager.getInstance().isMusicPlaying("windSound")){
                    SoundManager.getInstance().playMusic("windSound", false);
                }
                thunderstormEffect.update(delta, currentWeather);
                thunderstormEffect.render(batch, renderer.camera.zoom);
            }else{
                SoundManager.getInstance().stopMusic("windSound");

            }
            if (currentWeather != null &&currentWeather.equals("Partly Cloudy")) {
                if(!SoundManager.getInstance().isMusicPlaying("windSound")){
                    SoundManager.getInstance().playMusic("windSound", false);
                }
                thunderstormEffect.update(delta, currentWeather);
                thunderstormEffect.render(batch, renderer.camera.zoom);
            }else{
                SoundManager.getInstance().stopMusic("windSound");

            }
        }

        batch.setProjectionMatrix(uiCamera.combined);

        batch.end();

            renderer.renderUI(turn, maxMoves, currentMoves, currentWeather, currentSeason, currentNode, attemptedTaskSelection);

            if (isSpaceBarHeld) {
                float progress = Math.min(spaceBarHeldTime / requiredHoldTime, 1);
                renderer.renderProgressBar(progress, players.get(turn).getColour());
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

            TutorialManager.getInstance().render();
            Tooltip.getInstance().render(uiCamera, viewport.getWorldWidth(), viewport.getWorldHeight());


        if (weatherAlertTimer > 0) {
                weatherAlertTimer -= delta;
                renderer.renderWeatherAlert(weatherAlertText);
            }
        if(makerAlertTimer > 0){
            makerAlertTimer -= delta;
            renderer.renderMakersAlert();
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

            // Check if the clicked position is within the bounds of the main node
            if (handleNodeClick(mousePos, node)) {
                return;  // Exit if a main node was clicked
            }
        }
    }

    private boolean handleNodeClick(Vector3 mousePos, Node node) {
        if (mousePos.x >= node.x && mousePos.x <= node.x + node.size &&
            mousePos.y >= node.y && mousePos.y <= node.y + node.size) {

            if(teleport){
                SoundManager.getInstance().playSound("moving", 0.3f);

                moveToNode(node);
            }
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


    private boolean notSingleAttached(Node currentNode, Node targetNode){

        int counter = 0;
        for(Node node : nodes){
            if(node.links.contains(targetNode)){
                counter++;
            }
        }

        Gdx.app.log("DEBUG", "Node " + counter + " not single attached");

        if(counter == 0){
            return true;
        }else{
            return false;

        }
    }

    private void moveToNode(Node targetNode) {
        Player currentPlayer = players.get(turn);

        // Check if the target node has already been visited
        // Check if the current node is only attached to one other node
        if (!notSingleAttached(currentNode, targetNode) && currentPlayer.hasVisited(targetNode)) {
            Gdx.app.log("DEBUG", "Cannot move back to a visited node.");
            return; // Prevent the player from moving back
        }

        // Mark the target node as visited
        currentPlayer.markVisited(currentNode);

        // Proceed with move
        currentMoves++;
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

        // Task's

        if(currentMoves >= maxMoves){
            handleAttachTask();
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                handleTaskRequest();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
                handlePlayerTaskHelpScreen();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C) && makersRefill && currentNode.isJobCentre){
            players.get(turn).getRand().addAmount(makersZarRefillAmount);
            players.get(turn).getRand2().addAmount(makersPeopleRefillAmount);
            makersRefill = false;

        }

        handlePlayerTaskScreen();

        handleOptions();

        handleToolTipInput();

    }

    private void handlePlayerTaskHelpScreen() {

        Player currentPlayer = players.get(turn);
        if (currentNode.getTask() != null && currentNode.getTask().isActive() &&
                (currentPlayer.getCurrentCategory() == null ||
                        !currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) {
            Gdx.app.log("DEBUG", "Help screen");
            Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayerTaskHelpScreen(currentScreen, currentNode.getTask(), currentPlayer));
        }

    }

    private void handlePlayerTaskScreen(){

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            GameState.getInstance().setCurrentScreen("PTS");
            Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayerTabScreen(currentScreen));
        }
    }

    private void handleToolTips(){

        Player currentPlayer = players.get(turn);
        boolean isObjectiveClaimed = true;
        if(currentNode.getTask() != null){
            isObjectiveClaimed = objectiveOwners.containsKey(currentNode.getTask().getCategory()) && objectiveOwners.get(currentNode.getTask().getCategory()) != players.get(turn);
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
        if((!isObjectiveClaimed && (currentPlayer.getCurrentCategory() == null ||
            currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) && !currentNode.getTask().isActive() && !currentPlayer.hasActiveTask()){
            if(currentNode.getTask().getCategory().contains("CHANCE")){
                Tooltip.getInstance().updateText("AT", "Open Chance");
            }
            else if(currentPlayer.isObjectiveStarted()){
                Tooltip.getInstance().updateText("AT", "Start Task");
            }else{
                Tooltip.getInstance().updateText("AT", "Acquire Task");
            }
            Tooltip.getInstance().setVisible("AT", true);
        }else{
            Tooltip.getInstance().setVisible("AT", false);
        }if (currentNode.getTask() != null &&
                currentNode.getTask().getCategory() != null &&
                (currentNode.getTask().getCategory() != currentPlayer.getCurrentCategory()) &&
                currentPlayer.getCurrentCategory() != null &&
                PlayerManager.getInstance().getPlayers().size() > 1 &&
                !currentNode.getTask().isActive() &&
                !currentPlayer.currentCategory.equals(currentNode.getTask().getCategory()) ) {

            boolean s = false;
            if (currentNode.getTask() != null) {
                String taskCategory = currentNode.getTask().getCategory();
                s = objectiveOwners.containsKey(taskCategory) &&
                        objectiveOwners.get(taskCategory) != players.get(turn);
            }
            Tooltip.getInstance().setVisible("GT", !s);
        } else {
            Tooltip.getInstance().setVisible("GT", false);
        }


        if (currentNode.getTask() != null && currentNode.getTask().isActive() &&
                (currentPlayer.getCurrentCategory() == null ||
                        !currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) {
            Tooltip.getInstance().setVisible("HT", true);
        } else {
            Tooltip.getInstance().setVisible("HT", false);
        }

        if (makersRefill && currentNode.isJobCentre) {
            Tooltip.getInstance().setVisible("MR", true);
        }else{
            Tooltip.getInstance().setVisible("MR", false);
        }
    }

    private void handleOptions(){

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            // change to options
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(((Game) Gdx.app.getApplicationListener()).getScreen()));
        }
    }

    private void handleToolTipInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if(TutorialManager.getInstance().getOff()){
                TutorialManager.getInstance().setTemp();
                TutorialManager.getInstance().setOff();
            }
            TutorialManager.getInstance().startTutorial("weather");
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
            if(TutorialManager.getInstance().getOff()){
                TutorialManager.getInstance().setTemp();
                TutorialManager.getInstance().setOff();
            }
            TutorialManager.getInstance().startTutorial("overview");
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

    private void handleChanceSquare(Node node) {
        Task chanceTask = node.getTask();
        if (chanceTask != null && chanceTask.isChanceSquare()) {
            if (!chanceTask.hasBeenOpened()){

                // Add the resources to the player's resources
                for (Resource resource : chanceTask.getResources()) {
                    if (resource.getType().equals("Money")) {
                        players.get(turn).getRand().addAmount(resource.getAmount());
                    } else if (resource.getType().equals("People")) {
                        players.get(turn).getRand2().addAmount(resource.getAmount());
                    }
                }

                // Mark the chance square as opened
                chanceTask.setHasBeenOpened(true);

                // Show the chance square screen
                Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ChanceSquareScreen(currentScreen, chanceTask));

                // Do not mark the chance square as taken, so it can be reused
                Gdx.app.log("DEBUG", "Chance square opened. Resources added to player.");
            }

        }
    }

    private void handleAttachTask() {
        Player currentPlayer = players.get(turn);
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                attemptedTaskSelection = true;

                if (currentNode.getTask() != null && !currentNode.getTask().taskTaken()) {
                    Gdx.app.debug("DEBUG", "Task not taken and not Null.");
                    if (currentNode.getTask().isChanceSquare()){
                        handleChanceSquare(currentNode);
                    }
                    else
                    {

                        Task task = currentNode.getTask();
                        String taskCategory = task.getCategory();

                        // Check if the player has an active task
                        if (currentPlayer.hasActiveTask()) {
                            Gdx.app.log("DEBUG", "You already have an active task. Complete it before starting another.");
                            return;
                        }

                        // Check if the objective is already claimed by another player
                        if (objectiveOwners.containsKey(taskCategory) && objectiveOwners.get(taskCategory) != currentPlayer) {

                            Gdx.app.log("DEBUG", "Objective : "+ taskCategory + " is already claimed by another player");
                            return; // Exit if the objective is claimed by someone else
                        }

                        // If the objective is not claimed, claim it for the current player
                        if (!objectiveOwners.containsKey(taskCategory) && currentPlayer.currentCategory == null || currentPlayer.currentCategory == taskCategory) {
                            objectiveOwners.put(taskCategory, currentPlayer);
                            Gdx.app.log("DEBUG", "Player " + currentPlayer.getName() + " has claimed the " + taskCategory + " objective.");
                        }

                        // Check if the task belongs to the player's current objective category
                        if (currentPlayer.getCurrentCategory() == null || currentPlayer.getCurrentCategory().equals(taskCategory)) {
                            if (!currentPlayer.isObjectiveStarted()) {
                                // Selection Phase: Select the task

                                // Show the TaskSelectionScreen for confirmation
                                ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskSelectionScreen(this, task, () -> {
                                    // Deduct the selecting fee and assign the task

                                    // Check the resources and deduct the selecting fee
                                    for (int i = 0; i < task.getResources().size(); i++) {

                                        String resourceType = task.getResources().get(i).getType();
                                        double amount = task.getResources().get(i).getAmount();

                                        if (resourceType.equals("Money")) {
                                            currentPlayer.getRand().deductAmount((int) (task.getResources().get(0).getAmount() * 0.2));
                                        } else if (resourceType.equals("People")) {
                                            currentPlayer.getRand2().deductAmount((int) (task.getResources().get(1).getAmount() * 0.2));
                                        }
                                    }

                                    currentPlayer.addTask(task);
                                    task.setOwner(currentPlayer);
                                    task.setTaken(true);

                                    // Log the selected task in the appropriate list
                                    switch (taskCategory) {
                                        case "Educational":
                                            selectedEducationTasks.add(task);
                                            Gdx.app.log("DEBUG", "Education task added number" + selectedEducationTasks.size());
                                            break;
                                        case "Financial":
                                            selectedFinanceTasks.add(task);
                                            Gdx.app.log("DEBUG", "Finance task added number" + selectedFinanceTasks.size());
                                            break;
                                        case "Business":
                                            selectedBusinessTasks.add(task);
                                            Gdx.app.log("DEBUG", "Business task added number" + selectedBusinessTasks.size());
                                            break;
                                        case "Community":
                                            selectedCommunityTasks.add(task);
                                            Gdx.app.log("DEBUG", "Community task added number" + selectedCommunityTasks.size());
                                            break;
                                    }

                                    Gdx.app.log("DEBUG", "Educational" + selectedEducationTasks.size());

                                    // Check if all tasks for the objective have been selected
                                    if (selectedEducationTasks.size() == 10 && taskCategory.equals("Educational")) {
                                        educationObjectiveCanStart = true;
                                        Gdx.app.log("DEBUG", "Education Objective Should Start");
                                    } else if (selectedFinanceTasks.size() == 10 && taskCategory.equals("Financial")) {
                                        financeObjectiveCanStart = true;
                                        Gdx.app.log("DEBUG", "Finance Objective Should Start");
                                    } else if (selectedBusinessTasks.size() == 10 && taskCategory.equals("Business")) {
                                        businessObjectiveCanStart = true;
                                        Gdx.app.log("DEBUG", "Business Objective Should Start");
                                    }
                                    else if (selectedCommunityTasks.size() == 10 && taskCategory.equals("Community")) {
                                        communityObjectiveCanStart = true;
                                        Gdx.app.log("DEBUG", "Community Objective Should Start");
                                    }

                                    Gdx.app.log("DEBUG", "Task selected but not started.");
                                }));

                            }


                        }
                    }

                }

                // starting a task
                else if (currentNode.getTask() != null && currentNode.getTask().taskTaken()){
                    if (currentNode.getTask().getCategory() != null && currentNode.getTask().getCategory().equals("Educational") &&
                        currentPlayer.getCurrentCategory().equals("Educational") && educationObjectiveCanStart){
                        if (!currentNode.getTask().isCompleted() && !currentNode.getTask().isActive() &&
                            !currentPlayer.hasActiveTask()) {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskStartConfirmationScreen(this, currentNode.getTask(), () -> {
                                currentPlayer.startTask(currentNode.getTask());
                            }));
                        }
                        else {
                            Gdx.app.log("DEBUG", "Task cannot be started as it already has been");
                        }
                    }
                    if (currentNode.getTask().getCategory() != null &&
                            currentNode.getTask().getCategory().equals("Financial") &&
                            currentPlayer.getCurrentCategory() != null &&
                            currentPlayer.getCurrentCategory().equals("Financial") &&
                            financeObjectiveCanStart) {

                        // if the task hasn't been started and player doesn't have active task
                        if (currentNode.getTask() != null && !currentNode.getTask().isCompleted() && !currentNode.getTask().isActive() &&
                            !currentPlayer.hasActiveTask()) {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskStartConfirmationScreen(this, currentNode.getTask(), () -> {
                                currentPlayer.startTask(currentNode.getTask());
                            }));
                        }
                        else {
                            Gdx.app.log("DEBUG", "Task cannot be started as it already has been");
                        }
                    }
                    if (currentNode.getTask() != null && currentNode.getTask().getCategory().equals("Business") &&
                        currentPlayer.getCurrentCategory().equals("Business") && businessObjectiveCanStart){
                        // if the task hasn't been started and player doesn't have active task
                        if (!currentNode.getTask().isCompleted() && !currentNode.getTask().isActive() &&
                            !currentPlayer.hasActiveTask()) {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskStartConfirmationScreen(this, currentNode.getTask(), () -> {
                                currentPlayer.startTask(currentNode.getTask());
                            }));
                        }
                        else {
                            Gdx.app.log("DEBUG", "Task cannot be started as it already has been");
                        }
                    }
                    if (currentNode.getTask() != null && currentNode.getTask().getCategory().equals("Community") &&
                        currentPlayer.getCurrentCategory().equals("Community") && communityObjectiveCanStart){
                        // if the task hasn't been started and player doesn't have active task
                        if (!currentNode.getTask().isCompleted() && !currentNode.getTask().isActive() &&
                            !currentPlayer.hasActiveTask()) {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TaskStartConfirmationScreen(this, currentNode.getTask(), () -> {
                                currentPlayer.startTask(currentNode.getTask());
                            }));
                        }
                        else {
                            Gdx.app.log("DEBUG", "Task cannot be started as it already has been");
                        }
                    }

                }

        }
    }

    private void handleTaskRequest() {
        Player currentPlayer = players.get(turn);
        Task task = currentNode.getTask();

        // Check if the objective is already claimed by another player
        boolean isObjectiveClaimed = false;
        if (currentNode.getTask() != null) {
            String taskCategory = currentNode.getTask().getCategory();
            isObjectiveClaimed = objectiveOwners.containsKey(taskCategory) &&
                objectiveOwners.get(taskCategory) != players.get(turn);
        }

        if (task != null && !task.taskTaken()) {
            // Check if the current player can select the task
            if (!isObjectiveClaimed && (currentPlayer.getCurrentCategory() == null ||
                currentPlayer.getCurrentCategory().equals(currentNode.getTask().getCategory()))) {
                // The current player can select the task, so no need to send a request
                Gdx.app.log("DEBUG", "You can select this task yourself.");
                return;
            }

            // Find eligible players (players who can select this task)
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

                double requiredMoney = 0.0;
                double requiredPeople = 0.0;
                // Check the resources and deduct the selecting fee
                for (int i = 0; i < pendingTask.getResources().size(); i++) {

                    String resourceType = pendingTask.getResources().get(i).getType();
                    double amount = pendingTask.getResources().get(i).getAmount();

                    if (resourceType.equals("Money")) {
                        requiredMoney = amount;
                    } else if (resourceType.equals("People")) {
                        requiredPeople = amount;
                    }
                }
                int selectingFeeMoney = (int) (requiredMoney * 0.2);
                int selectingFeePeople = (int) (requiredPeople * 0.2);
                currentPlayer.getRand().deductAmount(selectingFeeMoney); // Deduct money
                currentPlayer.getRand2().deductAmount(selectingFeePeople); // Deduct people

                Gdx.app.log("DEBUG", "Selecting fee deducted: " + selectingFeeMoney + " ZAR and " + selectingFeePeople + " people");
                Gdx.app.log("DEBUG", "Task selected but not started");

                // Return to the main game screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(this);
            }));
        }
    }

    private void endTurn() {
        Gdx.app.log("DEBUG", "Ending turn for player: " + players.get(turn).getName());

        Player currentPlayer = players.get(turn);

        if (currentPlayer.getTaskSpeed() > 0) {
            currentPlayer.setTaskSpeed(currentPlayer.getTaskSpeed() - 1); // Decrement task speed
            if (currentPlayer.getTaskSpeed() == 0) {
                // Task completed
                Task completedTask = currentPlayer.getTasks().get(currentPlayer.getTasks().size() - 1);
                completedTask.setCompleted(true);
                Gdx.app.log("DEBUG", "Task completed: " + completedTask.getName());
            }
        }

        // Progress the active task (if any)
        if (currentPlayer.hasActiveTask()) {
            currentPlayer.progressTask();
        }

        // Check if all tasks of the current category are complete
        if (currentPlayer.isCurrentCategoryComplete()) {


            Gdx.app.log("DEBUG", "All tasks of the current category are complete. Resetting category.");
        }

        // Reset the list of visited nodes for the current player
        currentPlayer.resetVisitedNodes();

        // Reset the task selection attempt flag
        attemptedTaskSelection = false;

        if (turn + 1 < players.size()) {
            turn++;
            PlayerManager.getInstance().updateCurrentPlayer(turn);
        } else {
            turn = 0;
            PlayerManager.getInstance().updateCurrentPlayer(turn);
            makersRefill = true;
            makerAlertTimer = WEATHER_ALERT_DURATION;
            SoundManager.getInstance().playSound("notification");
            globalTurn++;
            currentSeason = weatherManager.getSeason(globalTurn);
        }

        // Mark the starting node as visited at the beginning of the turn
        currentNode = players.get(turn).getCurrentNode();
        players.get(turn).markVisited(currentNode); // Mark the starting node as visited

        currentMoves = 0;
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


        GameState.getInstance().updateData(globalTurn, years);

        // Handle pending tasks at the beginning of the turn
        handlePendingTasks();
    }

    private void handleSpaceBarInput() {
        if (input.isKeyPressed(Input.Keys.SPACE)) {
            isSpaceBarHeld = true;
            spaceBarHeldTime += Gdx.graphics.getDeltaTime();  // Increment hold time

            // Check if the space bar has been held for the required duration
            if (spaceBarHeldTime >= requiredHoldTime) {
                // Cycle through players' turns
               endTurn();
            }
        } else {
            spaceBarHeldTime = 0;
            isSpaceBarHeld = false;
        }
    }

    private void handleCameraZoom() {
        if (input.isKeyPressed(Input.Keys.UP)) {
            if (renderer.camera.zoom >= 0.25){
                renderer.camera.zoom -= 0.02f;
            }
        } else if (input.isKeyPressed(Input.Keys.DOWN)) {
            if (renderer.camera.zoom <= 0.6){
                renderer.camera.zoom += 0.02f;
            }

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
    }


}
