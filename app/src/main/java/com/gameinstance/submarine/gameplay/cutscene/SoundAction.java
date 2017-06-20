package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.GameManager;

/**
 * Created by gringo on 20.06.2017 7:36.
 *
 */
public class SoundAction implements CutsceneAction {
    boolean finished = false;
    int soundId;
    boolean repeat;

    public SoundAction(int soundId, boolean repeat) {
        this.soundId = soundId;
        this.repeat = repeat;
    }

    @Override
    public void run() {
        if (!finished) {
            GameManager.getSoundManager().playSound(soundId, repeat);
            finished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
