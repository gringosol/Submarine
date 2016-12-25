package com.gameinstance.submarine;

import com.gameinstance.submarine.ui.TextLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gringo on 11.12.2016 20:02.
 *
 */
public class Scene {
    Map<String, Layerset> layerSets = new HashMap<>();
    Map<String, Layer> layers = new HashMap<>();
    List<Movable> movables = new ArrayList<>();
    GameRenderer renderer;
    String currentSet = null;

    public Scene(GameRenderer renderer){
        this.renderer = renderer;
    }

    public void addLayerSet(String setName, Layerset layerSet) {
        layerSets.put(setName, layerSet);
    }

    public void addLayer(String name, Layer layer) {
        layers.put(name, layer);
    }

    public void setLayerSet(String name) {
        if (layerSets.containsKey(name))
            currentSet = name;
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, float [] guiViewMatrix) {
        if (currentSet != null) {
            for (String layerName : layerSets.get(currentSet).layerNames) {
                if (layers.containsKey(layerName)) {
                    Layer layer = layers.get(layerName);
                    if (layer.isGui) {
                        layer.drawLayer(guiViewMatrix, projectionMatrix);
                    } else {
                        layer.drawLayer(viewMatrix, projectionMatrix);
                    }
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

    public void showText(TextLine textLine, String layer) {
        if (layers.containsKey(layer)) {
            layers.get(layer).addTextline(textLine);
        }
    }

    public void hideText(TextLine textLine, String layer) {
        if (layers.containsKey(layer)) {
            layers.get(layer).removeTextLine(textLine);
        }
    }

    public Layer getLayer(String layerName) {
        return layers.get(layerName);
    }

    public Map<String, Layer> getLayers() {
        return layers;
    }

    public Map<String, Layerset> getLayerSets() {
        return layerSets;
    }
}