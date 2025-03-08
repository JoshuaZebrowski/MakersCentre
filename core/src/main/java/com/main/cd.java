package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class cd {
    private String name;
    private String soundKey;
    private Texture texture;
    private String soundPath;
    private boolean isSoundLoaded = false;

    public cd(String name, String texturePath, String soundPath) {
        this.name = name;
        this.soundKey = name + "_sound"; // Unique key for SoundManager
        this.soundPath = soundPath;
        this.texture = new Texture(Gdx.files.internal(texturePath));
    }

    public String getSoundKey() {
        return soundKey;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isSoundLoaded() {
        return isSoundLoaded;
    }

    public void loadSoundAsync(Runnable callback) {
        new Thread(() -> {
            try {
                SoundManager.getInstance().loadMusic(soundKey, soundPath);
                isSoundLoaded = true;
                Gdx.app.postRunnable(callback);  // Notify on main thread
            } catch (GdxRuntimeException e) {
                Gdx.app.error("CD", "Failed to load sound: " + soundPath, e);
            }
        }).start();
    }



    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
        SoundManager.getInstance().stopMusic(soundKey); // Stop before disposing
    }
}
