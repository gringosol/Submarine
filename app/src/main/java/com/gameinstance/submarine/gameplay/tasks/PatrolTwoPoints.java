package com.gameinstance.submarine.gameplay.tasks;

import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.utils.MathUtils;

/**
 * Created by gringo on 04.01.2017 17:10.
 *
 */
public class PatrolTwoPoints extends MobTask {
    float [] p1;
    float [] p2;
    float [] targ;

    public PatrolTwoPoints(Movable mob, float [] p1, float [] p2) {
        super(mob);
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run() {
        if (MathUtils.coordEquals(mob.getTarget(), p1)) {
            float dist = MathUtils.distance(p1, mob.getSprite().getPosition());
            if (dist < 0.1f) {
                mob.setTarget(p2);
                targ = p2;
            }
        }
        if (MathUtils.coordEquals(mob.getTarget(), p2)) {
            float dist = MathUtils.distance(p2, mob.getSprite().getPosition());
            if (dist < 0.1f) {
                mob.setTarget(p1);
                targ = p1;
            }
        }
    }

    //вызывается при загрузке состояния объекта
    @Override
    public void onRestore() {
        if (targ != null) {
            mob.setTarget(targ);
        }
    }
}
