package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Sprite;

/**
 * Created by gringo on 18.06.2017 14:06.
 *
 */
public class SetCameraToSprite implements CutsceneAction {
    public boolean finished = false;
    Sprite target;

    public SetCameraToSprite(Sprite target) {
        this.target = target;
    }

    @Override
    public void run(){
        if (!finished) {
            GameManager.getRenderer().getCamera().setTarget(target);
            finished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
