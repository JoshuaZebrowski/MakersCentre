package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

public class GameSetup implements Screen {
    private Stage stage;
    private Skin skin;
    private TextButton startButton;
    private TextButton plusButton;
    private TextButton minusButton;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Vector2> playerPositions; // List to manage positions of the circles
    private ArrayList<TextField> nameFields; // List to store TextFields for player names
    private float circleRadius = 50; // Circle radius
    private float spacing = 150; // Horizontal spacing between circles and buttons
    private float yPosition = Gdx.graphics.getHeight() / 2; // Y position of the circles and buttons
    private float yOffset = 100; // Offset for positioning the TextField below the circle

    private List<Task> tasks; // List to store tasks loaded from JSON

    public void show() {
        // Load tasks from JSON
        tasks = ResourceLoader.loadTask(); // Assuming ResourceLoader.loadTask() returns a List<Task>

        // Set up the Stage and Skin (used for UI elements like buttons)
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize ShapeRenderer for drawing the circles
        shapeRenderer = new ShapeRenderer();

        // Create the player list (starts with one player)
        playerPositions = new ArrayList<>();
        nameFields = new ArrayList<>();

        // Initialize first player and their position
        PlayerManager.getInstance().addPlayer(new Player("Player 1", randomColor()));

        playerPositions.add(new Vector2(100, yPosition));

        // Initialize the first TextField for the player's name
        TextField playerNameField = new TextField("Player 1", skin);
        playerNameField.setPosition(100, yPosition - yOffset); // Positioning below the circle
        nameFields.add(playerNameField);

        // Create the Start Button
        startButton = new TextButton("Start", skin);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2, yPosition - 150);

        // Add a click listener for the button
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // Switch to the Main screen (the actual game screen)

                boolean a = true;

                for (int i = 0; i < PlayerManager.getInstance().getPlayers().size(); i++) {
                    if (nameFields.get(i).getText().isEmpty()) { // Use isEmpty() instead of == ""
                        a = false;
                    }
                }

                if (a) {
                    for (int i = 0; i < PlayerManager.getInstance().getPlayers().size(); i++) {
                        PlayerManager.getInstance().getPlayers().get(i).setName(nameFields.get(i).getText() + "#" + i);
                    }

                    // Create the board with the list of tasks
                    Board board = new Board(new ArrayList<>(tasks)); // Pass the tasks to the Board
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new Main(board.getNodes()));
                }
            }
        });

        // Create the Plus Button
        plusButton = new TextButton("+", skin);
        plusButton.setSize(50, 50);
        plusButton.setPosition(playerPositions.get(0).x + circleRadius + 20, yPosition);

        // Create the minus Button
        minusButton = new TextButton("-", skin);
        minusButton.setSize(50, 50);
        minusButton.setPosition(playerPositions.get(0).x + circleRadius + 20, yPosition-50);

        if(PlayerManager.getInstance().getPlayers().size() == 1){
            minusButton.setVisible(false);
        }else{
            minusButton.setVisible(true);
        }

        // Add a click listener for the button
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if(PlayerManager.getInstance().getPlayers().size() == 1){
                    minusButton.setVisible(false);
                }else{
                    minusButton.setVisible(true);
                }

                if (PlayerManager.getInstance().getPlayers().size() < 4) {
                    // Add a new player to the list
                    Player newPlayer = new Player("Player " + (PlayerManager.getInstance().getPlayers().size() + 1), randomColor());
                    PlayerManager.getInstance().getPlayers().add(newPlayer);

                    // Update position for the new player
                    float newX = playerPositions.get(playerPositions.size() - 1).x + circleRadius * 2 + spacing;
                    playerPositions.add(new Vector2(newX, yPosition));

                    // Update the name field for the new player
                    TextField newPlayerNameField = new TextField("Player " + (PlayerManager.getInstance().getPlayers().size()), skin);
                    newPlayerNameField.setPosition(newX, yPosition - yOffset); // Position the TextField below the circle
                    nameFields.add(newPlayerNameField);

                    // Update the plus button position
                    plusButton.setPosition(newX + circleRadius + 20, yPosition);
                    minusButton.setPosition(newX + circleRadius + 20, yPosition-50);

                    if(PlayerManager.getInstance().getPlayers().size() == 4){
                        plusButton.setVisible(false);
                    }
                }
            }
        });

        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (PlayerManager.getInstance().getPlayers().size() > 1) {
                    if(PlayerManager.getInstance().getPlayers().size() == 1){
                        minusButton.setVisible(false);
                    }else{
                        minusButton.setVisible(true);
                    }

                    // Remove the last player from the list
                    playerPositions.remove(PlayerManager.getInstance().getPlayers().size() - 1);
                    nameFields.remove(PlayerManager.getInstance().getPlayers().size() - 1);
                    PlayerManager.getInstance().getPlayers().remove(PlayerManager.getInstance().getPlayers().size() - 1);

                    float newX = playerPositions.get(playerPositions.size() - 1).x + circleRadius * 2 + spacing;

                    // Update the plus button position
                    plusButton.setPosition(newX + circleRadius + 20, yPosition);
                    minusButton.setPosition(newX + circleRadius + 20, yPosition-50);

                    if(PlayerManager.getInstance().getPlayers().size() < 4){
                        plusButton.setVisible(true);
                    }
                }
            }
        });

        // Add the start, plus buttons, and name fields to the stage
        stage.addActor(startButton);
        stage.addActor(plusButton);
        stage.addActor(minusButton);
        stage.addActor(nameFields.get(0)); // Add the first player's name field

        // Add listeners for circle clicks to open color picker
        ClickListener colorPickerListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Debug", "Circle Clicked");
                for (int i = 0; i < PlayerManager.getInstance().getPlayers().size(); i++) {
                    float playerX = playerPositions.get(i).x;
                    float playerY = playerPositions.get(i).y;
                    final Player player = PlayerManager.getInstance().getPlayers().get(i);

                    if (isClickInsideCircle(x, y, playerX, playerY, circleRadius)) {
                        // Show color picker for the clicked player
                        showColorPicker(player);
                    }
                }
            }
        };
        stage.addListener(colorPickerListener);
    }

    private boolean checkPlayerTakenColour(String hex){
        for (Player player : PlayerManager.getInstance().getPlayers()) {
            if(player.color.equals(Color.valueOf(hex))){
                return true;
            }
        }
        return false;
    }

    private boolean isClickInsideCircle(float clickX, float clickY, float circleX, float circleY, float radius) {
        // Check if the click is inside the circle
        return Math.sqrt(Math.pow(clickX - circleX, 2) + Math.pow(clickY - circleY, 2)) <= radius;
    }

    private void showColorPicker(Player player) {
        // Create a color picker dialog
        final Dialog colorPickerDialog = new Dialog("Choose Color", skin);
        colorPickerDialog.getContentTable().defaults().space(10);

        // Load textures for the color options
        Texture colorTexture1 = new Texture(Gdx.files.internal("ui/colourPicker1.png"));
        Texture colorTexture2 = new Texture(Gdx.files.internal("ui/colourPicker2.png"));
        Texture colorTexture3 = new Texture(Gdx.files.internal("ui/colourPicker3.png"));
        Texture colorTexture4;

        if (checkPlayerTakenColour("ff29be")) {
            colorTexture4 = new Texture(Gdx.files.internal("ui/colourPickerTaken4.png"));
        } else {
            colorTexture4 = new Texture(Gdx.files.internal("ui/colourPicker4.png"));
        }

        // Create image buttons for the textures
        ImageButton colorButton1 = new ImageButton(createButtonStyle(colorTexture1));
        ImageButton colorButton2 = new ImageButton(createButtonStyle(colorTexture2));
        ImageButton colorButton3 = new ImageButton(createButtonStyle(colorTexture3));
        ImageButton colorButton4 = new ImageButton(createButtonStyle(colorTexture4));

        // Add listeners to the buttons
        colorButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.color = Color.valueOf("29ff29");
                colorPickerDialog.hide();
            }
        });

        colorButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.color = Color.valueOf("ff3b29");
                colorPickerDialog.hide();
            }
        });

        colorButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.color = Color.valueOf("2973ff"); // Update this to the intended color if needed
                colorPickerDialog.hide();
            }
        });

        colorButton4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!checkPlayerTakenColour("ff29be")) {
                    player.color = Color.valueOf("ff29be");
                }
                colorPickerDialog.hide();
            }
        });

        // Add buttons to the dialog in a grid layout
        colorPickerDialog.getContentTable().add(colorButton1).pad(10);
        colorPickerDialog.getContentTable().add(colorButton2).pad(10);
        colorPickerDialog.getContentTable().add(colorButton3).pad(10).row();
        colorPickerDialog.getContentTable().add(colorButton4).pad(10);

        // Show the dialog
        colorPickerDialog.show(stage);

        // Dispose textures when no longer needed
        colorPickerDialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!colorPickerDialog.isVisible()){
                    colorTexture1.dispose();
                    colorTexture2.dispose();
                    colorTexture3.dispose();
                    colorTexture4.dispose();
                }
            }
        });
    }

    private ImageButton.ImageButtonStyle createButtonStyle(Texture texture) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(texture)); // Optionally, a different texture for "pressed"
        return style;
    }

    private Color randomColor() {
        // Return a random color for the player
        return new Color(
            MathUtils.random(0.5f, 1f),  // Red channel (light range)
            MathUtils.random(0.5f, 1f),  // Green channel (light range)
            MathUtils.random(0.5f, 1f),  // Blue channel (light range)
            1f                           // Alpha channel (fully opaque)
        );
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the shapes (circles)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < PlayerManager.getInstance().getPlayers().size(); i++) {
            Player player = PlayerManager.getInstance().getPlayers().get(i);
            Vector2 position = playerPositions.get(i); // Get the position for this player
            shapeRenderer.setColor(player.getColor());
            shapeRenderer.circle(position.x, position.y, circleRadius); // Draw the circle at the specified position
        }
        shapeRenderer.end();

        // Draw the stage (UI elements)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Add the new name fields to the stage for each player
        for (TextField nameField : nameFields) {
            stage.addActor(nameField);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
