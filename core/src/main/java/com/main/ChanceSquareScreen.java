package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ChanceSquareScreen implements Screen {
    private Screen previousScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Task chanceTask;
    private Texture whiteTexture;

    public ChanceSquareScreen(Screen previousScreen, Task chanceTask) {
        this.previousScreen = previousScreen;
        this.chanceTask = chanceTask;
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));


        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1f * GameState.getInstance().getTextScale());
        font.setColor(Color.WHITE);

        // Create white texture for background box
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        // Create a table to organize the content
        Table mainTable = new Table();
        mainTable.setSize(600, 300);
        mainTable.setPosition(Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() / 2f - 150);
        mainTable.center();

        // Add the chance square name (yellow)
        Label taskNameLabel = new Label(chanceTask.getName(), new Label.LabelStyle(font, Color.YELLOW));
        taskNameLabel.setAlignment(Align.center);
        taskNameLabel.setWrap(false);
        mainTable.add(taskNameLabel).width(550).padBottom(20).row();

        // Add the chance square description (white)
        String description = chanceTask.getDescription()
            .replace("{m}", chanceTask.getResourceAmountString("Money"))
            .replace("{p}", chanceTask.getResourceAmountString("People"));
        Label descriptionLabel = new Label(description, new Label.LabelStyle(font, Color.WHITE));
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);
        mainTable.add(descriptionLabel).width(550).padBottom(20).row();

        TextButton backButton = new TextButton("Continue", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
            }
        });
        mainTable.add(backButton).width(150).padTop(10);

        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        batch.begin();
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(whiteTexture, Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() / 2f - 150, 800, 400);
        batch.setColor(1, 1, 1, 1);
        batch.end();

        // Draw the UI stage
        stage.act(delta);
        stage.draw();

        // Escape key to return
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
        whiteTexture.dispose();
    }
}
