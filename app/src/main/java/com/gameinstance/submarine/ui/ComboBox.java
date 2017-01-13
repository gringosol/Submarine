package com.gameinstance.submarine.ui;

import android.support.annotation.NonNull;

import com.gameinstance.submarine.Button;
import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Layer;
import com.gameinstance.submarine.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 28.12.2016 9:21.
 *
 */
public class ComboBox {
    Button leftButton;
    Button rightButton;
    List<TextLine> textLines = new ArrayList<>();
    int currentIndex = 0;
    Layer layer = null;
    List<Integer> values = new ArrayList<>();

    public ComboBox(float x, float y, float w, float h, @NonNull Integer [] values, float textSize, int order) {
        this.values = Arrays.asList(values);
        leftButton = new Button(GameManager.getRenderer(), new int[] {R.drawable.comboleft,
                R.drawable.comboleft1}, GameManager.getMovablePrimitiveMap(), h, h, new Button.ClickListener() {
            @Override
            public void onClick() {
                shiftLeft();
            }
        }, new float[] {x - w / 2.0f + h / 2.0f, y});
        leftButton.setZOrder(order);
        rightButton = new Button(GameManager.getRenderer(), new int[] {R.drawable.comboright,
                R.drawable.comboright1}, GameManager.getMovablePrimitiveMap(), h, h, new Button.ClickListener() {
            @Override
            public void onClick() {
                shiftRight();
            }
        }, new float[] {x + w / 2.0f - h / 2.0f, y});
        rightButton.setZOrder(order);
        for (Integer value : values) {
            String s = GameManager.getString(value);
            TextLine textLine = new TextLine(value, new float[] {x - (s.length() / 2) * textSize * TextLine.getCharAspect() *
                    TextLine.getCharAspect(), y}, textSize, GameManager.getRenderer());
            textLines.add(textLine);
        }
    }

    private void shiftLeft() {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                layer.removeTextLine(textLines.get(currentIndex));
                currentIndex--;
                if (currentIndex < 0)
                    currentIndex = textLines.size() - 1;
                layer.addTextline(textLines.get(currentIndex));
                onValueChange(getValue());
            }
        });

    }

    private void shiftRight() {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                layer.removeTextLine(textLines.get(currentIndex));
                currentIndex++;
                if (currentIndex >= textLines.size())
                    currentIndex = 0;
                layer.addTextline(textLines.get(currentIndex));
                onValueChange(getValue());
            }
        });

    }

    public void setValue(int value){
        int i = 0;
        for (Integer s : values){
            if (s == value) {
                layer.removeTextLine(textLines.get(currentIndex));
                currentIndex = i;
                layer.addTextline(textLines.get(currentIndex));
                break;
            }
            i++;
        }
        onValueChange(getValue());
    }

    public void addToLayer(Layer layer) {
        this.layer = layer;
        layer.addSprite(leftButton);
        layer.addSprite(rightButton);
        layer.addTextline(textLines.get(0));
    }

    public int getValue() {
        return values.get(currentIndex);
    }

    public void onValueChange(int newValue) {

    }

    public void setTimerInterval(int v) {
        leftButton.setTimerInterval(v);
        rightButton.setTimerInterval(v);
    }
}
