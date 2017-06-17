package com.gameinstance.submarine;

import android.opengl.Matrix;

/**
 * Created by gringo on 18.12.2016 7:34.
 *
 */
public class Camera {
    Sprite target = null;
    float minX = -1.0f;
    float maxX = 1.0f;
    float minY = -1.0f;
    float maxY = 1.0f;
    float horOffset = 0.0f;
    float [] pointTarget;
    public enum Mode {
        SPRITE,
        POINT
    }
    Mode mode = Mode.SPRITE;

    public void setTarget(Sprite sprite) {
        mode = Mode.SPRITE;
        target = sprite;
    }

    public void setBounds(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public float [] update(float [] mViewMatrix, float aspect, float scale) {
        if (target != null && mode == Mode.SPRITE) {
            mViewMatrix = updateMatrix(mViewMatrix, aspect, scale, target.getPosition());
        } else if (pointTarget != null && mode == Mode.POINT) {
            mViewMatrix = updateMatrix(mViewMatrix, aspect, scale, pointTarget);
        }
        return mViewMatrix;
    }

    private float [] updateMatrix(float [] mViewMatrix, float aspect, float scale, float [] pos) {
        float x = -pos[0] + horOffset;
        if (x < minX + scale / aspect)
            x = minX + scale / aspect;
        if (x > maxX - scale / aspect)
            x = maxX - scale / aspect;
        float y = -pos[1];
        if (y < minY + scale)
            y = minY + scale;
        if (y > maxY - scale)
            y = maxY - scale;
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, x, y, 0);
        return mViewMatrix;
    }

    public float [] getPosition(){
        if (mode == Mode.SPRITE && target != null)
            return target.getPosition();
        else if (mode == Mode.POINT && pointTarget != null)
            return pointTarget;
        else
            return new float[] {0, 0};
    }

    public void setHorOffset(float offset) {
        horOffset = offset;
    }

    public void setPointTarget(float [] pos) {
        mode = Mode.POINT;
        pointTarget = pos;
    }

    public Mode getMode() {
        return mode;
    }
}
