package com.gameinstance.submarine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gringo on 25.12.2016 16:13.
 *
 */

public class Layerset {
    public List<String> layerNames;
    public int [] target = null;
    public float [] projectionMatrix;
    public int [] viewport;
    boolean collide = false;

    public Layerset(List<String> layerNames, int[] target, float [] projectionMatrix,
                    int [] viewport, boolean collide) {
        this.layerNames = layerNames;
        this.target = target;
        this.projectionMatrix = projectionMatrix;
        this.viewport = viewport;
        this.collide = collide;
    }
}
