package com.gameinstance.submarine;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gringo on 23.12.2016 20:47.
 *
 */
public class Animation {
    boolean repeat = false;
    List<Integer> frames = new ArrayList<>();
    int currentFrame = 0;
    int interval;
    Timer timer = null;
    boolean enabled = false;

    public Animation(int interval, boolean repeat, Integer ... texIds) {
        this.repeat = repeat;
        this.interval = interval;
        for (Integer texId : texIds) {
            frames.add(TextureManager.getTextureHandle(GameManager.getRenderer()
                    .getActivityContext(), texId));
        }
    }

    public Animation(int interval, Integer [] texHandles , boolean repeat) {
        this.repeat = repeat;
        this.interval = interval;
        for (Integer texHandle : texHandles) {
            frames.add(texHandle);
        }
    }

    public void setFrame(Sprite sprite, int num) {
        currentFrame = num;
        sprite.setTexHandle(frames.get(num));
    }

    public void play(final Sprite sprite) {
        enabled = true;
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                    if (!repeat) {
                        enabled = false;
                    }
                }
                setFrame(sprite, currentFrame);
                currentFrame++;
                if (!enabled) {
                    timer.cancel();
                }
            }
        }, 0, interval);
    }

    public void rawPlay(final Sprite sprite) {
        if (currentFrame >= frames.size()) {
            currentFrame = 0;
            if (!repeat) {
                enabled = false;
            }
        }
        setFrame(sprite, currentFrame);
        currentFrame++;
    }

    public void stop() {
        enabled = false;
    }
}
