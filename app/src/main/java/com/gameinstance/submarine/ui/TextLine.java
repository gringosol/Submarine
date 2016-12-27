package com.gameinstance.submarine.ui;

import com.gameinstance.submarine.GameRenderer;
import com.gameinstance.submarine.Primitive;
import com.gameinstance.submarine.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gringo on 18.12.2016 13:12.
 *
 */
public class TextLine {
    Letter [] letters;
    static float charAspect = 0.7f;
    static Primitive primitive = null;

    public TextLine(String text, float [] pos, float lineHeight, GameRenderer renderer) {
        letters = new Letter[text.length()];
        float charWidth = lineHeight * charAspect;
        if (primitive == null)
            primitive = renderer.createPrimitiveTextured();
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int i = 0;
        for (char ch : text.toCharArray()) {
            letters[i] = new Letter(renderer, R.drawable.textbackground, primitiveMap, charWidth,
                    lineHeight, ch);
            letters[i].setPosition(pos[0] + i * charWidth * charAspect, pos[1]);
            i++;
        }
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, int programHandle) {
        for (Letter letter : letters) {
            letter.draw(viewMatrix, projectionMatrix, programHandle);
        }
    }

    public static float getCharAspect() {
        return charAspect;
    }
}
