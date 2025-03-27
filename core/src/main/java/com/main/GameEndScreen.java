package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;

public class GameEndScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private ScrollPane scrollPane;
    private Table timeline;
    private BitmapFont font;
    private float scrollSpeed = 100f;
    private float totalScrollWidth;
    private Table playerStatsPanel;
    private Label playerStatsLabel;
    private boolean outcome;

    public GameEndScreen(boolean outcome) {
        this.outcome = outcome;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);
        stage.addActor(root);

        Label title = new Label("", skin, "default");
        if (outcome) {
            title = new Label("Finished ... You Won!!!", skin, "default");
        } else {
            title = new Label("Finished... You Lost", skin, "default");
        }

        title.setFontScale(2);
        root.add(title).colspan(3).center().padBottom(20);
        root.row();

        createPlayerStatsPanel();
        root.add(playerStatsPanel).pad(10);

        timeline = createYearTimeline();
        totalScrollWidth = 4 * (300 + 30);

        Table wrappedTimeline = new Table();
        wrappedTimeline.add(timeline);
        wrappedTimeline.add(createYearTimeline());

        scrollPane = new ScrollPane(wrappedTimeline, skin);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setScrollBarPositions(false, false);
        scrollPane.setForceScroll(false, false);
        scrollPane.setScrollX(0);
        scrollPane.setTouchable(Touchable.disabled);

        root.row();
        root.add(scrollPane).colspan(3)
            .height(200)
            .width(Gdx.graphics.getWidth() - 100)
            .expandY()
            .bottom()
            .padBottom(20);

        TextButton restartButton = new TextButton("Menu", new TextButton.TextButtonStyle(null, null, null, font));
        restartButton.getLabel().setFontScale(2f);
        restartButton.setColor(Color.GREEN);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Tooltip.getInstance().resetInstance();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        root.add(restartButton).center().padBottom(30);
        root.row();

        TextButton quitGame = new TextButton("Quit", new TextButton.TextButtonStyle(null, null, null, font));
        quitGame.getLabel().setFontScale(2f);
        quitGame.setColor(Color.GREEN);
        quitGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Gdx.app.exit();
            }
        });

        root.add(quitGame).center().padBottom(40);
    }

    private void createPlayerStatsPanel() {
        playerStatsPanel = new Table(skin);
        playerStatsPanel.setBackground("default-round");

        Table buttonRow = new Table(skin);
        int numPlayers = PlayerManager.getInstance().getPlayers().size();

        for (int i = 0; i < numPlayers; i++) {
            final int playerIndex = i;
            TextButton button = new TextButton("Player " + (i + 1), skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updatePlayerStats(playerIndex);
                }
            });
            buttonRow.add(button).pad(5);
        }

        playerStatsLabel = new Label("Select a player to view stats.", skin);
        playerStatsLabel.setAlignment(Align.topLeft);
        playerStatsLabel.setWrap(true);
        playerStatsLabel.setFontScale(1.0f);

        playerStatsPanel.add(buttonRow).row();
        playerStatsPanel.add(playerStatsLabel).width(250).pad(10).left().top();
    }

    private void updatePlayerStats(int index) {
        Player player = PlayerManager.getInstance().getPlayer(index);
        List<String> pCategories = player.pastCategories;
        List<Task> pTasks = player.pastTaskList;
        double amountDonated = player.amountDoanted;

        StringBuilder achievementsInfo = new StringBuilder("Tasks Complete:\n");
        StringBuilder mistakesInfo = new StringBuilder("Categories Complete:\n");

        if (pTasks != null && !pTasks.isEmpty()) {
            achievementsInfo.append("Total tasks completed: ").append(pTasks.size()).append("\n");
            for (Task task : pTasks) {
                achievementsInfo.append("• ").append(task.getName()).append("\n");
            }
        } else {
            achievementsInfo.append("No tasks completed\n");
        }

        if (pCategories != null && !pCategories.isEmpty()) {
            mistakesInfo.append("Total categories completed: ").append(pCategories.size()).append("\n");
            for (String category : pCategories) {
                mistakesInfo.append("• ").append(category).append("\n");
            }
        } else {
            mistakesInfo.append("No categories completed\n");
        }

        String donationInfo = "\nAmount Donated: " + String.format("%.2f", amountDonated) + " rand";
        String info = achievementsInfo.toString() + "\n\n" + mistakesInfo.toString() + donationInfo;

        playerStatsLabel.setText(info);
    }

    private Table createYearTimeline() {
        Table timeline = new Table();
        String[] years;

        if (outcome) {
            years = new String[] {
                "1 Year After...\n• Increase in potential businesses.\n• Increase in attendance at tertiary classes.",
                "2 Years After...\n• Starter businesses are growing.\n• Entrepreneurs are confident and sustainable.",
                "3 Years After...\n• Better business + IT knowledge.\n• Decreasing unemployment.",
                "4 Years After...\n• More qualified residents.\n• Rising economic stability."
            };
        } else {
            years = new String[] {
                "1 Year After...\n• Business growth stalls without support.\n• Class attendance drops without a learning space.",
                "2 Years After...\n• Starter businesses struggle without support.\n• Job opportunities remain scarce.",
                "3 Years After...\n• Limited skills hinder innovation.\n• Startups fail from lack of support.",
                "4 Years After...\n• Lack of education limits skills.\n• Poverty and unemployment persist."
            };
        }

        float boxWidth = 300f;
        float boxHeight = 150f;

        for (String yearText : years) {
            Table yearBox = new Table(skin);
            yearBox.setBackground("default-round");

            Label label = new Label(yearText, skin);
            label.setWrap(true);
            label.setFontScale(1.1f);

            yearBox.add(label).width(boxWidth).height(boxHeight).pad(15);
            timeline.add(yearBox).width(boxWidth).height(boxHeight).padRight(30);
        }

        return timeline;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        float currentScroll = scrollPane.getScrollX();
        currentScroll += delta * scrollSpeed;

        if (currentScroll >= totalScrollWidth) {
            currentScroll -= totalScrollWidth;
        }

        scrollPane.setScrollX(currentScroll);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
