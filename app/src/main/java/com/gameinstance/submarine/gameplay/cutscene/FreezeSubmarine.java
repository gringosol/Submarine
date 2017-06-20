package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.GameManager;

/**
 * Created by gringo on 20.06.2017 7:40.
 *
 */
public class FreezeSubmarine implements CutsceneAction {
    boolean finished = false;
    boolean motionDenied;

    public FreezeSubmarine(boolean motionDenied) {
        this.motionDenied = motionDenied;
    }

    @Override
    public void run() {
        if (!finished) {
            GameManager.getSubmarineMovable().setMotionDenied(motionDenied);
            finished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
