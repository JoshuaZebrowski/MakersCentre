package com.main;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static GameState instance;

    public float textScale;
    private boolean colourBlind;
    private Camera uiCamera;
    private Viewport viewport;
    private String currentScreen;
    private int completedAllCategories = 0;
    private boolean removeWeatherEffects = false;
    private boolean forceColourUpdate = false;

    // data
    public int globalTurn;
    public int years;

    private static final List<ColourMap> colourMappings = new ArrayList<>();


    private GameState() {
        this.textScale = 1.0f;
        this.colourBlind = false;

        colourMappings.add(new ColourMap("Green", "#00FF00", "#62E573"));
        colourMappings.add(new ColourMap("Blue", "#0000FF", "#4045D3"));
        colourMappings.add(new ColourMap("Red", "#FF0000", "#84292A"));
        colourMappings.add(new ColourMap("Purple", "#DA70D6", "#D773F5"));
        colourMappings.add(new ColourMap("Yellow", "#FFED3A", "#FFF0AD"));
        colourMappings.add(new ColourMap("White", "#FFFFFF", "#FFFFFF"));

    }

    public void updateData(int globalTurn, int years) {
        this.globalTurn = globalTurn;
        this.years = years;
    }

    public int getGlobalTurn() {
        return globalTurn;
    }

    public int getYears() {
        return years;
    }

    public boolean isForceColourUpdate() {
        return forceColourUpdate;
    }

    public void setForceColourUpdate() {
        this.forceColourUpdate = !forceColourUpdate;
    }

    public String getColourHex(String name) {
        for (ColourMap mapping : colourMappings) {
            if (mapping.getColourName().equalsIgnoreCase(name)) {
                return GameState.getInstance().isColourBlind() ? mapping.getColourBlindHex() : mapping.getStandardHex();
            }
        }
        return "#FFFFFF";
    }


    public boolean isRemoveWeatherEffects() {
        return removeWeatherEffects;
    }
    public void setRemoveWeatherEffects() {
        this.removeWeatherEffects = !removeWeatherEffects;
    }

    public int getCompletedAllCategories() {
        return completedAllCategories;
    }

    public void updateCompletedAllCategories() {
        this.completedAllCategories +=1;
    }

    public void resetCompletedAllCategories() {
        this.completedAllCategories = 0;
    }

    public static GameState getInstance() {
        if (instance == null) {
            synchronized (GameState.class) {
                if (instance == null) {
                    instance = new GameState();
                }
            }
        }
        return instance;
    }
    public String getCurrentScreen() {
        return currentScreen;
    }


    public void setCurrentScreen(String currentScreen) {
        this.currentScreen = currentScreen;
    }

    public void setUiCamera(Camera camera) {
        this.uiCamera = camera;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Camera getUiCamera() {
        return uiCamera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public boolean isColourBlind() {
        return colourBlind;
    }

    public void setColourBlind(){
        colourBlind = !colourBlind;
        forceColourUpdate = true;
    }


    public float getTextScale() {
        return textScale;
    }

    public void setTextScale(float textScale) {
        this.textScale = textScale;
    }



}
