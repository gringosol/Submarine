package com.gameinstance.submarine.ui;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Layer;
import com.gameinstance.submarine.Primitive;
import com.gameinstance.submarine.Button;

import java.util.Map;

/**
 * Created by gringo on 27.12.2016 19:43.
 *
 */
public class TextButton {
    Button button;
    TextLine textLine;

    public TextButton(float x, float y, float w, float h, int [] texIds, Integer captionId, float textSize,
                      Map<Integer, Primitive> primitives, Button.ClickListener clickListener,
                      int zOrder) {
        button = new Button(GameManager.getRenderer(), texIds, primitives, w, h, clickListener,
                new float[] {x, y});
        float [] textPos = new float[2];
        String s = GameManager.getString(captionId);
        textPos[0] = x - (s.length() / 2) * textSize * TextLine.getCharAspect() *
            TextLine.getCharAspect();
        textPos[1] = y;
        textLine = new TextLine(captionId, textPos, textSize, GameManager.getRenderer());
        button.setZOrder(zOrder);
    }

    public void addToLayer(Layer layer) {
        layer.addSprite(button);
        layer.addTextline(textLine);
    }
}
