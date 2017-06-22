package com.gameinstance.submarine;

import android.opengl.GLES20;

import com.gameinstance.submarine.ui.TextLine;
import com.gameinstance.submarine.utils.MathUtils;

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
    private boolean optimize;
    Float transparency = null;
    Integer bgrTexHandle = null;

    public Layer(int programHandle, boolean optimize) {
        this.programHandle = programHandle;
        this.optimize = optimize;
    }

    public void addSprite(final Sprite sprite) {
        if (!sprites.contains(sprite))
            GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                @Override
                public void run() {
                    sprites.add(sprite);
                }
            });
    }

    public void removeSprite(final Sprite sprite) {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                sprites.remove(sprite);
            }
        });
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
            if (color == null && transparency == null && bgrTexHandle == null) {
                for (Sprite sprite : sprites) {
                    if (!optimize || inScreen(viewMatrix, projectionMatrix, sprite)) {
                        sprite.draw(viewMatrix, projectionMatrix, programHandle);
                    }
                }
            } else if (transparency == null && bgrTexHandle == null) {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix, color, programHandle);
                }
            } else if (transparency == null) {
                GLES20.glUniform1i(GameManager.getRenderer().getmTexBgrHandle(), 1);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bgrTexHandle);
                for (Sprite sprite : sprites) {
                    if (!optimize || inScreen(viewMatrix, projectionMatrix, sprite)) {
                        sprite.draw(viewMatrix, projectionMatrix, programHandle, bgrTexHandle, 0);
                    }
                }
            } else {
                for (Sprite sprite : sprites) {
                    sprite.draw(viewMatrix, projectionMatrix, sprite.getTransparency(), programHandle);
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

    public void reinitText() {
        for (TextLine textLine : textLines) {
            textLine.reinit();
        }
    }

    private boolean inScreen(float [] viewMatrix, float [] projectionMatrix, Sprite sprite) {
        float [] screenCenter = new float[] {-viewMatrix[12], -viewMatrix[13]};
        float scrW = 2.1f / projectionMatrix[0];
        float scrH = 2.1f / projectionMatrix[5];
        return MathUtils.testQuads(screenCenter, sprite.getPosition(), scrW, scrH, sprite.getScaleX(),
                sprite.getScaleY());
    }

    public void setTransparency(float t) {
        transparency = t;
    }

    public void setBgrTexHandle(int handle) {
        bgrTexHandle = handle;
    }
}