package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.main.Tooltip;
import org.w3c.dom.Text;


public class Settings implements Screen {
    private final Screen previousScreen;
    private Stage stage;
    private SoundManager soundManager;

    private SpriteBatch batch;

    private Slider musicSlider;
    private Slider sfxSlider;
    private Slider textScaleSlider;

    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private Texture whiteTexture;

    public Settings(Screen previousScreen) {
        this.previousScreen = previousScreen;
        this.soundManager = SoundManager.getInstance();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
        batch = new SpriteBatch();
        GameState gameState = GameState.getInstance();

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
        Label musicLabel = new Label("Ambience Volume", skin);
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

        // Get rid of tutorials
        TextButton stopTutorial = new TextButton("Stop Tutorial Screens", skin);
        if(!TutorialManager.getInstance().getOff()){
            // button colour
            stopTutorial.setColor(Color.RED); // Changes button color
        }else{
            stopTutorial.setColor(Color.GREEN);
        }
        stopTutorial.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                TutorialManager.getInstance().setOff();
                if(!TutorialManager.getInstance().getOff()){
                    stopTutorial.setColor(Color.RED); // Changes button color
                }else{
                    stopTutorial.setColor(Color.GREEN);
                }
            }
        });

        TextButton removeWeatherEffects = new TextButton("Remove Weather Effects", skin);
        if(!GameState.getInstance().isRemoveWeatherEffects()){
            // button colour
            removeWeatherEffects.setColor(Color.RED); // Changes button color
        }else{
            removeWeatherEffects.setColor(Color.GREEN);
        }
        removeWeatherEffects.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                GameState.getInstance().setRemoveWeatherEffects();

                if(!GameState.getInstance().isRemoveWeatherEffects()){
                    removeWeatherEffects.setColor(Color.RED); // Changes button color
                }else{
                    removeWeatherEffects.setColor(Color.GREEN);
                    soundManager.stopMusic("rainSound");
                    soundManager.stopMusic("stormSound");
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



        CheckBox colourBlindMode = new CheckBox("Colour Blind Mode", skin);
        colourBlindMode.setChecked(GameState.getInstance().isColourBlind());

        colourBlindMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameState.getInstance().setColourBlind();
            }
        });

        // Text Scale Slider
        Label textScaleLabel = new Label("Text Scale", skin);
        textScaleSlider = new Slider(0.5f, 2.0f, 0.01f, false, skin);  // Range 0.5 to 2.0 for text scale
        textScaleSlider.setValue(GameState.getInstance().getTextScale()); // Initialize with the current text scale
        textScaleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // Update text scale in GameState when slider is adjusted
                GameState.getInstance().setTextScale(textScaleSlider.getValue());
            }
        });

        // Quit Game
            TextButton quitButton = new TextButton("Quit Game", skin);
            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new GameEndScreen(false));
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

        Label info = new Label("*For some changes to take effect click Back", skin);

        // Layout
        table.add(musicLabel).pad(10);
        table.row();
        table.add(musicSlider).width(300).pad(10);
        table.row();
        table.add(colourBlindMode).pad(10);
        table.row();
        table.add(sfxLabel).pad(10);
        table.row();
        table.add(sfxSlider).width(300).pad(10);
        table.row();
        table.add(textScaleLabel).pad(10);  // Add text scale label
        table.row();
        table.add(textScaleSlider).width(300).pad(10);  // Add text scale slider
        table.row();
        table.add(removeToolTips).width(300).pad(10);
        table.add(removeWeatherEffects).width(300).pad(10);
        table.row();
        table.add(stopTutorial).width(300).pad(10);
        table.row();
        table.add(info);
        table.row();
        if(PlayerManager.getInstance().getPlayers().size() > 0){
            table.add(quitButton);
        }
        table.row();
        table.add(backButton).pad(20);

    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        batch.begin();
        batch.setColor(0, 0, 0,0.2f); // Semi-transparent black
        batch.draw(whiteTexture,Gdx.graphics.getWidth() / 2 - 250,Gdx.graphics.getHeight() / 2 - 250, 550, 500);
        batch.setColor(1, 1, 1, 0.2f); // Reset color to default
        batch.end();

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
