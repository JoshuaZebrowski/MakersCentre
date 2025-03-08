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
    private Stage stage;
    private ImageButton smallSquareButton;
    private Table expandedTable;
    private boolean isExpanded = false;
    private Skin skin;
    private Player player;
    private boolean needsRefresh = true; // Flag to trigger refresh


    public playerTab(Player player) {
        // Initialize stage and skin
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create UI elements
        smallSquareButton = createSmallSquareButton();
        this.player = player;

        expandedTable = createExpandedTable();

        // Initially hide the expanded table
        expandedTable.setVisible(false);

        // Add actors to the stage
        stage.addActor(smallSquareButton);
        stage.addActor(expandedTable);


        // Set input processor to handle interactions
        Gdx.input.setInputProcessor(stage);

    }

    private ImageButton createSmallSquareButton() {

        Texture texture = new Texture(Gdx.files.internal("assets/ui/taskButton.png"));

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();

        buttonStyle.up = skin.getDrawable("default-rect");
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(texture)); // Set the texture for the button's up state

        ImageButton button = new ImageButton(buttonStyle);

        button.setSize(100, 50);
        button.setPosition(Gdx.graphics.getWidth() - 75,
            Gdx.graphics.getHeight() - button.getHeight() - 10);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleExpansion(); // Toggle table expansion on click
            }
        });
        return button;
    }

    private Table createExpandedTable() {
        // Create the main table
        Table table = new Table(skin);
        table.setSize(300, 300);
        table.setPosition(Gdx.graphics.getWidth() - table.getWidth() - 10,
            Gdx.graphics.getHeight() - table.getHeight() - 10);

        // Create the square
        Table colorSquare = new Table(skin);
        colorSquare.setBackground(skin.newDrawable("white", 1, 1, 1, 1)); // Initial color: white
        colorSquare.setSize(300, 400); // Adjust size to include tabs
        colorSquare.setPosition(0, -150); // Ensure it's flush to the parent table

        // Create a scrollable container for the main tabs
        Table tabsContainer = new Table(skin);
        ScrollPane scrollPane = new ScrollPane(tabsContainer, skin);
        scrollPane.setScrollingDisabled(false, true); // Horizontal scroll enabled, vertical disabled
        scrollPane.setFadeScrollBars(false);

        // Set the size and position of the scroll pane
        scrollPane.setSize(300, 50);
        scrollPane.setPosition(0, colorSquare.getHeight() - scrollPane.getHeight()); // Attach to the top of the square
        colorSquare.addActor(scrollPane);

        // Prepare a table for displaying subtasks
        Table subTaskDisplayTable = new Table(skin);
        subTaskDisplayTable.setSize(300, 300);
        subTaskDisplayTable.setPosition(0, 50); // Position below the main tabs
        colorSquare.addActor(subTaskDisplayTable);

        // Clear any existing actors in case of reuse
        tabsContainer.clearChildren();
        subTaskDisplayTable.clearChildren();

        // Dynamically generate tabs based on player tasks
        List<Task> tasks = player.getTasks();
        for (Task mainTask : tasks) {
            // Create the main task tab

            if(!mainTask.getIsSubTask()){
                Table mainTab = new Table(skin);
                mainTab.setBackground(skin.newDrawable("white", 1, 1, 1, 0.5f));
                Label mainTabLabel = new Label(mainTask.getName(), skin);
                mainTabLabel.setColor(Color.BLACK);
                mainTab.add(mainTabLabel).left().pad(10);

                // Add click listener for main task
                mainTab.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // Clear the subtask display table
                        subTaskDisplayTable.clearChildren();


                        displayTaskDetails(mainTask, subTaskDisplayTable, colorSquare);

                        // Display the main task details
                        // CHECK IF PLAYER DOESN;T HAVE ONE SUB TASK AND REMIND THE USER TO ADD IT OR SOMETHIGN
                        if (mainTask.getSteps().size() > 1) {
                            if (player.hasSubTasks(mainTask)) {
                                Label taskDetailsLabel = new Label("Steps: " , skin);
                                taskDetailsLabel.setColor(Color.BLACK);
                                subTaskDisplayTable.add(taskDetailsLabel).left().pad(10).row();
                            } else {
                                Label taskDetailsLabel = new Label("Steps: " , skin);
                                taskDetailsLabel.setColor(Color.BLACK);
                                Label tsk = new Label("Need to acquire tasks first", skin);
                                tsk.setColor(Color.BLACK);

                                subTaskDisplayTable.add(taskDetailsLabel).left().pad(10).row();
                                subTaskDisplayTable.add(tsk).left().pad(10).row();

                            }
                        }


                        // Add subtasks to the subtask display table
                        List<Task> subtasks = mainTask.getSteps();
                        if (subtasks != null && !subtasks.isEmpty() && player.hasSubTasks(mainTask)) {
                            for (Task subTask : subtasks) {
                                if (player.hasTask(subTask)) {
                                    // Create a Table for the subtask
                                    Table subTab = new Table(skin);

                                    // Set the background to a square (using a border-like drawable)
                                    subTab.setBackground(skin.newDrawable("white", 1, 1, 1, 0.3f));  // Light background for subtasks
                                    subTab.pad(10);  // Padding around the content

                                    // Create a label to display the subtask name
                                    Label subTabLabel = new Label(subTask.getName(), skin);

                                    // Check if the subtask is completed
                                    if (subTask.getIsCompleted()) {
                                        subTabLabel.setColor(Color.GREEN);  // Turn text green for completed task

                                        // Strike through the text if completed
                                        subTabLabel.setStyle(new Label.LabelStyle(subTabLabel.getStyle().font, Color.GREEN));
//                                        subTabLabel.getStyle().font.getData().strikethrough = true;  // Apply strikethrough

                                        // Disable the click listener for completed subtasks
                                        subTab.addListener(new ClickListener() {
                                            @Override
                                            public void clicked(InputEvent event, float x, float y) {
                                            }
                                        });
                                    } else {
                                        // If task is not complete, use default text color and add a click listener
                                        subTabLabel.setColor(Color.DARK_GRAY); // Default text color

                                        // Add click listener for subtask details
                                        subTab.addListener(new ClickListener() {
                                            @Override
                                            public void clicked(InputEvent event, float x, float y) {
                                                displayTaskDetails(subTask, subTaskDisplayTable, colorSquare); // Display subtask details when clicked
                                            }
                                        });
                                    }

                                    // Add the label to the subtask tab
                                    subTab.add(subTabLabel).center().pad(10);  // Add the label to the subtask tab

                                    // Add the subtask tab to the display table
                                    subTaskDisplayTable.add(subTab).width(280).height(40).pad(5).left().row();
                                }
                            }
                        }


                    }
                });

                // Add the main task tab to the tabs container
                tabsContainer.add(mainTab).width(100).height(40).pad(5).left();
            }
            }


        // Add the color square to the main table
        table.addActor(colorSquare);

        return table;
    }

    // Utility method to display task details
    private void displayTaskDetails(Task task, Table textDisplayTable, Table colorSquare) {
        textDisplayTable.clearChildren();

        if (task != null) {
            Label nameLabel = new Label(task.getName(), skin);
            nameLabel.setColor(Color.BLACK);
            textDisplayTable.add(nameLabel).left().pad(10).row();

            task.getResourceAmount("Money");
            String description = task.getDescription()

                .replace("{m}", task.getResourceAmount("Money"))
                .replace("{p}", task.getResourceAmount("People"));

            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setColor(Color.BLACK);
            descriptionLabel.setWrap(true);
            descriptionLabel.setAlignment(Align.left);
            descriptionLabel.setWidth(colorSquare.getWidth() - 20);

            textDisplayTable.add(descriptionLabel).width(colorSquare.getWidth() - 20).pad(10).left().row();

            List<Resource> resources = task.getResources();
            for (Resource resource : resources) {

                // Create a button for the resource
                TextButton resourceButton = new TextButton(resource.getType(), skin);
                if(resource.isCompleted()){
                    Label completed = new Label("Added " + resource.getType(), skin);
                    completed.setColor(Color.GREEN);
                    textDisplayTable.add(completed).pad(5).width(100).height(30).row();


                }else{
                    resourceButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Handle resource button click
                            Gdx.app.log("Debug","Clicked on resource: " + resource.getType());
                            if(task.getOwner().checkResources(resource));{
                                task.setCompleted(true);
                                resource.setCompleted();
                            }

                        }
                    });

                    // Add the resource button to the text display table
                    textDisplayTable.add(resourceButton).pad(5).width(100).height(30).row();
                }

            }

        }
    }


    private void toggleExpansion() {
        if (isExpanded) {
            expandedTable.setVisible(false); // Hide the expanded table
        } else {
            expandedTable.setVisible(true); // Show the expanded table
        }
        isExpanded = !isExpanded;
    }

    public void playerTarget(Player player) {
        this.player = player;
        needsRefresh = true; // Mark table for refresh when expanded

    }

    public void draw(Batch batch) {
        if (needsRefresh) {
            stage.getActors().removeValue(expandedTable, true); // Remove old table
            expandedTable = createExpandedTable(); // Recreate table
            expandedTable.setVisible(false);
            stage.addActor(expandedTable); // Add updated table
            needsRefresh = false; // Reset refresh flag
        }


        stage.act(Gdx.graphics.getDeltaTime()); // Update stage actions
        stage.draw(); // Render stage


    }

    public Stage getStage() {
        return stage;
    }

public boolean isExpanded() {
        return isExpanded;
    }
}
