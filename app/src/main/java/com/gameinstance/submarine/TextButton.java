package com.gameinstance.submarine;

import com.gameinstance.submarine.ui.TextLine;

import java.util.Map;

/**
 * Created by gringo on 27.12.2016 19:43.
 *
 */
public class TextButton {
    Button button;
    TextLine textLine;

    public TextButton(float x, float y, float w, float h, int [] texIds, String caption, float textSize,
                      Map<Integer, Primitive> primitives, Button.ClickListener clickListener,
                      int zOrder) {
        button = new Button(GameManager.getRenderer(), texIds, primitives, w, h, clickListener,
                new float[] {x, y});
        float [] textPos = new float[2];
        textPos[0] = x - (caption.length() / 2) * textSize * TextLine.getCharAspect() *
            TextLine.getCharAspect();
        textPos[1] = y;
        textLine = new TextLine(caption, textPos, textSize, GameManager.getRenderer());
        button.setZOrder(zOrder);
    }

    public void addToLayer(Layer layer) {
        layer.addSprite(button);
        layer.addTextline(textLine);
    }
}
