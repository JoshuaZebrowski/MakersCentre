package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    private TextButton startButton;
    private TextButton settingsButton;
    private TextButton quitButton;
    private Texture backgroundTexture;

    public MainMenuScreen() {
        // Initialize batch and stage in constructor
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set input processor once here

        // Load skin and background texture
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("ui/menuBackground.png"));

        // Create and position buttons
        createButtons();
    }

    private void createButtons() {
        startButton = new TextButton("Start", skin);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2 - 250, Gdx.graphics.getHeight() / 2 - startButton.getHeight() / 2);

        settingsButton = new TextButton("Settings", skin);
        settingsButton.setSize(200, 50);
        settingsButton.setPosition(Gdx.graphics.getWidth() / 2 - settingsButton.getWidth() / 2 + 250, Gdx.graphics.getHeight() / 2 - settingsButton.getHeight() / 2);

        quitButton = new TextButton("Quit", skin);
        quitButton.setSize(200, 50);
        quitButton.setPosition(Gdx.graphics.getWidth() / 2 - quitButton.getWidth() / 2 , Gdx.graphics.getHeight() / 2 - quitButton.getHeight() / 2);

        // Add button listeners
        addListeners();

        // Add buttons to stage
        stage.addActor(startButton);
        stage.addActor(settingsButton);
        stage.addActor(quitButton);
    }

    private void addListeners() {
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                dispose();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameSetup());
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Screen currentScreen = ((Game) Gdx.app.getApplicationListener()).getScreen();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(currentScreen));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        // Set input processor for the stage
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background texture and UI elements
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw stage UI elements
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // You can add any logic when the screen is hidden, but no additional code needed for this simple screen
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        // Dispose of all resources
        if (batch != null) {
            batch.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
