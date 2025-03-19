package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ChanceSquareScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture
    private Main main; // Reference to the main game screen
    private Task chanceTask; // The chance square task

    public ChanceSquareScreen(Main main, Task chanceTask) {
        this.main = main;
        this.chanceTask = chanceTask;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(4f); // Increase font size
        font.setColor(Color.WHITE);

        // Load the background texture
        backgroundTexture = new Texture(Gdx.files.internal("ui/weatherBackground.png"));

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();


        // Add the chance square name (yellow)
        Label taskNameLabel = new Label(chanceTask.getName(), new Label.LabelStyle(font, Color.YELLOW));
        taskNameLabel.setAlignment(Align.center);
        taskNameLabel.setWrap(false); // Disable wrapping for the title
        mainTable.add(taskNameLabel).left().width(800).row(); // Set a fixed width for the title

        // Add the chance square description (white)
        String description = chanceTask.getDescription()
            .replace("{m}", chanceTask.getResourceAmountString("Money"))
            .replace("{p}", chanceTask.getResourceAmountString("People"));
        Label descriptionLabel = new Label(description, new Label.LabelStyle(font, Color.WHITE));
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true); // Enable wrapping for the description
        mainTable.add(descriptionLabel).left().width(800).row(); // Set a fixed width for the description

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

        // Handle the Escape key to exit
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
