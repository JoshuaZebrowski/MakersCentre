package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Options implements Screen {
    private Screen previousScreen;
    private Stage stage;


    public Options(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Main Menu Button
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // Navigate to the main menu screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());  // Replace with your main menu screen class
            }
        });

        // Back Button (Back to previous screen)
        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // Navigate back to settings screen

                ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(previousScreen)); // Assuming Settings takes a previous screen
            }
        });

        // Back Button (Back to previous screen)
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // Navigate back to settings screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
            }
        });

        // Layout
        table.add(mainMenuButton).expandX();
        table.row();
        table.add(settingsButton).pad(20);
        table.row();
        table.add(backButton).pad(20);
    }

    @Override
    public void render(float delta) {
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
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
