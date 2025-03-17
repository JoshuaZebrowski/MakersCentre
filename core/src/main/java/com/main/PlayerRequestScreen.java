package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class PlayerRequestScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture
    private Main main; // Reference to the main game screen
    private Task task; // The task to be assigned
    private List<Player> eligiblePlayers; // List of players eligible to take the task

    public PlayerRequestScreen(Main main, Task task, List<Player> eligiblePlayers) {
        this.main = main;
        this.task = task;
        this.eligiblePlayers = eligiblePlayers;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Increase font size
        font.setColor(Color.WHITE);

        // Load the background texture
        backgroundTexture = new Texture(Gdx.files.internal("ui/weatherBackground.png"));

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Add a title label at the top
        Label titleLabel = new Label("Send Task Request", new Label.LabelStyle(font, Color.YELLOW));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).center().padBottom(20).row();

        for (Player player : eligiblePlayers) {
            String playerName = player.getName();
            Gdx.app.log("DEBUG", "Player name: " + playerName); // Debug log to verify the name
            int hashIndex = playerName.indexOf("#");
            if (hashIndex != -1) {
                playerName = playerName.substring(0, hashIndex); //extracts the player's display name
            }

            TextButton playerButton = new TextButton(playerName, new TextButton.TextButtonStyle(null, null, null, font));
            playerButton.getLabel().setFontScale(2f);
            playerButton.setColor(player.getColor());

            playerButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Add the task to the player's pending tasks list
                    player.addPendingTask(task);
                    main.resumeGame(); // Return to the main game screen
                }
            });

            mainTable.add(playerButton).pad(20).width(200).height(60).row();
        }

        // Add a cancel button
        TextButton cancelButton = new TextButton("Cancel", new TextButton.TextButtonStyle(null, null, null, font));
        cancelButton.getLabel().setFontScale(2f);
        cancelButton.setColor(Color.RED);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.resumeGame(); // Return to the main game screen
            }
        });

        mainTable.add(cancelButton).pad(20).width(200).height(60);

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw the stage (text and UI elements)
        stage.act(delta);
        stage.draw();

        // Handle the Escape key to cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            main.resumeGame(); // Return to the main game screen
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose(); // Dispose of the background texture
    }
}
