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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PlayerTabScreen implements Screen {
    private Screen previousScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player; // Reference to the current player

    public PlayerTabScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.player = PlayerManager.getInstance().getCurrentPlayer();

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Add a title label at the top
        String currentObjective = player.getCurrentCategory();
        String titleText = "Tasks for the: " + (currentObjective != null ? currentObjective : "N/A") + " Objective";
        Label titleLabel = new Label(titleText, new Label.LabelStyle(font, Color.YELLOW));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).center().padBottom(20).row();

        // Create a scrollable container for the tasks
        Table tasksTable = new Table();
        ScrollPane scrollPane = new ScrollPane(tasksTable); // Use ScrollPane
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(false, true); // Enable vertical scrolling
        scrollPane.setFadeScrollBars(false);

        // Check if the player has any tasks
        if (player.getTasks().isEmpty()) {
            // Display a message if no tasks are selected
            Label noTasksLabel = new Label("No tasks selected", new Label.LabelStyle(font, Color.RED));
            noTasksLabel.setAlignment(Align.center);
            tasksTable.add(noTasksLabel).colspan(2).center().padBottom(20).row();
        } else {
            // Display the player's tasks in two columns
            int taskCount = player.getTasks().size();
            for (int i = 0; i < taskCount; i++) {
                Task task = player.getTasks().get(i);

                // Create a table for the task (title and description together)
                Table taskTable = new Table();
                taskTable.pad(10).defaults().pad(5);

                // Add the task title (yellow)
                Label taskLabel = new Label(task.getName(), new Label.LabelStyle(font, Color.YELLOW));
                taskLabel.setAlignment(Align.left);
                taskLabel.setWrap(false); // Disable wrapping for the title
                taskTable.add(taskLabel).left().width(400).row(); // Set a fixed width for the title

                // Add the task description (white)
                String description = task.getDescription()
                    .replace("{m}", task.getResourceAmountString("Money"))
                    .replace("{p}", task.getResourceAmountString("People"));
                Label descriptionLabel = new Label(description, new Label.LabelStyle(font, Color.WHITE));
                descriptionLabel.setAlignment(Align.left);
                descriptionLabel.setWrap(true); // Enable wrapping for the description
                taskTable.add(descriptionLabel).left().width(400).row(); // Set a fixed width for the description

                // Add the task table to the appropriate column
                if (i % 2 == 0) {
                    tasksTable.add(taskTable).left().width(400).padRight(20).padBottom(20); // Add spacing between columns
                } else {
                    tasksTable.add(taskTable).left().width(400).padBottom(20).row(); // Ensure rows are aligned
                }
            }

            // If the number of tasks is odd, add an empty cell to balance the columns
            if (taskCount % 2 != 0) {
                tasksTable.add(); // Empty cell
            }
        }

        // Add the scroll pane to the main table
        mainTable.add(scrollPane).expand().fill().colspan(2).padBottom(20).row();

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

        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background

        // Draw the stage (text and UI elements)
        stage.act(delta);
        stage.draw();

        // Handle the Escape key or 'P' key to close the screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            GameState.getInstance().setCurrentScreen("MS");
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
