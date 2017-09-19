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
    int currentLine = 0;
    boolean lineDelay = false;

    public BriefWindow(List<String> messages, float width, float height, Sprite icon,
                       int stringDelay, float charSpeed) {
        backGroundSprite = GameManager.addSprite(R.drawable.briefbackground, 0, 1 - height / 2.0f, width,
                height);
        iconSprite = icon;
        iconSprite.setScale(0.8f * height, 0.8f * height);
        iconSprite.setPosition(0 - width / 2.0f + icon.getScaleX() / 2.0f + 0.1f * height, 1.0f - height / 2.0f);
        this.stringDelay = stringDelay;
        this.charSpeed = charSpeed;
        this.messages = messages;
        textLines = new ArrayList<>();
        float textWidth = width - (icon.getScaleX() + 0.2f);
        for (String s : messages) {
            TextLine tl = new TextLine(s, new float[]{0 - width / 2.0f + iconSprite.getScaleX() + 0.2f,
                    0.87f}, 0.2f, GameManager.getRenderer(), textWidth);
            tl.setVisibleChars(0);
            textLines.add(tl);
        }
        charTimer = new Timer();
    }

    public void show() {
        if (currentLine == 0) {
            if (backGroundSprite.getVisible()) {
                GameManager.getScene().getLayer("hud").addSprite(backGroundSprite);
                GameManager.getScene().getLayer("hud").addSprite(iconSprite);
            } else {
                backGroundSprite.setVisible(true);
                iconSprite.setVisible(true);
            }
            if (iconSprite.getAnimation() != null) {
                iconSprite.getAnimation().play(iconSprite);
            }
        }
        GameManager.getScene().showText(textLines.get(currentLine), "hud");
        charTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!lineDelay) {
                    int currentCharCount = textLines.get(currentLine).getVisibleChars();
                    if (currentCharCount < textLines.get(currentLine).letters.length) {
                        textLines.get(currentLine).setVisibleChars(currentCharCount + 1);
                    } else if (currentCharCount == textLines.get(currentLine).letters.length) {
                        lineDelay = true;
                        if (iconSprite.getAnimation() != null) {
                            iconSprite.getAnimation().stop();
                        }
                        Timer lineTimer = new Timer();
                        lineTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                GameManager.getScene().hideText(textLines.get(currentLine), "hud");
                                currentLine++;
                                if (currentLine < textLines.size()) {
                                    lineDelay = false;
                                    show();
                                } else {
                                    backGroundSprite.setVisible(false);
                                    iconSprite.setVisible(false);
                                }
                            }
                        }, stringDelay);
                    }
                }
            }
        }, 0, (int)(1000 / charSpeed));
    }

    public void setMessages(List<String> messages) {

    }
}
