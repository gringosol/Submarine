package com.gameinstance.submarine.gameplay.cutscene;

/**
 * Created by gringo on 18.06.2017 14:22.
 *
 */
public class Idle implements CutsceneAction {
    long duration;
    boolean finished = false;
    Long initTime;

    public Idle(int duration) {
        this.duration = duration;
    }

    @Override
    public void run() {
        if (!finished) {
            if (initTime == null) {
                initTime = System.currentTimeMillis();
            } else {
                long currentTime = System.currentTimeMillis();
                if (currentTime - initTime >= duration) {
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
