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

    public void setTarget(Sprite sprite) {
        target = sprite;
    }

    public void setBounds(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public float [] update(float [] mViewMatrix, float aspect, float scale) {
        if (target != null) {
            float x = -target.getPosition()[0];
            if (x < minX + scale / aspect || x > maxX - scale / aspect)
                x = mViewMatrix[12];
            float y = -target.getPosition()[1];
            if (y < minY + scale || y > maxY - scale)
                y = mViewMatrix[13];
            Matrix.setIdentityM(mViewMatrix, 0);
            Matrix.translateM(mViewMatrix, 0, x, y, 0);
        }
        return mViewMatrix;
    }
}
