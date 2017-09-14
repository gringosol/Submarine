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
    int lineLength;
    int visibleChars = -1;

    public TextLine(int resId, float[] pos, float lineHeight, GameRenderer renderer, int lineLength) {
        this.resId = resId;
        this.pos = pos;
        this.lineHeight = lineHeight;
        this.renderer = renderer;
        String text = GameManager.getString(resId);
        initTexLine(text, pos, lineHeight, renderer, lineLength);
        this.lineLength = lineLength;
    }

    public TextLine(int resId, float[] pos, float lineHeight, GameRenderer renderer) {
        this(resId, pos, lineHeight, renderer, 80);
    }

    public TextLine(String text, float[] pos, float lineHeight, GameRenderer renderer, int lineLength) {
        this.pos = pos;
        this.lineHeight = lineHeight;
        this.renderer = renderer;
        initTexLine(text, pos, lineHeight, renderer, lineLength);
        this.lineLength = lineLength;
    }

    public TextLine(String text, float[] pos, float lineHeight, GameRenderer renderer) {
        this(text, pos, lineHeight, renderer, 80);
    }

    public void initTexLine(String text, float [] pos, float lineHeight, GameRenderer renderer,
                            int lineLength) {

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
            letters[i].setPosition(pos[0] + (i % lineLength) * charWidth * charAspect, pos[1]
                    - lineHeight * 1.2f * (i / lineLength));
            i++;
        }
    }

    public void reinit() {
        if (resId != null) {
            String text = GameManager.getString(resId);
            initTexLine(text, pos, lineHeight, renderer, lineLength);
        }
    }

    public void draw(float [] viewMatrix, float [] projectionMatrix, int programHandle) {
        int i = 0;
        for (Letter letter : letters) {
            letter.draw(viewMatrix, projectionMatrix, programHandle);
            i++;
            if (visibleChars > 0 && i >= visibleChars) {
                break;
            }
        }
    }

    public static float getCharAspect() {
        return charAspect;
    }

    public void setVisibleChars(int n) {
        visibleChars = n;
    }

    public int getVisibleChars() {
        return visibleChars;
    }
}
