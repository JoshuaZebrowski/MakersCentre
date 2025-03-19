package com.main;

import com.badlogic.gdx.Game;
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

public class GameOverScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture
    private Main main; // Reference to the main game screen
    private Player losingPlayer; // The player who ran out of resources

    public GameOverScreen(Main main, Player losingPlayer) {
        this.main = main;
        this.losingPlayer = losingPlayer;

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
        Label titleLabel = new Label("GAME OVER", new Label.LabelStyle(font, Color.RED));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).center().padBottom(20).row();

        // Add the game-over message
        String message = "OH NO. " + losingPlayer.getName() + " ran out of resources and so the game has ended.\nBetter luck next time!";
        Label messageLabel = new Label(message, new Label.LabelStyle(font, Color.WHITE));
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true); // Enable wrapping for the message
        mainTable.add(messageLabel).colspan(2).center().width(600).padBottom(20).row();

        // Add a restart button
        TextButton restartButton = new TextButton("Restart", new TextButton.TextButtonStyle(null, null, null, font));
        restartButton.getLabel().setFontScale(2f);
        restartButton.setColor(Color.GREEN);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Dispose of the current screen
                dispose();

                // Create a new MainMenuScreen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        // Add the restart button to the UI
        mainTable.add(restartButton).pad(20).width(200).height(60);

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

        // Handle the Escape key to return to the main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
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
        backgroundTexture.dispose();
    }
}
