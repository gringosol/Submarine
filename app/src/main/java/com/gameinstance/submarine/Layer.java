package com.gameinstance.submarine;

import android.opengl.GLES20;

import com.gameinstance.submarine.ui.TextLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gringo on 11.12.2016 20:33.
 *
 */
public class Layer {
    int programHandle;
    List<Sprite> sprites = new ArrayList<>();
    List<TextLine> textLines = new ArrayList<>();
    float [] color = null;
    public boolean isGui = false;
    public boolean visible = true;

    public Layer(int programHandle) {
        this.programHandle = programHandle;
    }

    public void addSprite(Sprite sprite) {
        if (!sprites.contains(sprite))
            sprites.add(sprite);
    }

    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }

    public void addSprites(List<Sprite> sprites) {
        this.sprites.addAll(sprites);
    }

    public void addTextline(TextLine textLine) {
        if (!textLines.contains(textLine))
          textLines.add(textLine);
    }

    public void  removeTextLine(TextLine textLine) {
        textLines.remove(textLine);
    }

    public void drawLayer(float [] viewMatrix, float [] projectionMatrix) {
        GLES20.glUseProgram(programHandle);
        if (sprites != null) {
            if (color == null) {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix, programHandle);
                }
            } else {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix, color, programHandle);
                }
            }
        }
        for (TextLine textLine : textLines) {
            textLine.draw(viewMatrix, projectionMatrix, programHandle);
        }
    }

    public void setColor(float [] color) {
        this.color = color;
    }

    public void clear(){
        sprites.clear();

    }
}