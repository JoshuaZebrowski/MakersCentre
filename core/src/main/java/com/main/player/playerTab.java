package com.main.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.main.Player;
import com.main.Resource;
import com.main.Task;

import java.util.List;

public class playerTab {
    private Player player;

    public playerTab(Player player) {
        this.player = player;
    }

    public void playerTarget(Player player) {
        this.player = player;
    }

    public void draw(Batch batch) {
        // No rendering logic needed here anymore
    }

    public boolean isExpanded() {
        return false; // No expansion logic needed
    }
}
