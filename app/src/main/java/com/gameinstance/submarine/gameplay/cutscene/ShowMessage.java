package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.GameManager;

/**
 * Created by gringo on 18.06.2017 14:18.
 *
 */
public class ShowMessage implements CutsceneAction {
    int resource;
    float x;
    float y;
    int interval;
    boolean finished = false;

    public ShowMessage(int resource, float x, float y, int interval) {
        this.resource = resource;
        this.x = x;
        this.y = y;
        this.interval = interval;
    }

    @Override
    public void run () {
        if (!finished) {
            GameManager.showMessage(resource, x, y, interval);
            finished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
