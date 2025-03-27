package com.main;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.ShadowMap;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.AmbientLight;

public class Dice {
    private ModelInstance diceInstance;
    private Model diceModel;
    private Texture[] faceTextures;
    private Environment environment;
    private boolean isRolling;
    private float rotationSpeedX, rotationSpeedY, rotationSpeedZ;
    private float rotationTimeLeft;
    private int faceValue;
    private boolean visible = false;
    private boolean alreadyRolled = false;

    private DirectionalLight directionalLight;
    private ShadowMap shadowMap;

    public Dice(Texture[] textures) {
        this.faceTextures = textures;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder partBuilder;
        Material[] faceMaterials = new Material[6];

        for (int i = 0; i < 6; i++) {
            faceMaterials[i] = new Material(TextureAttribute.createDiffuse(faceTextures[i]));
        }

        partBuilder = modelBuilder.part("front", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[0]);
        partBuilder.rect(-0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f,  0.5f, 0.5f,
            -0.5f,  0.5f, 0.5f,
            0f, 0f, 1f);

        partBuilder = modelBuilder.part("back", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, faceMaterials[5]);
        partBuilder.rect(0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0f, 0f, -1f);

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
        alignFaceUp(1);

        environment = new Environment();
        environment.set(new ColorAttribute(AmbientLight, 0.8f, 0.8f, 0.8f, 1f));

        isRolling = false;
        faceValue = 1;
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

    private void alignFaceUp(int faceValue) {
        diceInstance.transform.idt();
        switch (faceValue) {
            case 1:
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
            rotationTimeLeft -= delta;
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
            rotationTimeLeft = 1.5f;
            rotationSpeedX = MathUtils.random(200f, 400f);
            rotationSpeedY = MathUtils.random(200f, 400f);
            rotationSpeedZ = MathUtils.random(200f, 400f);
        }
    }

    public Vector3 getPosition() {
        Vector3 position = new Vector3();
        diceInstance.transform.getTranslation(position);
        return position;
    }

    private void setFinalFace() {
        faceValue = MathUtils.random(1, 6);
        System.out.println("Rolled: " + faceValue);
        alignFaceUp(faceValue);
    }

    public void onClicked() {
        roll();
    }

    public void resetFace(){
        faceValue = 1;
        alignFaceUp(faceValue);
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
}
