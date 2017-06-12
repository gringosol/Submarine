package com.gameinstance.submarine;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Map;

/**
 * Created by gringo on 28.11.2016 20:38.
 *
 */

public class Sprite {
    float [] position = new float [] {0, 0};
    float angle;
    Map<Integer, Primitive> primitives;
    float scaleX;
    float scaleY;
    float [] modelMatrix = new float[16];
    float [] scaleMatrix = new float[16];
    float [] translateMatrix = new float[16];
    float [] rotateMatrix = new float[16];
    int texHandle;
    Animation animation;
    boolean visible = true;
    float transparency = 1.0f;

    public Sprite(GameRenderer renderer, int texResourseId, Map<Integer, Primitive> primitives, float width,
                  float height) {
        this.primitives = primitives;
        scaleX = width;
        scaleY = height;
        texHandle = TextureManager.getTextureHandle(renderer.getActivityContext(), texResourseId);
    }

    public Sprite(int texHandle, Map<Integer, Primitive> primitives, float size,
                   float [] position) {
        this.primitives = primitives;
        scaleX = size;
        scaleY = size;
        this.texHandle = texHandle;
        this.position = position;
    }

    public Sprite(int texHandle, Map<Integer, Primitive> primitives, float [] size,
                  float [] position) {
        this.primitives = primitives;
        scaleX = size[0];
        scaleY = size[1];
        this.texHandle = texHandle;
        this.position = position;
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, int programHandle) {
        if (!visible)
            return;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.translateM(translateMatrix, 0, position[0], position[1], 0);
        Matrix.rotateM(rotateMatrix, 0, angle, 0, 0, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, rotateMatrix, 0, scaleMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translateMatrix, 0, modelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle);
        primitives.get(programHandle).draw(projectionMatrix, viewMatrix, modelMatrix);
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, float [] color, int programHandle) {
        if (!visible)
            return;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.translateM(translateMatrix, 0, position[0], position[1], 0);
        Matrix.rotateM(rotateMatrix, 0, angle, 0, 0, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, rotateMatrix, 0, scaleMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translateMatrix, 0, modelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle);
        primitives.get(programHandle).draw(projectionMatrix, viewMatrix, modelMatrix, color);
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, float transparency, int programHandle) {
        if (!visible)
            return;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.translateM(translateMatrix, 0, position[0], position[1], 0);
        Matrix.rotateM(rotateMatrix, 0, angle, 0, 0, 1.0f);
        Matrix.multiplyMM(modelMatrix, 0, rotateMatrix, 0, scaleMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translateMatrix, 0, modelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle);
        primitives.get(programHandle).draw(projectionMatrix, viewMatrix, modelMatrix, transparency);
    }

    public void setPosition(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    public void setRotation(float angle) {
        this.angle = angle;
    }

    public float [] getPosition() {
        return position;
    }

    public void setTexHandle(int texHandle) {
        this.texHandle = texHandle;
    }

    public void setScale(float sx, float sy) {
        scaleX = sx;
        scaleY = sy;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void playAnimation() {
        animation.play(this);
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean getVisible() {
        return  visible;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    public float getTransparency() {
        return transparency;
    }
}
