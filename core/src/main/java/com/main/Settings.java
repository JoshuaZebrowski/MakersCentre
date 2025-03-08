package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.main.Tooltip;


public class Settings implements Screen {
    private final Screen previousScreen;
    private Stage stage;
    private SoundManager soundManager;

    private Slider musicSlider;
    private Slider sfxSlider;

    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;

    public Settings(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.soundManager = SoundManager.getInstance();
        loadSettings();
    }

    private void loadSettings() {
        try {
            FileHandle file = Gdx.files.local("settings.json"); // Use local storage so we can write back
            if (file.exists()) {
                JsonValue json = new JsonReader().parse(file);
                JsonValue settings = json.get("settings");

                if (settings != null) {
                    musicVolume = settings.getFloat("Music-Volume", 1.0f);
                    sfxVolume = settings.getFloat("SFX-Volume", 1.0f);
                }
            }

            soundManager.setMusicVolume(musicVolume);
            soundManager.setSoundVolume(sfxVolume);
        } catch (Exception e) {
            Gdx.app.error("Settings", "Failed to load settings.json", e);
        }
    }

    private void saveSettings() {
        soundManager.setMusicVolume(musicSlider.getValue());
        soundManager.setSoundVolume(sfxSlider.getValue());

//        try {
//            Json json = new Json();
//
//            // Create a new JsonValue object for the settings
//            JsonValue settings = new JsonValue(JsonValue.ValueType.object);
//            settings.addChild("Music-Volume", new JsonValue(musicSlider.getValue()));
//            settings.addChild("SFX-Volume", new JsonValue(sfxSlider.getValue()));
//
//            // Avoid recursive or unwanted serialization by carefully checking parent object
//
//
//            // Root JSON object that holds settings
//            JsonValue root = new JsonValue(JsonValue.ValueType.object);
//            root.addChild("settings", settings);
//
//            // Write to the file
//            FileHandle file = Gdx.files.local("data/settings.json");
//            file.writeString(json.prettyPrint(root), false);
//
//        } catch (Exception e) {
//            Gdx.app.error("Settings", "Failed to save settings.json", e);
//        }
    }


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Music Volume Slider
        Label musicLabel = new Label("Music Volume", skin);
        musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(musicVolume);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.setMusicVolume(musicSlider.getValue());
            }
        });

        // SFX Volume Slider
        Label sfxLabel = new Label("SFX Volume", skin);
        sfxSlider = new Slider(0, 1, 0.01f, false, skin);
        sfxSlider.setValue(sfxVolume);
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.setSoundVolume(sfxSlider.getValue());
            }
        });

        TextButton removeToolTips = new TextButton("ToolTips", skin);
        if(Tooltip.getInstance().isVisible()){
            // button colour
            removeToolTips.setColor(Color.GREEN); // Changes button color
        }else{
            removeToolTips.setColor(Color.RED);
        }
        removeToolTips.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Tooltip.getInstance().setVisible();
                if(Tooltip.getInstance().isVisible()){
                    removeToolTips.setColor(Color.GREEN); // Changes button color
                }else{
                    removeToolTips.setColor(Color.RED);
                }
            }
        });

        // Save Button
        TextButton saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                saveSettings();
            }
        });

        CheckBox backgroundMusicCheckBox = new CheckBox("Background Music", skin);
        backgroundMusicCheckBox.setChecked(soundManager.isMusicPlaying("background")); // Set state based on current music playing status

        backgroundMusicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (backgroundMusicCheckBox.isChecked()) {
                    SoundManager.getInstance().playMusic("background", true); // Play with looping
                } else {
                    SoundManager.getInstance().stopMusic("background");
                }
            }
        });

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
            }
        });

        // Layout
        table.add(musicLabel).pad(10);
        table.row();
        table.add(musicSlider).width(300).pad(10);
        table.row();
        table.add(backgroundMusicCheckBox).pad(10);
        table.row();
        table.add(sfxLabel).pad(10);
        table.row();
        table.add(sfxSlider).width(300).pad(10);
        table.row();
        table.add(removeToolTips).width(300).pad(10);
        table.row();
        table.add(saveButton).pad(20);
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
