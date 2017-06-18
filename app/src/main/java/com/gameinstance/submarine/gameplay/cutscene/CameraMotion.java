package com.gameinstance.submarine.gameplay.cutscene;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.GameRenderer;
import com.gameinstance.submarine.gameplay.cutscene.CutsceneAction;
import com.gameinstance.submarine.utils.MathUtils;

/**
 * Created by gringo on 16.06.2017 7:38.
 *
 */
public class CameraMotion implements CutsceneAction {
    boolean finished = false;
    float[] dest;
    float [] start;
    Float speed;
    float minDist = 99999.0f;
    float [] direction;
    GameRenderer renderer;
    long startTime;

    public CameraMotion(float[] dest, Float speed){
        this.dest = dest;
        this.speed = speed;
        renderer = GameManager.getRenderer();
    }

    @Override
    public void run() {
        if (!finished) {
            if (speed != null) {
                if (direction == null) {
                    start = renderer.getCamera().getPosition();
                    direction = MathUtils.getDirection(start, dest);
                    startTime = System.currentTimeMillis();
                }
                long curTime = System.currentTimeMillis();
                long time = curTime - startTime;
                float dist = speed * time / 1000.0f;
                float [] curPos = MathUtils.vecSum(start, MathUtils.vecMulFloat(direction, dist));
                renderer.getCamera().setPointTarget(curPos);
                float distLeft = MathUtils.distance(curPos, dest);
                if (distLeft < minDist) {
                    minDist = distLeft;
                }
                else {
                    finished = true;
                }
                if (distLeft < 0.01) {
                    finished = true;
                }
            } else {
                renderer.getCamera().setPointTarget(dest);
                finished = true;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
