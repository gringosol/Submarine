package com.gameinstance.submarine.gameplay.tasks;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.List;

/**
 * Created by gringo on 27.05.2017 13:51.
 *
 */
public class GoToPointByRouteTask extends MobTask {
    List<float []> points;
    int targetIndex = 0;

    public GoToPointByRouteTask(Movable mob, float[] src, float [] dest) {
        super(mob);
        int [] map = GameManager.getPathMap();
        int width = GameManager.getPathWidth();
        int height = GameManager.getPathHeight();
        float cellSize = GameManager.getPathCellSize();
        if (map != null) {
            points = MathUtils.getPath(src == null ? mob.getSprite().getPosition() : src,
                    dest, map, width, height, cellSize);
            if (points != null && points.size() > 0) {
                mob.setTarget(points.get(targetIndex));
            }
        }
    }

    @Override
    public void run() {
        if (points != null && points.size() > 0) {
            float dist = MathUtils.distance(points.get(targetIndex), mob.getSprite().getPosition());
            if (dist < 0.1f && targetIndex < points.size() - 1) {
                targetIndex = targetIndex + 1;
                mob.setTarget(points.get(targetIndex));
                if (targetIndex == points.size() - 1) {
                    onComplete();
                }
            }
        }
    }

    @Override
    public void onRestore() {
        mob.setTarget(points.get(targetIndex));
    }

    public void onComplete() {

    }
}
