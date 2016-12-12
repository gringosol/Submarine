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

    public Sprite(GameRenderer renderer, int texResourseId, Map<Integer, Primitive> primitives, float width,
                  float height) {
        this.primitives = primitives;
        scaleX = width;
        scaleY = height;
        texHandle = TextureManager.getTextureHandle(renderer.getActivityContext(), texResourseId);
    }

    public Sprite(GameRenderer renderer, int texHandle, Map<Integer, Primitive> primitives, float size,
                  float [] position) {
        this.primitives = primitives;
        scaleX = size;
        scaleY = size;
        this.texHandle = texHandle;
        this.position = position;
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, int programHandle) {
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
}
