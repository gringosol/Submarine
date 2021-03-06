package com.gameinstance.submarine;

import android.opengl.Matrix;

import java.util.List;

/**
 * Created by gringo on 25.12.2016 16:13.
 *
 */

public class Layerset {
    public List<String> layerNames;
    public int [] target = null;
    public float [] projectionMatrix;
    public float [] viewMatrix = new float[16];
    public int [] viewport;
    boolean collide = false;
    boolean enabled = true;

    public Layerset(List<String> layerNames, int[] target, float [] projectionMatrix,
                    int [] viewport, boolean collide) {
        this.layerNames = layerNames;
        this.target = target;
        this.projectionMatrix = projectionMatrix;
        this.viewport = viewport;
        this.collide = collide;
        Matrix.setIdentityM(viewMatrix, 0);
    }

    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean getEnabled() {
        return enabled;
    }
}
