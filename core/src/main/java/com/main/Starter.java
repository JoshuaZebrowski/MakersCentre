package com.main;

import com.badlogic.gdx.Game;

public class Starter extends Game {

    @Override
    public void create() {
        // Set the first screen to be the MainMenuScreen
        this.setScreen(new MainMenuScreen());
    }

    @Override
    public void render() {
        super.render(); // Let LibGDX handle screen rendering
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }
}
