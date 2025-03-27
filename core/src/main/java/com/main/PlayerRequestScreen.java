package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
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
    private Screen previousScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture
    private Task task; // The task to be assigned
    private List<Player> eligiblePlayers; // List of players eligible to take the task
    private Texture whiteTexture;

    public PlayerRequestScreen(Screen previousScreen, Task task, List<Player> eligiblePlayers) {
        this.task = task;
        this.eligiblePlayers = eligiblePlayers;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Increase font size
        font.setColor(Color.WHITE);

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Add a title label at the top
        Label titleLabel = new Label("Send Task Request", new Label.LabelStyle(font, Color.YELLOW));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).center().padBottom(20).row();

        int counter = 0;
        boolean categoryAlreadyPending = false;
        String playerWithPendingCategory = "";
        String category = task.getCategory();

// First, check if any player already has a task of the same category pending
        for (Player player : eligiblePlayers) {
            for (Task pendingTask : player.pendingTasks) {
                if (pendingTask.getCategory().equals(category)) {
                    categoryAlreadyPending = true;
                    playerWithPendingCategory = player.getName();
                    break;
                }
            }
            if (categoryAlreadyPending) {
                break;
            }
        }

// Now, iterate through the eligible players and display the appropriate buttons
        for (Player player : eligiblePlayers) {
            if (categoryAlreadyPending) {
                // If a category is already pending, only show the player who owns that category
                if (player.getName().equals(playerWithPendingCategory)) {
                    String playerName = player.getName();
                    Gdx.app.log("DEBUG", "Player name: " + playerName); // Debug log to verify the name
                    int hashIndex = playerName.indexOf("#");
                    if (hashIndex != -1) {
                        playerName = playerName.substring(0, hashIndex); // Extracts the player's display name
                    }

                    TextButton playerButton = new TextButton(playerName, new TextButton.TextButtonStyle(null, null, null, font));
                    playerButton.getLabel().setFontScale(2f);
                    playerButton.setColor(player.getColour());

                    playerButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Add the task to the player's pending tasks list
                            player.addPendingTask(task);
                            ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
                        }
                    });

                    mainTable.add(playerButton).pad(20).width(200).height(60).row();
                    counter += 1;
                }
            } else {
                // If no category is pending, check if the player has any pending tasks
                if (player.pendingTasks.isEmpty()) {
                    String playerName = player.getName();
                    Gdx.app.log("DEBUG", "Player name: " + playerName); // Debug log to verify the name
                    int hashIndex = playerName.indexOf("#");
                    if (hashIndex != -1) {
                        playerName = playerName.substring(0, hashIndex); // Extracts the player's display name
                    }

                    TextButton playerButton = new TextButton(playerName, new TextButton.TextButtonStyle(null, null, null, font));
                    playerButton.getLabel().setFontScale(2f);
                    playerButton.setColor(player.getColour());

                    playerButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Add the task to the player's pending tasks list
                            player.addPendingTask(task);
                            ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
                        }
                    });

                    mainTable.add(playerButton).pad(20).width(200).height(60).row();
                    counter += 1;
                }
            }
        }

        if (counter == 0) {
            Label noEligiblePlayers = new Label("Players have Pending Tasks or have Tasks already", new Label.LabelStyle(font, Color.WHITE));
            titleLabel.setFontScale(4f);
            titleLabel.setAlignment(Align.center);
            mainTable.add(noEligiblePlayers).pad(20).width(200).height(60).row();
        }

        // Add a cancel button
        TextButton cancelButton = new TextButton("Cancel", new TextButton.TextButtonStyle(null, null, null, font));
        cancelButton.getLabel().setFontScale(2f);
        cancelButton.setColor(Color.RED);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
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
        // Draw the stage (text and UI elements)
        batch.begin();
        batch.setColor(100, 0, 0, 0.7f); // Dark semi-transparent
        batch.draw(whiteTexture, Gdx.graphics.getWidth() / 2f - 400, Gdx.graphics.getHeight() / 2f - 150, 900, 400);
        batch.setColor(1, 1, 1, 1); // Reset color
        batch.end();

        stage.act(delta);


        stage.draw();




        // Handle the Escape key to cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);

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
