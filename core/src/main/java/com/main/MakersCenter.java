package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MakersCenter implements Screen {
    private Screen previousScreen;
    private Stage stage;
    private Texture backgroundTexture;
    private ArrayList<cd> cds;
    private ArrayList<Image> cdImages;
    private Rectangle playBox;
    private Sound currentSound;
    private Image currentCdImage;
    private boolean isPlaying = false;
    private ShapeRenderer shapeRenderer;
    private ComputerScreen computerScreen;
    private List<Map<String, String>> songs;

    public MakersCenter(Screen previousScreen) {
        this.previousScreen = previousScreen;
        cds = new ArrayList<>();
        cdImages = new ArrayList<>();
        songs = loadSongsFromJson();
    }

    @Override
    public void show() {
        SoundManager.getInstance().pauseMusic("background");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Player player = PlayerManager.getInstance().getPlayers().get(0);
        Gdx.app.log("MakersCenter", "Showing player " + player.getName());

        backgroundTexture = new Texture(Gdx.files.internal("ui/makersCentreBackground.png"));
        Image background = new Image(backgroundTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        computerScreen = new ComputerScreen();
        computerScreen.build();
        Group computerUI = computerScreen.getContainer();
        computerUI.setPosition((Gdx.graphics.getWidth() - computerUI.getWidth()) / 2,
            (Gdx.graphics.getHeight() - computerUI.getHeight()) / 2);
        stage.addActor(computerUI);

        createCDsAndAddToScene(songs);

        playBox = new Rectangle(Gdx.graphics.getWidth() / 2 + 165, 100, 100, 100);
        shapeRenderer = new ShapeRenderer();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
                    return true;
                }
                return false;
            }
        });
    }

    private List<Map<String, String>> loadSongsFromJson() {
        FileHandle file = Gdx.files.internal("data/songs.json");
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(file.readString());
            JsonNode songsNode = rootNode.get("songs");

            if (songsNode == null || !songsNode.isArray()) {
                Gdx.app.error("JSON Load Error", "Invalid format: 'songs' key not found or not an array.");
                return null;
            }

            return objectMapper.readValue(
                songsNode.toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );

        } catch (IOException e) {
            Gdx.app.error("JSON Load Error", "Failed to parse songs.json", e);
            return null;
        }
    }

    private void createCDsAndAddToScene(List<Map<String, String>> songs) {
        if (songs == null || songs.isEmpty()) {
            Gdx.app.error("Scene Setup", "No songs to add to the scene.");
            return;
        }

        float startX = 100;
        float startY = Gdx.graphics.getHeight() - 600;

        for (Map<String, String> song : songs) {
            cd newCd = new cd(song.get("name"), song.get("texturePath"), song.get("soundPath"));
            cds.add(newCd);

            Image cdImage = new Image(newCd.getTexture());
            float scale = 0.5f;
            float cd_width = 180 * scale;
            float cd_height = 140 * scale;
            cdImage.setSize(cd_width, cd_height);
            cdImage.setPosition(startX, startY);

            addClickListener(cdImage, newCd);
            cdImages.add(cdImage);
            stage.addActor(cdImage);

            startX += 200;
            if (startX > 400) {
                startX = 100;
                startY -= 100;
            }
        }

        Gdx.app.log("JSON Load", "Successfully loaded and added songs to the scene.");
    }

    private void addClickListener(final Image cdImage, final cd cdObject) {
        final float originalX = cdImage.getX();
        final float originalY = cdImage.getY();

        cdImage.addListener(new ClickListener() {
            private boolean isPlacedInPlayBox = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isPlacedInPlayBox) {
                    float targetX = playBox.getX() + (playBox.getWidth() - cdImage.getWidth()) / 2;
                    float targetY = playBox.getY() + (playBox.getHeight() - cdImage.getHeight()) / 2;

                    cdImage.addAction(Actions.sequence(
                        Actions.moveTo(targetX, targetY, 0.5f),
                        Actions.run(() -> {
                            if (!isPlaying) {
                                isPlaying = true;
                                currentCdImage = cdImage;

                                // Load and play sound from SoundManager
                                cdObject.loadSoundAsync(() -> {

                                    if (cdObject.isSoundLoaded()) {
                                        SoundManager.getInstance().playMusic(cdObject.getSoundKey(), true);

                                        // Start rotation animation
                                        cdImage.setOrigin(cdImage.getWidth() / 2, cdImage.getHeight() / 2);
                                        cdImage.addAction(Actions.forever(Actions.rotateBy(360, 6f)));
                                    }
                                });
                            }
                        })
                    ));
                } else {
                    stopCurrentCD(cdObject);
                    cdImage.addAction(Actions.moveTo(originalX, originalY, 0.5f));
                }
                isPlacedInPlayBox = !isPlacedInPlayBox;
            }
        });
    }

    private void stopCurrentCD(cd cdObject) {
        if (isPlaying) {
            isPlaying = false;

            if (cdObject.isSoundLoaded()) {
                SoundManager.getInstance().stopMusic(cdObject.getSoundKey());  // Stops only the current CD's music
            }

            if (currentCdImage != null) {
                currentCdImage.clearActions(); // Stop rotation animation
            }
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(playBox.x, playBox.y, playBox.width, playBox.height);
        shapeRenderer.end();

        handleSettings();

    }

    private void handleSettings(){
        Gdx.app.log("DEBUG", "Back to Options");

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            Gdx.app.log("DEBUG", "Back to Options");
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings(((Game) Gdx.app.getApplicationListener()).getScreen()));
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
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        for (cd cdObject : cds) {
            cdObject.dispose();
        }
        shapeRenderer.dispose();
    }
}
