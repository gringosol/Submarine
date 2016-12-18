package com.gameinstance.submarine.ui;

import com.gameinstance.submarine.GameRenderer;
import com.gameinstance.submarine.Primitive;
import com.gameinstance.submarine.Sprite;

import java.util.Map;

/**
 * Created by gringo on 18.12.2016 13:13.
 *
 */
public class Letter extends Sprite {
    char ch;

    public Letter(GameRenderer renderer, int texResourseId, Map<Integer, Primitive> primitives,
                  float width, float height, char ch) {
        super(renderer, texResourseId, primitives, width, height);
        this.ch = ch;
        setTexHandle(LetterGenerator.getCharTexId(ch, renderer));
    }
}
