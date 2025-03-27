package com.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class ThunderstormEffect {
    private Texture cloudTexture;
    private Texture rainTexture;
    private List<Cloud> clouds;
    private List<RainParticle> rainParticles;
    private float cloudWidth, cloudHeight;
    private OrthographicCamera camera;
    private String currentWeather;
    private float boardMinX, boardMaxX, boardMinY, boardMaxY;

    private class Cloud {
        float x, y, scaleX, scaleY, driftX, driftY;

        Cloud(float x, float y, float scaleX, float scaleY, float driftX, float driftY) {
            this.x = x;
            this.y = y;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.driftX = driftX;
            this.driftY = driftY;
        }
    }

    private class RainParticle {
        Vector2 position;
        float speed;
        Color colour;
        Cloud parentCloud;
        Vector2 offset;
        Vector2 lastCloudPos;

        RainParticle(Cloud cloud, float offsetX, float offsetY, float speed, Color colour) {
            this.parentCloud = cloud;
            this.offset = new Vector2(offsetX, offsetY);
            this.speed = speed;
            this.colour = colour;
            this.lastCloudPos = new Vector2(cloud.x, cloud.y);
            this.position = new Vector2(cloud.x + offsetX, cloud.y + offsetY);
        }

        void update(float delta) {
            float dx = parentCloud.x - lastCloudPos.x;
            float dy = parentCloud.y - lastCloudPos.y;
            position.add(dx, dy);
            position.y -= speed * delta;
            lastCloudPos.set(parentCloud.x, parentCloud.y);

            if (position.y < boardMinY) {
                offset.x = MathUtils.random(0, cloudWidth * parentCloud.scaleX);
                offset.y = MathUtils.random(-cloudHeight * parentCloud.scaleY, 0);
                position.set(parentCloud.x + offset.x, parentCloud.y + offset.y);
                speed = MathUtils.random(100, 300);
            }
        }

        void draw(SpriteBatch batch) {
            batch.setColor(colour);
            batch.draw(rainTexture, position.x, position.y, 2, 10);
            batch.setColor(Color.WHITE);
        }
    }

    public ThunderstormEffect(OrthographicCamera camera, String currentWeather, List<Node> nodes) {
        this.camera = camera;
        this.currentWeather = currentWeather;
        cloudTexture = new Texture("ui/cloud.png");
        rainTexture = new Texture("ui/rain.png");
        cloudWidth = cloudTexture.getWidth();
        cloudHeight = cloudTexture.getHeight();
        clouds = new ArrayList<>();
        rainParticles = new ArrayList<>();
        updateBoardBounds(nodes);
        initialiseClouds();
    }

    private void initialiseClouds() {
        int numClouds = getCloudCountBasedOnWeather();
        clouds.clear();

        for (int i = 0; i < numClouds; i++) {
            float randomScaleX = MathUtils.random(0.7f, 1.2f);
            float randomScaleY = MathUtils.random(0.7f, 1.2f);
            float xPosition = MathUtils.random(boardMinX, boardMaxX + 100 - cloudWidth * randomScaleX);
            float yPosition = MathUtils.random(boardMinY, boardMaxY + 100 - cloudHeight * randomScaleY);
            yPosition = MathUtils.clamp(yPosition, boardMinY, boardMaxY - cloudHeight * randomScaleY);
            float driftX = MathUtils.random(-2f, 2f);
            float driftY = MathUtils.random(-1f, 1f);

            Cloud cloud = new Cloud(xPosition, yPosition, randomScaleX, randomScaleY, driftX, driftY);
            clouds.add(cloud);

            for (int j = 0; j < 30; j++) {
                float offsetX = MathUtils.random(0, cloudWidth * cloud.scaleX);
                float offsetY = MathUtils.random(0, cloudHeight * cloud.scaleY);
                rainParticles.add(new RainParticle(cloud, offsetX, offsetY, MathUtils.random(100, 300), Color.CYAN));
            }
        }
    }

    private int getCloudCountBasedOnWeather() {
        switch (currentWeather) {
            case "Thunderstorms":
            case "Cloudy":
                return 10;
            case "Partly Cloudy":
                return 5;
            case "Clear":
            default:
                return 0;
        }
    }

    public void update(float delta, String currentWeather) {
        if (!this.currentWeather.equals(currentWeather)) {
            this.currentWeather = currentWeather;
            initialiseClouds();
        }

        for (Cloud cloud : clouds) {
            cloud.x += cloud.driftX * delta;
            cloud.y += cloud.driftY * delta;

            if (cloud.x > boardMaxX) cloud.x = boardMinX - cloudWidth * cloud.scaleX;
            if (cloud.x < boardMinX - cloudWidth * cloud.scaleX) cloud.x = boardMaxX;
            if (cloud.y > boardMaxY) cloud.y = boardMinY - cloudHeight * cloud.scaleY;
            if (cloud.y < boardMinY - cloudHeight * cloud.scaleY) cloud.y = boardMaxY;
        }

        for (RainParticle rain : rainParticles) {
            rain.update(delta);
        }
    }

    public void render(SpriteBatch batch, float zoom) {
        batch.setProjectionMatrix(camera.combined);

        if ("Thunderstorms".equals(currentWeather)) {
            for (RainParticle rain : rainParticles) {
                rain.draw(batch);
            }
        }

        if (zoom >= 0.3) {
            float cloudAlpha = "Thunderstorms".equals(currentWeather) ? 0.8f : 0.6f;
            batch.setColor(1f, 1f, 1f, cloudAlpha);

            for (Cloud cloud : clouds) {
                TextureRegion cloudRegion = new TextureRegion(cloudTexture);
                batch.draw(cloudRegion, cloud.x, cloud.y, cloudWidth / 2, cloudHeight / 2, cloudWidth, cloudHeight,
                    cloud.scaleX, cloud.scaleY, 0);
            }

            batch.setColor(Color.WHITE);
        }
    }

    public void updateBoardBounds(List<Node> nodes) {
        boardMinX = Float.MAX_VALUE;
        boardMaxX = Float.MIN_VALUE;
        boardMinY = Float.MAX_VALUE;
        boardMaxY = Float.MIN_VALUE;

        for (Node node : nodes) {
            boardMinX = Math.min(boardMinX, node.x);
            boardMaxX = Math.max(boardMaxX, node.x);
            boardMinY = Math.min(boardMinY, node.y);
            boardMaxY = Math.max(boardMaxY, node.y);
        }
        boardMaxY = boardMinY + 500;
        boardMinY = boardMinY + 200;
        boardMaxX = boardMaxX - 10;
    }

    public void dispose() {
        if (cloudTexture != null) {
            cloudTexture.dispose();
        }
        if (rainTexture != null) {
            rainTexture.dispose();
        }
    }
}
