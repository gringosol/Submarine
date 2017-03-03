package com.gameinstance.submarine.gameplay.tasks;

import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.List;

/**
 * Created by gringo on 03.03.2017 20:10.
 *
 */
public class PatrolPoints extends MobTask {
    List<float []> points;
    int targetIndex = 0;

    public PatrolPoints(Movable mob, List<float []> points) {
        super(mob);
        this.points = points;
    }

    @Override
    public void run() {
        float dist = MathUtils.distance(points.get(targetIndex), mob.getSprite().getPosition());
        if (dist < 0.1f) {
            targetIndex = (targetIndex + 1) % points.size();
            mob.setTarget(points.get(targetIndex));
        }
    }

    //вызывается при загрузке состояния объекта
    @Override
    public void onRestore() {
        mob.setTarget(points.get(targetIndex));
    }
}
