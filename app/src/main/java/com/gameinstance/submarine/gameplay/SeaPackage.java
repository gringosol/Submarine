package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.utils.MathUtils;

/**
 * Created by gringo on 02.06.2017 15:17.
 *
 */
public class SeaPackage {
    Sprite sprite;
    Runnable action;
    float [] pos;
    Movable targetMovable;

    public SeaPackage(float [] pos, int texId, Runnable action, float w, float h, Movable targetMovable) {
        this.pos = pos;
        this.action = action;
        this.targetMovable = targetMovable;
        sprite = GameManager.addSprite(texId, pos[0], pos[1], w, h);
    }

    public SeaPackage(float [] pos, int texId, Runnable action) {
        this(pos, texId, action, 0.15f, 0.15f, null);
    }

    public void update() {
        if (targetMovable == null) {
            targetMovable = GameManager.getSubmarineMovable();
        }
        if (sprite.getVisible()) {
            float dist = MathUtils.distance(pos, targetMovable.getSprite().getPosition());
            if (dist < 0.2f) {
                sprite.setVisible(false);
                if (action != null) {
                    action.run();
                }
            }
        }
    }
}
