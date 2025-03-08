package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.main.tooltips.TooltipPosition;




public class Dice {
    private ModelInstance diceInstance;
    private Model diceModel;
    private Texture[] faceTextures; // Array of dice face textures
    private Environment environment;
    private boolean isRolling;
    private float rotationSpeedX, rotationSpeedY, rotationSpeedZ; // Rotation speeds
    private float rotationTimeLeft;
    private int faceValue;
    private boolean visible = false;
    private boolean alreadyRolled = false;

    public Dice(Texture[] textures) {
        this.faceTextures = textures;

        // Create the 3D dice cube
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        // Define the six faces of the cube with their corresponding textures
        MeshPartBuilder partBuilder;
        Material[] faceMaterials = new Material[6];

        // Create materials for each face with the given textures
        for (int i = 0; i < 6; i++) {
            faceMaterials[i] = new Material(TextureAttribute.createDiffuse(faceTextures[i]));
        }

        // Create each face of the cube with the correct opposite side pairing
        // Face 1 (Front) - Opposite to Face 6 (Back)
        partBuilder = modelBuilder.part("front", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[0]);
        partBuilder.rect(-0.5f, -0.5f, 0.5f,  // Bottom-left
            0.5f, -0.5f, 0.5f,  // Bottom-right
            0.5f,  0.5f, 0.5f,  // Top-right
            -0.5f,  0.5f, 0.5f,  // Top-left
            0f, 0f, 1f);        // Normal

        partBuilder = modelBuilder.part("back", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[5]);
        partBuilder.rect(0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0f, 0f, -1f);

        // Face 2 (Right) - Opposite to Face 5 (Left)
        partBuilder = modelBuilder.part("right", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[1]);
        partBuilder.rect(0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, 0.5f,
            1f, 0f, 0f);

        partBuilder = modelBuilder.part("left", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[4]);
        partBuilder.rect(-0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f,  0.5f, 0.5f,
            -0.5f,  0.5f, -0.5f,
            -1f, 0f, 0f);

        // Face 3 (Top) - Opposite to Face 4 (Bottom)
        partBuilder = modelBuilder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[2]);
        partBuilder.rect(-0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0f, 1f, 0f);

        partBuilder = modelBuilder.part("bottom", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[3]);
        partBuilder.rect(-0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0f, -1f, 0f);

        diceModel = modelBuilder.end();
        diceInstance = new ModelInstance(diceModel);

        // Align face 1 facing up by default when initialized
        alignFaceUp(1);

        // Set up environment lighting
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));

        isRolling = false;
        faceValue = 1; // Initially showing face 1
    }


    public boolean getIsVisible(){
        return visible;
    }

    public void setIsVisible(boolean visible){
        this.visible = visible;
    }


    public void setRolling(boolean rolling){
        this.isRolling = rolling;
    }
    public boolean isRolling(){
        return isRolling;
    }

    // Method to align face 1 up
    private void alignFaceUp(int faceValue) {
        // Reset the dice's transform to identity to avoid any leftover rotations
        diceInstance.transform.idt();

        // Align the correct face to the positive Z-axis (camera view)
        switch (faceValue) {
            case 1:
                // Face 1 is already aligned by default (no rotation needed)
                break;
            case 2:
                diceInstance.transform.rotate(Vector3.Y, 270);
                break;
            case 3:
                diceInstance.transform.rotate(Vector3.Y, -180);
                diceInstance.transform.rotate(Vector3.X, -90);
                diceInstance.transform.rotate(Vector3.Y, 180);

                break;
            case 4:
                diceInstance.transform.rotate(Vector3.Y, -180);
                diceInstance.transform.rotate(Vector3.X, 90);
                diceInstance.transform.rotate(Vector3.Y, 180);


                break;
            case 5:
                diceInstance.transform.rotate(Vector3.Y, 90);

                break;
            case 6:
                diceInstance.transform.rotate(Vector3.Y, 180);
                break;
        }
    }


    public void update(float delta) {
        if (isRolling) {
            // Reduce rolling time
            rotationTimeLeft -= delta;

            // Apply random rotations
            diceInstance.transform.rotate(Vector3.X, rotationSpeedX * delta);
            diceInstance.transform.rotate(Vector3.Y, rotationSpeedY * delta);
            diceInstance.transform.rotate(Vector3.Z, rotationSpeedZ * delta);

            if (rotationTimeLeft <= 0) {
                isRolling = false;
                setFinalFace();
                alreadyRolled = true;

            }
        }

    }

    public void setAlreadyRolled(boolean alreadyRolled) {
        this.alreadyRolled = alreadyRolled;
    }

    public boolean isAlreadyRolled(){
        return alreadyRolled;
    }

    public void roll() {
        if (!isRolling) {
            isRolling = true;
            rotationTimeLeft = 2f; // Roll for 2 seconds
            rotationSpeedX = MathUtils.random(200f, 400f);
            rotationSpeedY = MathUtils.random(200f, 400f);
            rotationSpeedZ = MathUtils.random(200f, 400f);
        }
    }

    public Vector3 getPosition() {
        // Retrieve the translation part of the dice's transform matrix
        Vector3 position = new Vector3();
        diceInstance.transform.getTranslation(position);
        return position;
    }

    private void setFinalFace() {


        faceValue = MathUtils.random(1, 6);
        System.out.println("Rolled: " + faceValue);
        alignFaceUp(faceValue); // Orient the cube to the correct face
    }

    public void onClicked() {

        roll(); // Trigger rolling when clicked
    }

    public void resetFace(){
        faceValue = 1;
        alignFaceUp(faceValue); // Ensure the dice is oriented with face 1 up
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(diceInstance, environment);
    }

    public int getFaceValue() {
        return faceValue;
    }

    public void dispose() {
        diceModel.dispose();
        for (Texture texture : faceTextures) {
            texture.dispose();
        }
    }

    public Sound getSound(){
        int numb = MathUtils.random(1, 28);
        return Gdx.audio.newSound(Gdx.files.internal("sounds/diceRoll/dice-"+numb+".wav"));
    }
}
