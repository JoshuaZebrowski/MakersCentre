package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class GameSetup implements Screen {
    private Stage stage;
    private Skin skin;
    private TextButton startButton;
    private TextButton plusButton;
    private TextButton minusButton;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Vector2> playerPositions;
    private ArrayList<TextField> nameFields;
    private ArrayList<Label> errorLabels;
    private ArrayList<HorizontalGroup> colorPickers;
    private ArrayList<Color> selectedColors;
    private float circleRadius = 50;
    private float spacing = 150;
    private float yPosition = Gdx.graphics.getHeight() / 2;
    private float yOffset = 100;

    private List<Task> tasks;

    public GameSetup() {}

    public void show() {
        PlayerManager.getInstance().reset();

        tasks = ResourceLoader.loadTask();
        if (tasks == null || tasks.isEmpty()) {
            Gdx.app.error("ERROR", "Failed to load tasks from JSON");
            return;
        }

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        shapeRenderer = new ShapeRenderer();
        playerPositions = new ArrayList<>();
        nameFields = new ArrayList<>();
        errorLabels = new ArrayList<>();
        colorPickers = new ArrayList<>();
        selectedColors = new ArrayList<>();

        playerPositions.add(new Vector2(100, yPosition));
        Color initialColor = randomColor();
        selectedColors.add(initialColor);

        addPlayerUI(0, "Player 1", playerPositions.get(0).x);

        startButton = new TextButton("Start", skin);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2f - startButton.getWidth() / 2f, yPosition - 300);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean allNamesValid = true;
                for (Label label : errorLabels) label.setText("");

                for (int i = 0; i < nameFields.size(); i++) {
                    TextField nameField = nameFields.get(i);
                    String trimmedName = nameField.getText().trim();
                    nameField.setText(trimmedName);
                    if (trimmedName.isEmpty()) {
                        errorLabels.get(i).setText("Name required");
                        allNamesValid = false;
                    }
                }

                if (!allNamesValid) return;

                PlayerManager.getInstance().reset();

                for (int i = 0; i < nameFields.size(); i++) {
                    Player player = new Player(nameFields.get(i).getText(), selectedColors.get(i));
                    PlayerManager.getInstance().addPlayer(player);
                }

                Board board = new Board(new ArrayList<>(tasks));
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Main(board.getNodes()));
            }
        });

        plusButton = new TextButton("+", skin);
        plusButton.setSize(50, 50);
        plusButton.setPosition(playerPositions.get(0).x + circleRadius + 20, yPosition);

        minusButton = new TextButton("-", skin);
        minusButton.setSize(50, 50);
        minusButton.setPosition(playerPositions.get(0).x + circleRadius + 20, yPosition - 50);
        minusButton.setVisible(false);

        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedColors.size() < 4) {
                    int idx = selectedColors.size();
                    float newX = playerPositions.get(playerPositions.size() - 1).x + circleRadius * 2 + spacing;
                    playerPositions.add(new Vector2(newX, yPosition));

                    Color color = randomColor();
                    selectedColors.add(color);

                    addPlayerUI(idx, "Player " + (idx + 1), newX);

                    plusButton.setPosition(newX + circleRadius + 20, yPosition);
                    minusButton.setPosition(newX + circleRadius + 20, yPosition - 50);
                    minusButton.setVisible(true);
                    if (selectedColors.size() == 4) plusButton.setVisible(false);
                }
            }
        });

        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int idx = selectedColors.size() - 1;
                if (idx >= 1) {
                    playerPositions.remove(idx);
                    nameFields.get(idx).remove();
                    nameFields.remove(idx);
                    errorLabels.get(idx).remove();
                    errorLabels.remove(idx);
                    colorPickers.get(idx).remove();
                    colorPickers.remove(idx);
                    selectedColors.remove(idx);

                    float newX = playerPositions.get(playerPositions.size() - 1).x + circleRadius * 2 + spacing;
                    plusButton.setPosition(newX + circleRadius + 20, yPosition);
                    minusButton.setPosition(newX + circleRadius + 20, yPosition - 50);

                    if (selectedColors.size() < 4) plusButton.setVisible(true);
                    if (selectedColors.size() == 1) minusButton.setVisible(false);
                }
            }
        });

        stage.addActor(startButton);
        stage.addActor(plusButton);
        stage.addActor(minusButton);
    }

    private void addPlayerUI(int index, String name, float xPosition) {
        TextField nameField = new TextField(name, skin);
        nameField.setPosition(xPosition, yPosition - yOffset);
        nameFields.add(nameField);
        stage.addActor(nameField);

        Label errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        errorLabel.setPosition(xPosition, yPosition - yOffset - 25);
        errorLabels.add(errorLabel);
        stage.addActor(errorLabel);

        HorizontalGroup colorGroup = new HorizontalGroup();
        colorGroup.space(5);
        colorGroup.setPosition(xPosition, yPosition - yOffset - 60);

        String[] hexColors = { "29ff29", "ff3b29", "2973ff", "ff29be" };
        for (String hex : hexColors) {
            final int playerIdx = index;
            TextButton colorBtn = new TextButton("", skin);
            colorBtn.setColor(Color.valueOf(hex));
            colorBtn.setSize(30, 30);

            colorBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    selectedColors.set(playerIdx, Color.valueOf(hex));
                }
            });
            colorGroup.addActor(colorBtn);
        }

        colorPickers.add(colorGroup);
        stage.addActor(colorGroup);
    }

    private Color randomColor() {
        return new Color(
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f),
            1f
        );
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < selectedColors.size(); i++) {
            shapeRenderer.setColor(selectedColors.get(i));
            shapeRenderer.circle(playerPositions.get(i).x, playerPositions.get(i).y, circleRadius);
        }
        shapeRenderer.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void resetGame() {
        PlayerManager.getInstance().reset();
    }

    public void resetGameState() {}

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void hide() {
        dispose();
    }

    public void pause() {}

    public void resume() {}

    public void dispose() {
        Gdx.app.log("DEBUG", "GameSetup disposed");
        if (stage != null) stage.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (skin != null) skin.dispose();
    }
}
