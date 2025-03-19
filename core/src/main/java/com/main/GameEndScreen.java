package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameEndScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Main main; // Reference to the main game screen

    private String[] messages = {
        "1 Year After\n\nIncrease in potential business.\nIncrease in attendance at the tertiary classes.",
        "2 Years After\n\nStarter businesses are now growing due to the support from the makers centre.\nLocal entrepreneurs are gaining confidence and developing sustainable business models.",
        "3 Years After\n\nMakersCentre participants are now more knowledgeable on business management and have a higher understanding of it.\nUnemployment is rapidly decreasing due to the new job opportunities available from the starter companies.",
        "4 Years After\n\nThe people of Makers Valley are more qualified for tertiary employment due to the tertiary education classes.\nEconomic stability is improving as more residents secure stable employment and launch their own businesses."
    };

    private int currentMessageIndex = 0;
    private float messageTimer = 0;
    private Label messageLabel;

    public GameEndScreen(Main main) {
        this.main = main;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f); // Increase font size
        font.setColor(Color.WHITE);

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Add the main text "Finished.... You Won!!!!" at the top
        Label mainLabel = new Label("Finished.... You Won!!!!", new Label.LabelStyle(font, Color.YELLOW));
        mainLabel.setFontScale(4f);
        mainLabel.setAlignment(Align.center);
        mainTable.add(mainLabel).padBottom(50).row(); // Add padding below the main text

        // Add a label for the dynamic messages
        messageLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        messageLabel.setFontScale(2f);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true); // Enable text wrapping
        mainTable.add(messageLabel).width(Gdx.graphics.getWidth() * 0.8f).padBottom(20).row(); // Set width and padding

        // Add a button to return to the main game screen
        TextButton returnButton = new TextButton("Return to Game", new TextButton.TextButtonStyle(null, null, null, font));
        returnButton.getLabel().setFontScale(2f);
        returnButton.setColor(Color.GREEN);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.resumeGame(); // Return to the main game screen
            }
        });

        mainTable.add(returnButton).pad(20).width(300).height(60);

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        currentMessageIndex = 0; // Reset the message index
        messageTimer = 0; // Reset the timer
        messageLabel.setText(messages[currentMessageIndex]); // Set the initial message
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the message timer
        messageTimer += delta;
        if (messageTimer >= 5) { // Every 5 seconds
            messageTimer = 0; // Reset the timer
            currentMessageIndex = (currentMessageIndex + 1) % messages.length; // Cycle to the next message
            messageLabel.setText(messages[currentMessageIndex]); // Update the label text
        }

        // Draw the stage (text and UI elements)
        stage.act(delta);
        stage.draw();
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
