package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WeatherInfoScreen implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture; // Background texture

    public WeatherInfoScreen() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Increase font size
        font.setColor(Color.WHITE);

        // Load the background texture
        backgroundTexture = new Texture(Gdx.files.internal("ui/weatherBackground.png"));

        // Create a table to organize the weather info
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Add a title label at the top
        Label titleLabel = new Label("Weather Information", new Label.LabelStyle(font, Color.YELLOW));
        titleLabel.setFontScale(4f);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(4).center().padBottom(20).row();

        // Create columns for each season
        Table springTable = createSeasonTable("Spring",
            "Sunny: 35% chance\nEffects: +20% Resource Efficiency, +10% Task Speed, +30% Community Morale\n\n" +
                "Partly Cloudy: 20% chance\nEffects: No changes\n\n" +
                "Cloudy: 15% chance\nEffects: -10% Community Morale\n\n" +
                "Rainy: 23% chance\nEffects: -10% Resource Efficiency, -20% Task Speed, -20% Community Morale\n\n" +
                "Thunderstorms: 7% chance\nEffects: -30% Resource Efficiency, -50% Task Speed, -50% Community Morale");

        Table summerTable = createSeasonTable("Summer",
            "Sunny: 50% chance\nEffects: +20% Resource Efficiency, +10% Task Speed, +30% Community Morale\n\n" +
                "Partly Cloudy: 25% chance\nEffects: No changes\n\n" +
                "Cloudy: 15% chance\nEffects: -10% Community Morale\n\n" +
                "Rainy: 8% chance\nEffects: -10% Resource Efficiency, -20% Task Speed, -20% Community Morale\n\n" +
                "Thunderstorms: 2% chance\nEffects: -30% Resource Efficiency, -50% Task Speed, -50% Community Morale");

        Table autumnTable = createSeasonTable("Autumn",
            "Sunny: 10% chance\nEffects: +20% Resource Efficiency, +10% Task Speed, +30% Community Morale\n\n" +
                "Partly Cloudy: 23% chance\nEffects: No changes\n\n" +
                "Cloudy: 15% chance\nEffects: -10% Community Morale\n\n" +
                "Rainy: 50% chance\nEffects: -10% Resource Efficiency, -20% Task Speed, -20% Community Morale\n\n" +
                "Thunderstorms: 2% chance\nEffects: -30% Resource Efficiency, -50% Task Speed, -50% Community Morale");

        Table winterTable = createSeasonTable("Winter",
            "Sunny: 15% chance\nEffects: +20% Resource Efficiency, +10% Task Speed, +30% Community Morale\n\n" +
                "Partly Cloudy: 10% chance\nEffects: No changes\n\n" +
                "Cloudy: 30% chance\nEffects: -10% Community Morale\n\n" +
                "Rainy: 25% chance\nEffects: -10% Resource Efficiency, -20% Task Speed, -20% Community Morale\n\n" +
                "Thunderstorms: 20% chance\nEffects: -30% Resource Efficiency, -50% Task Speed, -50% Community Morale\n\n" +
                "Snow: 0.00000001% chance\nEffects: -70% Resource Efficiency, -90% Task Speed, -50% Community Morale");

        // Add season tables to the main table
        mainTable.add(springTable).pad(20);
        mainTable.add(summerTable).pad(20);
        mainTable.add(autumnTable).pad(20);
        mainTable.add(winterTable).pad(20);

        // Add a description label at the bottom
        Label descriptionLabel = new Label(
            "How Weather Affects Your Turn:\n\n" +
                "- Sunny: +1 Moves.\n" +
                "- Partly Cloudy/Cloudy: 0 Moves.\n" +
                "- Rainy: -1 Moves.\n." +
                "- Thunderstorm/Snow: -2 Moves.",
            new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );
        descriptionLabel.setFontScale(3f);
        descriptionLabel.setAlignment(Align.center);
        mainTable.row();
        mainTable.add(descriptionLabel).colspan(4).center().padTop(20);

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    private Table createSeasonTable(String seasonName, String seasonInfo) {
        Table seasonTable = new Table();
        seasonTable.defaults().pad(10);

        // Add season name as a header
        Label seasonLabel = new Label(seasonName, new Label.LabelStyle(font, Color.YELLOW));
        seasonLabel.setAlignment(Align.center);
        seasonTable.add(seasonLabel).colspan(2).center().row();

        // Add season info
        Label infoLabel = new Label(seasonInfo, new Label.LabelStyle(font, Color.WHITE));
        infoLabel.setAlignment(Align.left);
        seasonTable.add(infoLabel).colspan(2).left();

        return seasonTable;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw the stage (text and UI elements)
        stage.act(delta);
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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose(); // Dispose of the background texture
    }
}
