package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.main.tooltips.TooltipPosition;

import java.util.HashMap;
import java.util.Map;

public class Tooltip {
    private static Tooltip instance;
    private final Array<TooltipEntry> tooltips = new Array<>();
    private final BitmapFont font;
    private final SpriteBatch batch;
    private boolean visible = true;
    private Texture whiteTexture;
    private boolean tutorialMode = false;


    private Tooltip() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        batch = new SpriteBatch();

        // Create a 1x1 white texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void setVisible(String id, boolean visible) {
        for (TooltipEntry entry : tooltips) {
            if (entry.id.equals(id)) {
                entry.visible = visible;
            }
        }
    }


    public static Tooltip getInstance() {
        if (instance == null) {
            instance = new Tooltip();
        }
        return instance;
    }

    public void setTutorialMode(boolean tutorialMode){
        this.tutorialMode = tutorialMode;
    }

    // Add tooltip with a specific position
    public void addTooltip(String id, String text, TooltipPosition position) {
        tooltips.add(new TooltipEntry(id, text, null, position, true, false));
    }

    public void addTooltip(String id, String text, TooltipPosition position, boolean clear, boolean highlight) {
        tooltips.add(new TooltipEntry(id, text, null, position, clear, highlight));
    }

    // Add tooltip with text, image, and position
    public void addTooltip(String id, String text, String imagePath, TooltipPosition position) {
        Texture image = new Texture(Gdx.files.internal(imagePath));
        tooltips.add(new TooltipEntry(id, text, image, position, true, false));
    }

    // Add tooltip with text, image, position, clear and highlight
    public void addTooltip(String id, String text, String imagePath, TooltipPosition position, boolean clear, boolean highlight) {
        Texture image = new Texture(Gdx.files.internal(imagePath));
        tooltips.add(new TooltipEntry(id, text, image, position, clear, highlight));
    }

    public void clear() {
        for(TooltipEntry entry : tooltips) {
            if (entry.clear){
                entry.visible = false;
//                tooltips.removeValue(entry, true);
            }
        }
    }

    public void setVisible(String id){
        for (TooltipEntry entry : tooltips) {
            if (entry.id.equals(id)) {
                entry.clear = false;
            }
        }
    }

    // Clears via id
    public void clear(String id) {
        for(TooltipEntry entry : tooltips) {
            if (entry.clear && entry.id.equals(id)) {
                entry.visible = false;
//                tooltips.removeValue(entry, true);
            }
        }
    }

    public void render(Camera uiCamera, float screenWidth, float screenHeight) {
        if (tooltips.isEmpty()) return;

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Store last used Y positions for each TooltipPosition
        Map<TooltipPosition, Float> lastYPositions = new HashMap<>();

        for (TooltipEntry entry : tooltips) {
            if (tutorialMode && !entry.id.startsWith("tut")) continue; // Only show tutorial tooltips
            if (!tutorialMode && entry.id.startsWith("tut")) continue; // Hide tutorial tooltips

            if(entry.visible){
                GlyphLayout layout = new GlyphLayout(font, entry.text);
                float textWidth = layout.width;
                float x, y;

                // Determine the base position
                switch (entry.position) {
                    case BOTTOM:
                        x = screenWidth / 2 - textWidth / 2 ;
                        y = 20;
                        break;
                    case BOTTOM_RIGHT:
                        x = screenWidth - 50 - textWidth;
                        y = lastYPositions.getOrDefault(TooltipPosition.BOTTOM_RIGHT, 40f);
                        break;
                    case BOTTOM_LEFT:
                        x = 20;
                        y = lastYPositions.getOrDefault(TooltipPosition.BOTTOM_LEFT, 40f);
                        break;
                    case TOP_LEFT:
                        x = 20;
                        y = lastYPositions.getOrDefault(TooltipPosition.TOP_LEFT, screenHeight - 20);
                        break;
                    case TOP_RIGHT:
                        x = screenWidth - 20 - textWidth;
                        y = lastYPositions.getOrDefault(TooltipPosition.TOP_RIGHT, screenHeight - 20);
                        break;
                    case CENTER:
                        x = screenWidth / 2 - textWidth / 2;
                        y = screenHeight / 2;
                        break;
                    case CLICK_ROLL:
                        x = screenWidth / 2 - textWidth / 2;
                        y = screenHeight / 2 - 100; //130
                        break;
                    default:
                        continue; // Skip if invalid position
                }

                if (entry.highlight) {
                    batch.setColor(0, 0, 0, 0.5f); // Semi-transparent black
                    batch.draw(whiteTexture, x, y - layout.height, textWidth, layout.height);
                    batch.setColor(1, 1, 1, 1); // Reset color to default
                }

                // Draw tooltip
                font.draw(batch, entry.text, x, y);

                // Draw optional image first (if exists)
                if (entry.image != null) {
                    batch.draw(entry.image, x + textWidth, y - layout.height - 10, 32, 32);
                }

                // Update last Y position to prevent overlap (move up by height + padding)
                lastYPositions.put(entry.position, y + layout.height + 20);
            }
        }

        batch.end();
    }

    public void setVisible() {
        this.visible = !this.visible;
    }

    public boolean isVisible() {
        return visible;
    }



    private static class TooltipEntry {
        String id;
        String text;
        Texture image;
        TooltipPosition position;
        boolean clear;
        boolean highlight;
        boolean visible = true;

        TooltipEntry(String id, String text, Texture image, TooltipPosition position, Boolean clear, boolean highlight) {
            this.id = id;
            this.text = text;
            this.image = image;
            this.position = position;
            this.clear = clear;
            this.highlight = highlight;
        }

    }

}
