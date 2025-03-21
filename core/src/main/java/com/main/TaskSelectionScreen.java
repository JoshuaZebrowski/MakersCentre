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

public class TaskSelectionScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture
    private Runnable onConfirm; // Callback for confirmation
    private Main main; // Reference to the main game screen
    private Task task; // The task to be confirmed


    public TaskSelectionScreen(Main main, Task task, Runnable onConfirm) {
        this.main = main;
        this.task = task;
        this.onConfirm = onConfirm;

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
        Label titleLabel = new Label("Task Selection", new Label.LabelStyle(font, Color.YELLOW));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).center().padBottom(20).row();

        // Add the task name (yellow)
        Label taskNameLabel = new Label(task.getName(), new Label.LabelStyle(font, Color.YELLOW));
        taskNameLabel.setAlignment(Align.left);
        taskNameLabel.setWrap(false); // Disable wrapping for the title
        mainTable.add(taskNameLabel).left().width(400).row(); // Set a fixed width for the title

        // Add the task description (white)
        String description = task.getDescription()
            .replace("{m}", task.getResourceAmountString("Money"))
            .replace("{p}", task.getResourceAmountString("People"));
        Label descriptionLabel = new Label(description, new Label.LabelStyle(font, Color.WHITE));
        descriptionLabel.setAlignment(Align.left);
        descriptionLabel.setWrap(true); // Enable wrapping for the description
        mainTable.add(descriptionLabel).left().width(400).row(); // Set a fixed width for the description

        // Add Confirm and Cancel buttons
        TextButton confirmButton = new TextButton("Confirm", new TextButton.TextButtonStyle(null, null, null, font));
        confirmButton.getLabel().setFontScale(2f);
        confirmButton.setColor(Color.GREEN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onConfirm.run(); // Run the confirmation logic
                main.resumeGame(); // Return to the main game screen
            }
        });

        TextButton cancelButton = new TextButton("Cancel", new TextButton.TextButtonStyle(null, null, null, font));
        cancelButton.getLabel().setFontScale(2f);
        cancelButton.setColor(Color.RED);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.resumeGame(); // Return to the main game screen
            }
        });

        mainTable.add(confirmButton).pad(20).width(200).height(60);
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
