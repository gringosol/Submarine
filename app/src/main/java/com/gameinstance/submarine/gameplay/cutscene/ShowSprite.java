package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.Sprite;

/**
 * Created by gringo on 18.06.2017 14:44.
 *
 */
public class ShowSprite implements CutsceneAction {
    boolean finished = false;
    Sprite sprite;
    Long startTime;
    int duration;
    float x;
    float y;

    public ShowSprite(Sprite sprite, int duration, float x, float y) {
        this.sprite = sprite;
        this.duration = duration;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        if (!finished && sprite != null) {
            if (startTime == null) {
                startTime = System.currentTimeMillis();
                sprite.setPosition(x, y);
                sprite.setVisible(true);
                if (sprite.getAnimation() != null) {
                    sprite.playAnimation();
                }
            } else {
                long curTime = System.currentTimeMillis();
                if (curTime - startTime > duration) {
                    sprite.setVisible(false);
                    finished = true;
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
