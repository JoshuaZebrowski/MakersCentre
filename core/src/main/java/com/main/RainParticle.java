package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.graphics.Color.CYAN;

class RainParticle {
    private Vector2 position;
    private float speed;
    private Color colour;
    private Texture texture;

    public RainParticle(float x, float y, float speed, Texture texture) {
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.colour = CYAN;
        this.texture = texture;
    }

    public void update(float delta) {
        position.y -= speed * delta;

        if (position.y < 0) {
            position.set(MathUtils.random(0, Gdx.graphics.getWidth()), Gdx.graphics.getHeight());
            speed = MathUtils.random(100, 300);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(colour);
        batch.draw(texture, position.x, position.y, 4, 20);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
