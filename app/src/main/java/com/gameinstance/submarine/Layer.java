package com.gameinstance.submarine;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gringo on 11.12.2016 20:33.
 *
 */
public class Layer {
    int programHandle;
    List<Sprite> sprites = new ArrayList<>();
    float [] color = null;

    public Layer(int programHandle) {
        this.programHandle = programHandle;
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public void addSprites(List<Sprite> sprites) {
        this.sprites.addAll(sprites);
    }

    public void drawLayer(float [] viewMatrix, float [] projectionMatrix) {
        GLES20.glUseProgram(programHandle);
        if (sprites != null) {
            if (color == null) {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix);
                }
            } else {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix, color);
                }
            }
        }
    }

    public void setColor(float [] color) {
        this.color = color;
    }
}