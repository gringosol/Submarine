package com.gameinstance.submarine.ui;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gringo on 29.08.2017 8:13.
 *
 */
public class BriefWindow {
    Sprite backGroundSprite;
    Sprite iconSprite;
    List<TextLine> textLines;
    List<String> messages;
    float charSpeed;
    float width;
    float height;
    int stringDelay;
    Timer charTimer;

    public BriefWindow(List<String> messages, float width, float height, Sprite icon,
                       int stringDelay, float charSpeed) {
        backGroundSprite = GameManager.addSprite(R.drawable.yellow, 0, 1 - height / 2.0f, width,
                height);
        iconSprite = icon;
        iconSprite.setPosition(0 - width / 2.0f + icon.getScaleX() / 2.0f, height);
        this.stringDelay = stringDelay;
        this.charSpeed = charSpeed;
        this.messages = messages;
        textLines = new ArrayList<>();
        for (String s : messages) {
            TextLine tl = new TextLine(s, new float[]{0 - width / 2.0f + icon.getScaleX() / 2.0f,
                    height - 0.2f}, 0.2f, GameManager.getRenderer(), 10);
            tl.setVisibleChars(0);
            textLines.add(tl);
        }
        charTimer = new Timer();
    }

    public void show() {
        GameManager.getScene().getLayer("hud").addSprite(backGroundSprite);
        GameManager.getScene().getLayer("hud").addSprite(iconSprite);
        GameManager.getScene().showText(textLines.get(0), "hud");
        charTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int currentCharCount = textLines.get(0).getVisibleChars();
                if (currentCharCount < textLines.get(0).letters.length) {
                    textLines.get(0).setVisibleChars(currentCharCount + 1);
                }
            }
        }, 0, (int)(1000 / charSpeed));
    }

    public void setMessages(List<String> messages) {

    }
}
