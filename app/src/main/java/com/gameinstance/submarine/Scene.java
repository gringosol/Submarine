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
                    if (layer.visible) {
                        if (layer.isGui) {
                            layer.drawLayer(guiViewMatrix, projectionMatrix);
                        } else {
                            layer.drawLayer(viewMatrix, projectionMatrix);
                        }
                    }
                }
            }
        }
    }

    public void addMovable(Movable movable) {
        if (!movables.contains(movable))
            movables.add(movable);
    }

    public void removeMovable(Movable movable){
        movables.remove(movable);
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

    public List<Movable> getMovables() {
        return movables;
    }

    public void reinitText() {
        for(Map.Entry<String, Layer> entry : layers.entrySet()) {
            entry.getValue().reinitText();
        }
    }

    public List<Ship> getShips() {
        List<Ship> ships = new ArrayList<>();
        for (Movable movable : getMovables()) {
            if (movable instanceof Ship)
                ships.add((Ship)movable);
        }
        return ships;
    }

    public List<Tank> getTanks() {
        List<Tank> tanks = new ArrayList<>();
        for (Movable movable : getMovables()) {
            if (movable instanceof Tank)
                tanks.add((Tank)movable);
        }
        return tanks;
    }

    public List<Helicopter> getHelis() {
        List<Helicopter> helicopters = new ArrayList<>();
        for (Movable movable : getMovables()) {
            if (movable instanceof Helicopter)
                helicopters.add((Helicopter)movable);
        }
        return helicopters;
    }

    public Layerset getLayerset(String name) {
        return layerSets.get(name);
    }

    public void deactivateMovables() {
        if (getMovables() == null)
            return;
        for (Movable movable : getMovables()) {
            movable.deactivate();
        }
    }
}