package com.gameinstance.submarine.ui;

import com.gameinstance.submarine.GameManager;
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
    Integer resId = null;
    float [] pos;
    float lineHeight;
    GameRenderer renderer;

    public TextLine(int resId, float [] pos, float lineHeight, GameRenderer renderer) {
        this.resId = resId;
        this.pos = pos;
        this.lineHeight = lineHeight;
        this.renderer = renderer;
        initTexLine(resId, pos, lineHeight, renderer);
    }

    public void initTexLine(int resId, float [] pos, float lineHeight, GameRenderer renderer) {
        String text = GameManager.getString(resId);
        letters = new Letter[text.length()];
        float charWidth = lineHeight * charAspect;
        if (primitive == null)
            primitive = GameManager.getTexPrimitive();
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

    public void reinit() {
        if (resId != null) {
            initTexLine(resId, pos, lineHeight, renderer);
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
