package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

public class ComputerScreen {
    private AssetManager assetManager;
    private Texture desktopBackground;
    private Texture emailIcon;


    private Texture file;
    private Texture fileIcon;
    private Texture videoIcon;
    private Texture image;

    private int numRows = 5;
    private int numCols = 5;
    private Skin skin;

    // Group for the computer screen UI components (background and app grid)
    private Group container;

    // Group for the background layer
    private Group backgroundLayer;

    // Group for the UI elements (apps, icons, etc.)
    private Group uiLayer;

    public ComputerScreen() {
        assetManager = new AssetManager();
        container = new Group();

        // Create the two layers: background and UI elements
        backgroundLayer = new Group();
        uiLayer = new Group();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Path to your skin JSON file

        // Set container size to the size of the computer screen (e.g., 400x300)
        container.setSize(500, 400);

        // Add the background and UI layers to the container
        container.addActor(backgroundLayer);
        container.addActor(uiLayer);
    }

    /**
     * Build the computer screen UI.
     * This method loads the assets (synchronously for simplicity) and creates the UI elements.
     */
    public void build() {
        // Load assets
        assetManager.load("ui/computer/desktopBackground.png", Texture.class);
        assetManager.load("ui/computer/email_icon.png", Texture.class);

        // joke remove later
        assetManager.load("ui/computer/file.png", Texture.class);
        assetManager.load("ui/computer/video.png", Texture.class);


        assetManager.finishLoading();  // Wait until assets are loaded

        desktopBackground = assetManager.get("ui/computer/desktopBackground.png", Texture.class);
        emailIcon = assetManager.get("ui/computer/email_icon.png", Texture.class);

        // joke remove later
        file = assetManager.get("ui/computer/file.png", Texture.class);
        videoIcon = assetManager.get("ui/computer/video.png", Texture.class);


        // Create and add the background to the backgroundLayer
        Image backgroundImage = new Image(desktopBackground);
        backgroundImage.setSize(container.getWidth(), container.getHeight());
        backgroundLayer.addActor(backgroundImage);

        // Create and add the email icon to the UI layer (on top of the background)

        // Create the grid of interactive squares inside the UI layer
        createGrid();
    }

    private void createGrid() {
        float squareWidth = container.getWidth() / numCols;
        float squareHeight = container.getHeight() / numRows;

        // Loop to create grid squares in the UI layer
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final int finalI = i;
                final int finalJ = j;

                if (i == 4 && j == 0) {
                    // Create and position the email icon
                    Image emailImage = new Image(emailIcon);
                    emailImage.setSize(50, 50);
                    emailImage.setPosition(j * squareWidth, i * squareHeight);  // Corrected positioning
                    uiLayer.addActor(emailImage);

                    emailImage.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            openEmailScreen();
                            System.out.println("Clicked on email icon at (4, 0)");
                        }
                    });



                }else if(i == 2 && j == 0){
                    Image videoImage = new Image(videoIcon);
                    videoImage.setSize(50, 50);
                    videoImage.setPosition(j * squareWidth, i * squareHeight);  // Corrected positioning
                    uiLayer.addActor(videoImage);

                    videoImage.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                        }
                    });
                }else {
                    // Create regular grid square
                    Image square = new Image(desktopBackground);
                    square.setSize(squareWidth, squareHeight);
                    square.setPosition(j * squareWidth, i * squareHeight);
                    square.setColor(1, 1, 1, 0); // Keep transparency

                    uiLayer.addActor(square);

                    square.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (finalI == 0 && finalJ == 4) {
                                // Create a jumpscare image
                                Image image1 = new Image(file);
                                image1.setSize(100, 100); // Start small
                                image1.setPosition(
                                    (container.getWidth() - 100) / 2,
                                    (container.getHeight() - 100) / 2
                                ); // Center it

                                uiLayer.addActor(image1);

                                // Play jumpscare sound
                                SoundManager.getInstance().loadSound("jump", "audio/audio1.mp3");
                                SoundManager.getInstance().playSound("jump");

                                // Animate the image to grow bigger
                                image1.setOrigin(Align.center);  // Set the origin to the center
                                image1.addAction(Actions.sequence(
                                    Actions.scaleTo(5f, 5f, 0.5f),  // Scale up over 0.5 sec
                                    Actions.delay(0.7f),            // Wait for 0.7 sec
                                    Actions.fadeOut(0.3f),          // Fade out in 0.3 sec
                                    Actions.run(() -> image1.remove()) // Remove from UI
                                ));

                            }

                            System.out.println("Clicked on square (" + finalI + ", " + finalJ + ")");
                        }
                    });

                }
            }
        }
    }

    private void openEmailScreen() {
        // Create a Window using the skin. The title will be displayed in the title bar.
        Window emailWindow = new Window("Email", skin);
        emailWindow.setSize(300, 200);

        // Position the window in the center of the container.
        emailWindow.setPosition(
            (container.getWidth() - emailWindow.getWidth()) / 2,
            (container.getHeight() - emailWindow.getHeight()) / 2
        );

        // Create a Label to display the email content.
        String emailContent = "Subject: Welcome\n\nHello,\n\nThis is your first email!\nHave fun exploring.";
        Label emailLabel = new Label(emailContent, skin);
        emailLabel.setWrap(true);  // Allow text to wrap if necessary
        emailWindow.add(emailLabel).expand().fill().row();

        // Create a Close button.
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Remove the window when the close button is clicked.
                emailWindow.remove();
            }
        });
        emailWindow.add(closeButton).padTop(10);

        // Add the window to the UI layer so it appears on top.
        uiLayer.addActor(emailWindow);
    }

    /**
     * Returns the container Group that holds the computer screen UI.
     */
    public Group getContainer() {
        return container;
    }
}
