package com.gameinstance.submarine;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gringo on 11.12.2016 20:02.
 *
 */
public class Scene {
    Map<String, List<String>> layerSets = new HashMap<>();
    Map<String, Layer> layers = new HashMap<>();
    List<Movable> movables = new ArrayList<>();
    GameRenderer renderer;
    String currentSet = null;

    public Scene(GameRenderer renderer){
        this.renderer = renderer;
    }

    public void addLayerSet(String setName, List<String> layerSet) {
        layerSets.put(setName, layerSet);
    }

    public void addLayer(String name, Layer layer) {
        layers.put(name, layer);
    }

    public void setLayerSet(String name) {
        if (layerSets.containsKey(name))
            currentSet = name;
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix) {
        if (currentSet != null) {
            for (String layerName : layerSets.get(currentSet)) {
                if (layers.containsKey(layerName)) {
                    Layer layer = layers.get(layerName);
                    layer.drawLayer(viewMatrix, projectionMatrix);
                }
            }
        }
    }

    public void addMovable(Movable movable) {
        movables.add(movable);
    }

    public void move() {
        for (Movable movable : movables) {
            movable.move();
        }
    }

    public void collide(byte [] colMap, int scrH, float aspect, float [] viewMatr) {
        for (Movable movable : movables) {
            boolean col = movable.collideWithLandscape(colMap, scrH, aspect, viewMatr);
            if (col)
                movable.resetMotion();
        }
    }
}