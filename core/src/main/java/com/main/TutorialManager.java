package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import com.main.Tooltip;
import com.main.tooltips.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager {

    private static TutorialManager instance;
    private Map<String, List<String>> tutorialSets; // Stores tutorials by ID
    private List<String> currentTutorialImages; // Active tutorial image list
    private int currentPage;
    private boolean active;
    private ShapeRenderer shapeRenderer;
    private Texture currentTexture;
    private boolean allowNextPage; // True if more than one page
    private String currentTutorialId; // Stores active tutorial ID
    private Texture whiteTexture;

    private TutorialManager() {
        tutorialSets = new HashMap<>();
        shapeRenderer = new ShapeRenderer();
        active = false;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public static TutorialManager getInstance() {
        if (instance == null) {
            instance = new TutorialManager();
        }
        return instance;
    }

    /**
     * Registers a tutorial with a unique ID.
     * @param id A unique string ID for this tutorial.
     * @param imagePaths List of image file paths for this tutorial.
     */
    public void registerTutorial(String id, List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) return;
        tutorialSets.put(id, imagePaths);
    }


    public void startTutorial(String id) {
        if (!tutorialSets.containsKey(id)) return;


        Tooltip.getInstance().setTutorialMode(true);
        currentTutorialId = id;
        currentTutorialImages = tutorialSets.get(id);
        currentPage = 0;
        active = true;
        allowNextPage = currentTutorialImages.size() > 1; // Enable "Next Page" if multiple pages

        loadCurrentImage();
        pauseGame();
        addTutorialTooltips();
    }


    private void loadCurrentImage() {
        if (currentTexture != null) {
            currentTexture.dispose(); // Free memory
        }
        currentTexture = new Texture(currentTutorialImages.get(currentPage));
    }


    public void stopTutorial() {
        active = false;
        Tooltip.getInstance().setTutorialMode(false);
        resumeGame();
        if (currentTexture != null) {
            currentTexture.dispose();
        }
    }


    private void addTutorialTooltips() {
        Tooltip.getInstance().addTooltip("tut", "Exit Tutorial", "ui/toolTips/keyboard_key_escape.png", TooltipPosition.BOTTOM_RIGHT, false, false);
        if (allowNextPage) {
            Tooltip.getInstance().addTooltip("tut", "Next Page", "ui/toolTips/keyboard_key_d.png", TooltipPosition.BOTTOM_RIGHT, false, false);
            Tooltip.getInstance().addTooltip("tut", "< 1 / 2 > ",  TooltipPosition.BOTTOM, false, false);
        }
    }

    private void pauseGame() {
        // Logic to pause the game (e.g., setting a game state)
        Tooltip.getInstance().setVisible("tut");
    }

    private void resumeGame() {
        // Logic to resume the game
        Tooltip.getInstance().clear("tut");
    }


    public void update() {
        if (!active) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopTutorial();
        }

        if (allowNextPage && Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (currentPage < currentTutorialImages.size() - 1) {
                currentPage++;
                loadCurrentImage();
            } else {
                currentPage = 0;
            }
        }
    }


    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        if (!active) return;


        // Draw dark transparent background

//        batch.setColor(0, 0, 0, 1f); // Semi-transparent black
//        batch.draw(whiteTexture, screenWidth, screenHeight);
//        batch.setColor(1, 1, 1, 1); // Reset color to default

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, .1f);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();

        batch.begin(); // Restart batch


        // Draw the tutorial image centered
        // Get original image size
        float imageWidth = currentTexture.getWidth();
        float imageHeight = currentTexture.getHeight();

        // Calculate scale factor to fit screen
        float scaleX = screenWidth / imageWidth;
        float scaleY = screenHeight / imageHeight;
        float scale = Math.min(scaleX, scaleY); // Maintain aspect ratio

        // Calculate new width & height
        float newWidth = imageWidth * scale;
        float newHeight = imageHeight * scale;

        // Center the image
        float x = (screenWidth - newWidth) / 2;
        float y = (screenHeight - newHeight) / 2;

        // Draw scaled image
        batch.draw(currentTexture, x, y, newWidth, newHeight);

        batch.end();
    }

    public boolean isActive() {
        return active;
    }
}
