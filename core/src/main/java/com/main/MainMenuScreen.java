package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    public static final int VIRTUAL_WIDTH = 1920; // Virtual screen width
    public static final int VIRTUAL_HEIGHT = 1080; // Virtual screen height
    private Stage stage;
    private Skin skin;
    private TextButton startButton;
    private TextButton settingsButton;

    public MainMenuScreen() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json")); // Ensure the skin is loaded
        // Initialize buttons and other UI elements
        startButton = new TextButton("Start", skin);
        settingsButton = new TextButton("Settings", skin);
        // Add buttons to the stage
        stage.addActor(startButton);
        stage.addActor(settingsButton);
    }

    @Override
    public void show() {

        if (stage == null) {
            stage = new Stage(new ScreenViewport());
        }
        if (skin == null) {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        }
        // Set up the Stage and Skin (used for UI elements like buttons)
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the skin for buttons (ensure you have this file in the assets folder)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create the Start Button
        startButton = new TextButton("Start", skin);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2-200, Gdx.graphics.getHeight() / 2 - startButton.getHeight() / 2);


        settingsButton = new TextButton("Settings", skin);
        settingsButton.setSize(200, 50);
        settingsButton.setPosition(Gdx.graphics.getWidth() / 2 - settingsButton.getWidth() / 2 + 200, Gdx.graphics.getHeight() / 2 - settingsButton.getHeight() / 2);

        //add action listener to start button
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // Dispose of the current screen
                dispose();

                // Create a new instance of the GameSetup class
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameSetup());
            }
        });


        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // Switch to the Main screen (the actual game screen)
                Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(currentScreen));
            }
        });

        // Add the button to the stage
        stage.addActor(startButton);
        stage.addActor(settingsButton);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the stage (which includes the button)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        Gdx.app.log("DEBUG", "MainMenuScreen disposed");
        if (stage != null) {
            stage.dispose();
            stage = null; // Set to null to avoid double disposal
        }
        if (skin != null) {
            skin.dispose();
            skin = null; // Set to null to avoid double disposal
        }
    }
}
