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

public class TaskSelectionScreen implements Screen {
    private Screen previousScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Runnable onConfirm; // Callback for confirmation
    private Task task; // The task to be confirmed
    private Texture whiteTexture;

    public TaskSelectionScreen(Screen previousScreen, Task task, Runnable onConfirm) {
        this.previousScreen = previousScreen;
        this.task = task;
        this.onConfirm = onConfirm;

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
                SoundManager.getInstance().playSound("click");
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
            }
        });

        TextButton cancelButton = new TextButton("Cancel", new TextButton.TextButtonStyle(null, null, null, font));
        cancelButton.getLabel().setFontScale(2f);
        cancelButton.setColor(Color.RED);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
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

        Tooltip.getInstance().clear();

        batch.begin();
        batch.setColor(0, 0, 0, 0.2f); // Dark semi-transparent
        batch.draw(whiteTexture, Gdx.graphics.getWidth() / 2f - 400, Gdx.graphics.getHeight() / 2f - 150, 900, 400);
        batch.setColor(1, 1, 1, 1); // Reset color
        batch.end();

        stage.act(delta);
        stage.draw();

        // Handle the Escape key to cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
        }
        Tooltip.getInstance().render(GameState.getInstance().getUiCamera(), GameState.getInstance().getViewport().getWorldWidth(), GameState.getInstance().getViewport().getWorldHeight());

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
    }
}
