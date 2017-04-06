package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Sprite;

/**
 * Created by gringo on 03.04.2017 20:10.
 *
 */
public class Marker {
    Sprite radarMarker;
    Sprite landMarker;
    float scale;
    float radarRadius;
    boolean showOnLand;
    float [] pos;

    public Marker(float [] pos, boolean showOnLand) {
        this.showOnLand = showOnLand;
        this.pos = pos;
        scale = GameManager.getRadarScale();
        radarRadius = 1.0f / scale;
        radarMarker = GameManager.addSprite(R.drawable.marker, pos[0] * scale, pos[1] * scale, 0.3f, 0.3f);
        if (showOnLand) {
            landMarker = GameManager.addSprite(R.drawable.marker, pos[0], pos[1], 0.1f, 0.1f);
        }
    }

    public void add() {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                GameManager.getScene().getLayer("radarhud").addSprite(radarMarker);
                if (showOnLand)
                    GameManager.getScene().getLayer("submarines").addSprite(landMarker);
            }
        });

    }

    public void remove() {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                GameManager.getScene().getLayer("radarhud").removeSprite(radarMarker);
                if (showOnLand)
                    GameManager.getScene().getLayer("submarines").removeSprite(landMarker);
            }
        });

    }

    public void update() {
        float [] radarViewMatrix = GameManager.getScene().getLayerset("Radar").viewMatrix;
        float subX = -radarViewMatrix[12];
        float subY = -radarViewMatrix[13];
        float posX = (pos[0] - subX);
        float posY = (pos[1] - subY);
        float r = 1 / scale - 0.15f;
        if (posX > r)
            posX = r;
        else if (posX < -r)
            posX = -r;
        if (posY > r)
            posY = r;
        if (posY < -r)
            posY = -r;
        radarMarker.setPosition(posX, posY);
    }

    public void setPosition(float [] pos) {
        this.pos = pos;
        if (showOnLand)
            landMarker.setPosition(pos[0], pos[1]);
    }
}
